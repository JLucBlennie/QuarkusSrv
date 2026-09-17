'use client';

import { DemandeursStatsPanel } from '@/components/dashboard/DemandeursStatsPanel';
import { MoniteursStatsPanel } from '@/components/dashboard/MoniteursStatsPanel';
import { TypesStatsPanel } from '@/components/dashboard/TypesStatsPanel';
import { SaisonEvolutionChart } from '@/components/SaisonEvolutionChart';
import { SaisonSelector } from '@/components/SaisonSelector';
import { useState } from 'react';

function getCurrentSaison(): string {
    const now = new Date();
    const mois = now.getMonth(); // 0 = janvier, 8 = septembre
    const annee = now.getFullYear();
    const anneeDebut = mois >= 8 ? annee : annee - 1;
    return `${anneeDebut}-${anneeDebut + 1}`;
}

export default function StatistiquesPage() {
    const [saison, setSaison] = useState<string>(getCurrentSaison());

    return (
        <div className="p-6 space-y-6">
            <div className="flex items-center justify-between flex-wrap gap-2">
                <h1 className="text-2xl font-semibold">Statistiques</h1>
                <SaisonSelector value={saison} onChange={setSaison} />
            </div>

            <div className="grid gap-6 md:grid-cols-3">
                <MoniteursStatsPanel saison={saison || undefined} />
                <DemandeursStatsPanel saison={saison || undefined} />
                <TypesStatsPanel saison={saison || undefined} />
            </div>

            <SaisonEvolutionChart />
        </div>
    );
}