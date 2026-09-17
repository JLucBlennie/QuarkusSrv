"use client";

import { MoniteursList } from "@/components/MoniteursList";

export default function MoniteursPage() {
    return (
        <div className="p-6">
            <h1 className="text-2xl font-semibold mb-4">Moniteurs</h1>
            <MoniteursList />
        </div>
    );
}