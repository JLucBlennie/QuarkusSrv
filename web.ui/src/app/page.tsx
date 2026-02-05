"use client";

import { ThemeToggle } from "@/components/ui/theme-toggle";
import WebSocketNotificationListener from "@/components/WebSocketNotificationListener";

import { ClubStructureList } from "@/components/ClubStructureList";
import { DemandeurList } from "@/components/DemandeurList";
import { EvenementsList } from "@/components/EvenementsList";
import { MoniteursList } from "@/components/MoniteursList";
import { ThreeColumnsLayout } from "@/components/ThreeColumnsLayout";
import { TypeEvenementList } from "@/components/TypeEvenementList";
import { Button } from "@/components/ui/button";
import Tabs from "@/components/ui/tabs";
import { SERVER_URL } from "@/lib/constants";
import { FaArrowRotateLeft } from "react-icons/fa6";
import pack from "../../package.json";

export default function Home() {

  function handleUpdateClick() {
    console.log('Mise à jour de la BDD locale depuis le serveur Quarkus...');
    fetch(`${SERVER_URL}/ctr/evenements/updatebdd/`, {
      method: "GET",
      redirect: "follow",
    })
      .then((res) => {
        if (!res.ok) {
          throw new Error(`Erreur serveur : ${res.status}`);
        }
        return res;
      });
  }

  return (
    <div className="relative min-h-screen bg-logo-35op bg-no-repeat bg-center bg-contain ">
      <div className="w-full h-full absolute top-0 left-0">
        <h1 className="text-5xl font-bold text-center">Calendrier de la CTR</h1>
        <div className="relative p-5">
          <Tabs>
            {/* Onglet 1 : Liste des événements */}
            <Tabs.Tab label="Événements">
              <div>
                <h2 className="text-xl font-semibold mb-4">Liste des événements</h2>
                <EvenementsList />
              </div>
            </Tabs.Tab>

            {/* Onglet 2 : Statistiques */}
            <Tabs.Tab label="Moniteurs">
              <div>
                <h2 className="text-xl font-semibold mb-4">Liste des Moniteurs</h2>
                <MoniteursList />
              </div>
            </Tabs.Tab>

            {/* Onglet 3 : Paramètres */}
            <Tabs.Tab label="Paramètres">
              <div>
                <h2 className="text-xl font-semibold mb-4">Gestion des paramètres</h2>
                <ThreeColumnsLayout
                  left={<ClubStructureList />}
                  center={<DemandeurList />}
                  right={<TypeEvenementList />} />
              </div>
            </Tabs.Tab>
          </Tabs>
        </div>
      </div>

      {/* Upper Right  Box */}
      <div className="absolute top-5 right-20 transform bg-slate-900 bg-opacity-50 text-white p-2 w-max rounded shadow-lg z-10">
        <p className="text-xs font-bold">{pack.name}<br />Version {pack.version}</p>
      </div>
      <div className="absolute top-2 right-5 transform bg-slate-900 bg-opacity-50 text-white p-2 w-max rounded shadow-lg z-10">
        <ThemeToggle />
      </div>

      {/* Lower Left Box */}
      <div className="absolute bottom-5 left-5 transform bg-slate-900 bg-opacity-50 text-white p-4 rounded shadow-lg z-10">
        <Button className="flex items-center justify-center w-12 h-12 bg-blue-600 text-white rounded-full shadow-lg hover:bg-blue-700 transition-colors" onClick={handleUpdateClick}>
          <FaArrowRotateLeft className="h-6 w-6" />
        </Button>
      </div>

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

      <WebSocketNotificationListener url="ws://localhost:9090/ws" />
    </div>
  );
}
