"use client";

import { ClubStructureList } from "@/components/ClubStructureList";
import { DemandeurList } from "@/components/DemandeurList";
import { ThreeColumnsLayout } from "@/components/ThreeColumnsLayout";
import { TypeEvenementList } from "@/components/TypeEvenementList";

export default function ParametresPage() {
    return (
        <div className="p-6 relative min-h-screen bg-logo-35op bg-no-repeat bg-center bg-contain">
            <h1 className="text-2xl font-semibold mb-4">Paramètres</h1>
            <ThreeColumnsLayout
                left={<ClubStructureList />}
                center={<DemandeurList />}
                right={<TypeEvenementList />}
            />
        </div>
    );
}