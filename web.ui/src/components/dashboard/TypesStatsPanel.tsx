'use client';

import { authFetch } from '@/lib/authService';
import { SERVER_URL, TypeEvenement } from '@/lib/constants';
import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';
import { DataTable } from '../DataTable';
import { TypeEvenementColumn, typeevenementcolumns } from '../TypeEvenement-columns';
import { Card, CardContent, CardHeader, CardTitle } from '../ui/card';

export function TypesStatsPanel() {
    const router = useRouter();
    const [types, setTypes] = useState<TypeEvenementColumn[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        authFetch(`${SERVER_URL}/evenements/stats/types`, { method: 'GET', redirect: 'follow' })
            .then((res) => {
                if (!res.ok) throw new Error(`Erreur serveur : ${res.status}`);
                return res.json();
            })
            .then((data: TypeEvenement[]) => {
                setTypes(data.map((t) => ({
                    uuid: t.uuid || '',
                    name: t.name || '',
                    activite: t.activite || '',
                    nbevents: t.nbevents || 0,
                })));
            })
            .catch((err) => setError(err.message))
            .finally(() => setLoading(false));
    }, []);

    return (
        <Card>
            <CardHeader className="cursor-pointer hover:bg-muted/50 transition-colors rounded-t-lg"
                onClick={() => router.push('/parametres')}><CardTitle>Types d'activité →</CardTitle></CardHeader>
            <CardContent>
                {loading && <p className="text-sm text-gray-400">Chargement…</p>}
                {error && <p className="text-sm text-red-400">Erreur : {error}</p>}
                {!loading && !error && (
                    <DataTable
                        columns={typeevenementcolumns}
                        data={types}
                        height="300px"
                        onRowClick={() => { }}
                        rowClassName={(row) => row.getValue('nbevents') === 0 ? 'bg-red-800 bg-opacity-70' : 'bg-emerald-800 bg-opacity-70'}
                    />
                )}
            </CardContent>
        </Card>
    );
}