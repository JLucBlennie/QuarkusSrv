"use client";

import { ClubStructureList } from "@/components/ClubStructureList";
import { DemandeurList } from "@/components/DemandeurList";
import { EvenementsList } from "@/components/EvenementsList";
import { MoniteursList } from "@/components/MoniteursList";
import { ThreeColumnsLayout } from "@/components/ThreeColumnsLayout";
import { TypeEvenementList } from "@/components/TypeEvenementList";
import { Button } from "@/components/ui/button";
import Tabs from "@/components/ui/tabs";
import { ThemeToggle } from "@/components/ui/theme-toggle";
import WebSocketNotificationListener from "@/components/WebSocketNotificationListener";
import { useAuth } from "@/context/AuthContext"; // ← nouveau
import { authFetch } from "@/lib/authService"; // ← nouveau
import { SERVER_URL, WS_URL } from "@/lib/constants";
import { LogOut } from "lucide-react";
import Link from "next/link";
import { useEffect } from "react";
import { FaArrowRotateLeft } from "react-icons/fa6";
import pack from "../../package.json";

export default function Home() {
  const { user, logout, hasRole } = useAuth();  // ← accès à l'utilisateur connecté

  useEffect(() => {
    if (user?.username) {
      document.title = `Calendrier CTR — ${user.username}`;
    }
  }, [user]);

  function handleUpdateClick() {
    // Plus besoin de login() ici, le token est déjà disponible !
    authFetch(`${SERVER_URL}/evenements/updatebdd/`, {
      method: "GET",
      redirect: "follow",
    })
      .then((res) => {
        if (!res.ok) throw new Error(`Erreur serveur : ${res.status}`);
        console.log("Mise à jour effectuée avec succès");
      })
      .catch((err) => {
        console.error("Erreur lors de la mise à jour :", err);
        alert(`Erreur lors de la mise à jour : ${err.message}`);
      });
  }

  return (
    <div className="relative min-h-screen bg-logo-35op bg-no-repeat bg-center bg-contain">
      <div className="w-full h-full absolute top-0 left-0">
        <h1 className="text-5xl font-bold text-center">Calendrier de la CTR</h1>
        <div className="relative p-5">
          <Tabs>
            {hasRole("admin") && (
              <Tabs.Tab label="Événements">
                <div className="w-full h-full overflow-auto">
                  <Link href="/evenements" className="px-4 py-2 bg-blue-600 text-white rounded-md text-sm font-medium hover:bg-blue-700 transition-colors" >Ouvrir les événements</Link>
                </div>
              </Tabs.Tab>
            )}

            {user?.username !== 'ctr' && (
              <Tabs.Tab label="Mes Événements">
                <div className="w-full h-full overflow-auto">
                  <EvenementsList mesEvenementsOnly={true} />
                </div>
              </Tabs.Tab>
            )}

            {hasRole("admin") && (
              <Tabs.Tab label="Moniteurs">
                <div>
                  <h2 className="text-xl font-semibold mb-4">Liste des Moniteurs</h2>
                  <MoniteursList />
                </div>
              </Tabs.Tab>
            )}
            {hasRole("admin") && (
              <Tabs.Tab label="Paramètres">
                <div>
                  <h2 className="text-xl font-semibold mb-4">Gestion des paramètres</h2>
                  <ThreeColumnsLayout
                    left={<ClubStructureList />}
                    center={<DemandeurList />}
                    right={<TypeEvenementList />}
                  />
                </div>
              </Tabs.Tab>
            )}
          </Tabs>
        </div>
      </div>

      {/* Version */}
      <div className="absolute top-5 right-20 bg-slate-900 bg-opacity-50 text-white p-2 w-max rounded shadow-lg z-10">
        <p className="text-xs font-bold">{pack.name}<br />Version {pack.version}</p>
      </div>

      <div className="absolute top-2 right-5 bg-slate-900 bg-opacity-50 text-white p-2 w-max rounded shadow-lg z-10">
        <ThemeToggle />
        <button
          onClick={logout}
          title={`Déconnexion (${user?.username})`}
          className="text-red-400 hover:text-red-300 transition-colors"
        >
          <LogOut className="h-5 w-5" />
        </button>
      </div>
      {hasRole("admin") && (
        <div className="absolute top-20 right-5 bg-slate-900 bg-opacity-50 text-white p-2 w-max rounded shadow-lg z-10">
          <Button
            className="flex items-center justify-center w-12 h-12 bg-blue-600 text-white rounded-full shadow-lg hover:bg-blue-700 transition-colors"
            onClick={handleUpdateClick}
          >
            <FaArrowRotateLeft className="h-6 w-6" />
          </Button>
        </div>
      )}
      <WebSocketNotificationListener url={`${WS_URL}/ws`} />
    </div>
  );
}