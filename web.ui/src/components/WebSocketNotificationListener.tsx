"use client"

import { useAuth } from "@/context/AuthContext";
import { getToken } from "@/lib/authService";
import { useEffect, useRef, useState } from "react";
import ProgressCard from "./ProgressCard";
import { toast } from "./ui/use-toast";

type MessageType = {
  type: string;
  message: string;
  processKey: string;
  title: string;
  progressValue: number;
}

const RECONNECT_DELAY_MS = 3000;

export default function WebSocketNotificationListener(props: { url: string }) {
  const { isAuthenticated, isLoading } = useAuth();
  const [mapProgress, setMapProgress] = useState(new Map<string, any>());
  const [blocking, setBlocking] = useState(false);
  const wsRef = useRef<WebSocket | null>(null);
  const reconnectTimer = useRef<ReturnType<typeof setTimeout> | null>(null);
  const manualCloseRef = useRef(false);

  const addEntry = (key: string, value: any) => {
    setMapProgress(prevMap => new Map(prevMap).set(key, value));
  };

  const removeEntry = (key: string) => {
    setMapProgress(prevMap => {
      const newMap = new Map(prevMap);
      newMap.delete(key);
      return newMap;
    });
  };

  useEffect(() => {
    if (isLoading || !isAuthenticated) return;

    manualCloseRef.current = false;

    function connect() {
      const token = getToken();
      if (!token) return;

      const ws = new WebSocket(`${props.url}?token=${encodeURIComponent(token)}`);
      wsRef.current = ws;

      ws.addEventListener("message", (event) => handleMessage(event.data));

      ws.addEventListener("close", () => {
        if (!manualCloseRef.current) {
          reconnectTimer.current = setTimeout(connect, RECONNECT_DELAY_MS);
        }
      });

      ws.addEventListener("error", () => ws.close());
    }

    connect();

    return () => {
      manualCloseRef.current = true;
      if (reconnectTimer.current) clearTimeout(reconnectTimer.current);
      wsRef.current?.close();
    };
  }, [isAuthenticated, isLoading, props.url]);

  const handleMessage = (incomingMessage: string) => {
    const message: MessageType = JSON.parse(incomingMessage);

    if (message.type === "PROGRESSBLOCKING") {
      setBlocking(message.progressValue !== 100);
    }
    if (message.type === "PROGRESS" || message.type === "PROGRESSBLOCKING") {
      addEntry(message.processKey, message);
      if (message.progressValue === 100) {
        setTimeout(() => removeEntry(message.processKey), 1000);
      }
    } else if (message.type === "INFO") {
      toast({ title: message.title, description: <>{message.message}</> });
    } else if (message.type === "ERROR") {
      toast({ title: message.title, variant: "destructive", description: <span>{message.message}</span> });
    } else {
      toast({
        title: "Websocket message received:",
        description: (
          <pre className="mt-2 w-[340px] rounded-md bg-slate-950 p-4">
            <code className="text-white">{JSON.stringify(incomingMessage, null, 2)}</code>
          </pre>
        ),
      });
    }
  };

  return (
    <>
      <div className="absolute left-1/2 transform -translate-y-1/2 top-1/2">
        {Array.from(mapProgress.entries()).slice(0, 4).map(([key, value]) => (
          <ProgressCard message={value.message === "" ? value.title : value.message} key={key} progressKey={key} progress={value.progressValue} />
        ))}
      </div>
      <div className={`fixed top-0 left-0 w-full h-full flex items-center justify-center bg-gray-500 bg-opacity-50 z-40 ${blocking ? 'flex' : 'hidden'}`} />
    </>
  );
}