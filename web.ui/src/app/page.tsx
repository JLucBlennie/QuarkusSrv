"use client";

import { AdminDashboard } from "@/components/dashboard/AdminDashboard";
import { UserDashboard } from "@/components/dashboard/UserDashboard";
import { ThemeToggle } from "@/components/ui/theme-toggle";
import { useAuth } from "@/context/AuthContext";
import { LogOut } from "lucide-react";
import { useEffect } from "react";
import pack from "../../package.json";

export default function Home() {
  const { user, logout, hasRole, isLoading } = useAuth();

  useEffect(() => {
    if (user?.username) {
      document.title = `Calendrier CTR — ${user.username}`;
    }
  }, [user]);

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <p className="text-gray-400">Chargement…</p>
      </div>
    );
  }

  const isAdmin = hasRole("admin");

  return (
    <div className="relative min-h-screen bg-logo-35op bg-no-repeat bg-center bg-contain">
      <div className="w-full h-full absolute top-0 left-0">
        <h1 className="text-5xl font-bold text-center pt-5">Calendrier de la CTR</h1>
        <div className="relative p-5">
          {isAdmin ? <AdminDashboard /> : <UserDashboard />}
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
    </div>
  );
}