'use client';

import { authFetch } from '@/lib/authService';
import { SERVER_URL } from '@/lib/constants';
import { useEffect, useState } from 'react';
import { Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import { Card, CardContent, CardHeader, CardTitle } from './ui/card';

interface SaisonStat {
    saison: string;
    nbevents: number;
}

export function SaisonEvolutionChart() {
    const [data, setData] = useState<SaisonStat[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        authFetch(`${SERVER_URL}/evenements/stats/saisons`, { method: 'GET', redirect: 'follow' })
            .then((res) => {
                if (!res.ok) throw new Error(`Erreur serveur : ${res.status}`);
                return res.json();
            })
            .then((d: SaisonStat[]) => setData(d))
            .catch((err) => setError(err.message))
            .finally(() => setLoading(false));
    }, []);

    return (
        <Card>
            <CardHeader>
                <CardTitle>Évolution du nombre d'évènements par saison</CardTitle>
            </CardHeader>
            <CardContent>
                {loading && <p className="text-sm text-gray-400">Chargement…</p>}
                {error && <p className="text-sm text-red-400">Erreur : {error}</p>}
                {!loading && !error && (
                    <ResponsiveContainer width="100%" height={300}>
                        <BarChart data={data}>
                            <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
                            <XAxis dataKey="saison" stroke="#9ca3af" />
                            <YAxis stroke="#9ca3af" allowDecimals={false} />
                            <Tooltip contentStyle={{ backgroundColor: '#1f2937', border: 'none' }} />
                            <Bar dataKey="nbevents" fill="#3b82f6" name="Évènements" />
                        </BarChart>
                    </ResponsiveContainer>
                )}
            </CardContent>
        </Card>
    );
}