'use client';

import { authFetch } from '@/lib/authService';
import { MoniteurJSON, SERVER_URL } from '@/lib/constants';
import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';
import { DataTable } from '../DataTable';
import { MoniteurColumn, moniteurcolumns } from '../Moniteur-columns';
import { Card, CardContent, CardHeader, CardTitle } from '../ui/card';

export function MoniteursStatsPanel() {
    const router = useRouter();
    const [moniteurs, setMoniteurs] = useState<MoniteurColumn[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        authFetch(`${SERVER_URL}/evenements/stats/moniteurs`, { method: 'GET', redirect: 'follow' })
            .then((res) => {
                if (!res.ok) throw new Error(`Erreur serveur : ${res.status}`);
                return res.json();
            })
            .then((data: MoniteurJSON[]) => {
                setMoniteurs(data.map((m) => ({
                    uuid: m.uuid || '',
                    lastname: m.lastname || '',
                    firstname: m.firstname || '',
                    niveau: m.niveau || '',
                    nbevents: m.nbevents || 0,
                })));
            })
            .catch((err) => setError(err.message))
            .finally(() => setLoading(false));
    }, []);

    return (
        <Card>
            <CardHeader
                className="cursor-pointer hover:bg-muted/50 transition-colors rounded-t-lg"
                onClick={() => router.push('/moniteurs')}
            >
                <CardTitle>Moniteurs →</CardTitle>
            </CardHeader>
            <CardContent>
                {loading && <p className="text-sm text-gray-400">Chargement…</p>}
                {error && <p className="text-sm text-red-400">Erreur : {error}</p>}
                {!loading && !error && (
                    <DataTable
                        columns={moniteurcolumns}
                        data={moniteurs}
                        height="300px"
                        onRowClick={() => { }}
                        rowClassName={(row) => row.getValue('nbevents') === 0 ? 'bg-red-800 bg-opacity-70' : 'bg-emerald-800 bg-opacity-70'}
                    />
                )}
            </CardContent>
        </Card>
    );
}