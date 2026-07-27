"use client";

import { EvenementsList } from "@/components/EvenementsList";
import { useAuth } from "@/context/AuthContext";
import { useRouter } from "next/navigation";
import { useEffect } from "react";

export default function EvenementsPage() {
    const { hasRole } = useAuth();
    const router = useRouter();

    // Garde d'accès : seuls les admins voient la liste complète.
    // (Les non-admins passent par "Mes Événements" sur la page d'accueil pour le moment.)
    useEffect(() => {
        if (!hasRole("admin")) {
            router.push("/");
        }
    }, [hasRole, router]);

    if (!hasRole("admin")) {
        return null;
    }

    return (
        <div className="p-5">
            <h1 className="text-3xl font-bold mb-4">Événements</h1>
            <EvenementsList />
        </div>
    );
}