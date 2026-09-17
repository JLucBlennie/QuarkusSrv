'use client';

import { authFetch } from '@/lib/authService';
import { Demandeur, SERVER_URL } from '@/lib/constants';
import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';
import { DataTable } from '../DataTable';
import { DemandeurColumn, demandeurcolumns } from '../Demandeur-columns';
import { Card, CardContent, CardHeader, CardTitle } from '../ui/card';

interface DemandeursStatsPanelProps {
    saison?: string;
}
export function DemandeursStatsPanel({ saison }: DemandeursStatsPanelProps) {
    const router = useRouter();
    const [demandeurs, setDemandeurs] = useState<DemandeurColumn[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const url = saison
            ? `${SERVER_URL}/evenements/stats/demandeurs?saison=${saison}`
            : `${SERVER_URL}/evenements/stats/demandeurs`;
        authFetch(url, { method: 'GET', redirect: 'follow' })
            .then((res) => {
                if (!res.ok) throw new Error(`Erreur serveur : ${res.status}`);
                return res.json();
            })
            .then((data: Demandeur[]) => {
                setDemandeurs(data.map((d) => ({
                    uuid: d.uuid || '',
                    name: d.name || '',
                    numerostructure: d.numerostructure || '',
                    nbevents: d.nbevents || 0,
                })));
            })
            .catch((err) => setError(err.message))
            .finally(() => setLoading(false));
    }, [saison]);

    return (
        <Card>
            <CardHeader className="cursor-pointer hover:bg-muted/50 transition-colors rounded-t-lg"
                onClick={() => router.push('/parametres')}><CardTitle>Demandeurs →</CardTitle></CardHeader>
            <CardContent>
                {loading && <p className="text-sm text-gray-400">Chargement…</p>}
                {error && <p className="text-sm text-red-400">Erreur : {error}</p>}
                {!loading && !error && (
                    <DataTable
                        columns={demandeurcolumns}
                        data={demandeurs}
                        height="300px"
                        onRowClick={() => { }}
                        rowClassName={(row) => row.getValue('nbevents') === 0 ? 'bg-red-800 bg-opacity-70' : 'bg-emerald-800 bg-opacity-70'}
                    />
                )}
            </CardContent>
        </Card>
    );
}