"use client";

import { MoniteursList } from "@/components/MoniteursList";

export default function MoniteursPage() {
    return (
        <div className="p-6 relative min-h-screen bg-logo-35op bg-no-repeat bg-center bg-contain">
            <h1 className="text-2xl font-semibold mb-4">Moniteurs</h1>
            <MoniteursList />
        </div>
    );
}