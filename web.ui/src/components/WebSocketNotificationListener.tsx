"use client"

import { Fragment, useEffect, useState } from "react"
import ProgressCard from "./ProgressCard";
import { toast } from "./ui/use-toast"

export default function WebSocketNotificationListener(props: { url: any; }) {

    const [mapProgress, setMapProgress] = useState(new Map<string, any>());
    const [blocking, setBlocking] = useState(false);

    const addEntry = (key: string, value: any) => {
      setMapProgress(prevMap => {
        const newMap = new Map(prevMap);
        newMap.set(key, value);
        return newMap;
      });
    };

    const removeEntry = (key: string) => {
      setMapProgress(prevMap => {
        const newMap = new Map(prevMap);
        newMap.delete(key);
        return newMap;
      });
    };

    const getPosition = (key: string): number | undefined => {
      const entriesArray = Array.from(mapProgress.entries());
      const index = entriesArray.findIndex(([mapKey]) => mapKey === key);
      return index !== -1 ? index : undefined;
    };

    useEffect(() => {
        const ws = new WebSocket(props.url);

        ws.addEventListener("message", (event) => {
          console.log("Message from server ", event.data);
          handleMessage(event.data);
        });

        return () => {
          ws.close();
        };
      }, []);

      const handleMessage = (incomingMessage: string) => {
        
        const message = JSON.parse(incomingMessage);

        if(message.type == "PROGRESSBLOCKING") {
          if(message.progressValue === 100) {
            setBlocking(false)
          } else {
            setBlocking(true)
          }
        }

        if(message.type == "PROGRESS" || message.type == "PROGRESSBLOCKING") {
          addEntry(message.processKey, message)

          if(message.progressValue === 100) {
            setTimeout(() => { removeEntry(message.processKey)}, 1000)
          }
          
        } else if(message.type == "INFO") {
          toast({
            title: (<>{message.title}</>),
            description: (<>{message.message}</>)
          })
        } else if(message.type == "ERROR") {
          toast({
            title: (<>{message.title}</>),
            variant: "destructive",
            description: (<span>{message.message}</span>),
          })
        } //else {
          toast({
            title: "Websocket message received:",
            description: (
              <pre className="mt-2 w-[340px] rounded-md bg-slate-950 p-4">
                <code className="text-white">{JSON.stringify(incomingMessage, null, 2)}</code>
              </pre>
            ),
          })
        //}
      };

      //

      return (
        <>
        <div className="absolute left-5 transform -translate-y-1/2 top-1/2 w-1/6">
          {Array.from(mapProgress.entries()).slice(0,4).map(([key, value]) => (
            <ProgressCard message={value.message == "" ? value.title : value.message} key={key} progress={value.progressValue} />
          ))}
        </div>
          <div
        className={`fixed top-0 left-0 w-full h-full flex items-center justify-center bg-gray-500 bg-opacity-50 z-40 ${
          blocking ? 'flex' : 'hidden'
        }`}
      />
      </>
      );
}