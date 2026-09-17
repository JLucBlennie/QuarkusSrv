'use client';

import { authFetch } from '@/lib/authService';
import { EvenementJSON, SERVER_URL } from '@/lib/constants';
import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';
import { DataTable } from '../DataTable';
import { EventColumn, eventcolumns } from '../Event-columns';
import { Card, CardContent, CardHeader, CardTitle } from '../ui/card';

interface PendingEventsPanelProps {
    isAdmin: boolean;
}

export function PendingEventsPanel({ isAdmin }: PendingEventsPanelProps) {
    const router = useRouter();
    const [events, setEvents] = useState<EventColumn[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const url = isAdmin
            ? `${SERVER_URL}/evenements?statut=DEMANDE,CONFLIT`
            : `${SERVER_URL}/evenements/mes-evenements?statut=DEMANDE,CONFLIT`;

        authFetch(url, { method: 'GET', redirect: 'follow' })
            .then((res) => {
                if (!res.ok) throw new Error(`Erreur serveur : ${res.status}`);
                return res.json();
            })
            .then((data: EvenementJSON[]) => {
                console.log('PendingEventsPanel: data reçue', data);
                setEvents(data.map((e) => ({
                    uuid: e.uuid || '',
                    datedemande: e.datedemande || 0,
                    statut: e.statut || '',
                    activite: e.typeEvenement?.name ?? 'Type null',
                    organisateur: e.organisateur?.name || '',
                    datedebut: e.datedebut || 0,
                    datefin: e.datefin || 0,
                    lieu: e.lieu || '',
                })));
            })
            .catch((err) => setError(err.message))
            .finally(() => setLoading(false));
    }, [isAdmin]);

    return (
        <Card>
            <CardHeader>
                <CardTitle>En attente de validation ({events.length})</CardTitle>
            </CardHeader>
            <CardContent>
                {loading && <p className="text-sm text-gray-400">Chargement…</p>}
                {error && <p className="text-sm text-red-400">Erreur : {error}</p>}
                {!loading && !error && (
                    <DataTable
                        columns={eventcolumns}
                        data={events}
                        height="300px"
                        onRowClick={(row) => router.push(`/evenements/${row.uuid}`)}
                        rowClassName={(row) =>
                            row.getValue('statut') === 'VALIDE' ? 'bg-emerald-800 bg-opacity-70'
                                : row.getValue('statut') === 'DEMANDE' ? 'bg-orange-800 bg-opacity-70'
                                    : row.getValue('statut') === 'REFUSE' ? 'bg-red-800 bg-opacity-70'
                                        : ''
                        }
                    />
                )}
            </CardContent>
        </Card>
    );
}