'use client';

import { authFetch } from '@/lib/authService';
import { SERVER_URL } from '@/lib/constants';
import { useEffect, useState } from 'react';

interface SaisonStat {
    saison: string;
    nbevents: number;
}

interface SaisonSelectorProps {
    value: string;
    onChange: (saison: string) => void;
}

export function SaisonSelector({ value, onChange }: SaisonSelectorProps) {
    const [saisons, setSaisons] = useState<string[]>([]);

    useEffect(() => {
        authFetch(`${SERVER_URL}/evenements/stats/saisons`, { method: 'GET', redirect: 'follow' })
            .then((res) => {
                if (!res.ok) throw new Error(`Erreur serveur : ${res.status}`);
                return res.json();
            })
            .then((data: SaisonStat[]) => setSaisons(data.map((s) => s.saison)))
            .catch((err) => console.error('Erreur chargement des saisons :', err));
    }, []);

    return (
        <select
            value={value}
            onChange={(e) => onChange(e.target.value)}
            className="rounded-md border border-gray-600 bg-gray-800 text-white text-sm px-2 py-1.5"
        >
            <option value="">Toutes saisons confondues</option>
            {saisons.map((s) => (
                <option key={s} value={s}>{s}</option>
            ))}
        </select>
    );
}