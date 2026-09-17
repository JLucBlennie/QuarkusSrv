"use client";

import { EvenementsList } from "@/components/EvenementsList";
import { useAuth } from "@/context/AuthContext";

export default function EvenementsPage() {
    const { hasRole, isLoading } = useAuth();

    if (isLoading) {
        return null;
    }

    if (!hasRole("admin")) {

        return (<div className="p-5">
            <h1 className="text-3xl font-bold mb-4">Événements</h1>
            <EvenementsList mesEvenementsOnly={true} />
        </div>);
    }

    return (
        <div className="p-5 relative min-h-screen bg-logo-35op bg-no-repeat bg-center bg-contain">
            <h1 className="text-3xl font-bold mb-4">Événements</h1>
            <EvenementsList />
        </div>
    );
}