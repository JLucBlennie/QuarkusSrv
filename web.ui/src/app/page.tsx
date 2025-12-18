"use client";

import { ThemeToggle } from "@/components/ui/theme-toggle";
import WebSocketNotificationListener from "@/components/WebSocketNotificationListener";
import { useState } from "react";

import EvenementsList from "@/components/EvenementsList";
import pack from "../../package.json";

export default function Home() {
  const [notImplementedAlertVisible, setNotImplementedAlertVisible] = useState(false);

return (
  <div className="relative min-h-screen bg-logo-35op bg-no-repeat bg-center bg-contain ">
    <div className="w-full h-full absolute top-0 left-0">
      <h1 className="text-5xl font-bold text-center">{pack.name}</h1>
      <div className="relative p-5">
        <EvenementsList />
      </div>
    </div>

    {/* Upper Right  Box */}
    <div className="absolute top-5 right-20 transform bg-slate-900 bg-opacity-50 text-white p-2 w-max rounded shadow-lg z-10">
      <p className="text-xs font-bold">Version {pack.version}</p>
    </div>
    <div className="absolute top-2 right-5 transform bg-slate-900 bg-opacity-50 text-white p-2 w-max rounded shadow-lg z-10">
      <ThemeToggle />
    </div>

    {/* Lower Left Box 
      <div className="absolute bottom-5 left-5 transform bg-slate-900 bg-opacity-50 text-white p-4 rounded shadow-lg z-10">
        <h2 className="text-1xl font-bold mb-4">Version {pack.version}</h2>
      </div>
      */}

    {/* Lower Right Box 
      <div className="absolute bottom-5 right-5 transform bg-slate-900 bg-opacity-50 text-white p-4 rounded shadow-lg z-10">
        <h2 className="text-1xl font-bold mb-4">Boite en bas à droite</h2>
      </div>
      */}

    {/* Right Box 
      <div className="absolute right-5 top-1/2 transform -translate-y-1/2 bg-slate-900 bg-opacity-50 text-white p-4 rounded shadow-lg h-1/2 max-w-xs z-10">
        <h2 className="text-1xl font-bold mb-4">
          Boite droite
        </h2>
      </div>
      */}

    {/* Left Box 
      <div className="absolute left-5 top-1/2 transform -translate-y-1/2 bg-slate-900 bg-opacity-50 text-white p-4 rounded shadow-lg h max-w z-10">
        <h2 className="text-1xl font-bold mb-4">Liste des Plateformes</h2>
      </div>
      */}

    {/* Bottom Box 
      <div className="absolute bottom-5 left-1/2 transform -translate-x-1/2 bg-slate-900 bg-opacity-50 text-white p-4 rounded shadow-lg w-full max-w-2xl z-10">
        <h2 className="text-1xl font-bold mb-4">Gestionnaire d'éditeurs</h2>
      </div>
      */}

    {/*
      <AlertDialogNotImplemented
        open={notImplementedAlertVisible}
        onOpenChange={setNotImplementedAlertVisible}
      />
      */}

    <WebSocketNotificationListener url="ws://localhost:9090/ws" />
  </div>
);
}
