'use client';

import { authFetch } from '@/lib/authService';
import { EvenementJSON, SERVER_URL } from '@/lib/constants';
import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';
import { DataTable } from '../DataTable';
import { EventSansStatusColumn, eventsansstatuscolumns } from '../Event-columns-sans-status';
import { Card, CardContent, CardHeader, CardTitle } from '../ui/card';

interface UpcomingEventsPanelProps {
    isAdmin: boolean;
}

export function UpcomingEventsPanel({ isAdmin }: UpcomingEventsPanelProps) {
    const router = useRouter();
    const [events, setEvents] = useState<EventSansStatusColumn[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const url = isAdmin
            ? `${SERVER_URL}/evenements?statut=VALIDE`
            : `${SERVER_URL}/evenements/mes-evenements?statut=VALIDE`;

        authFetch(url, { method: 'GET', redirect: 'follow' })
            .then((res) => {
                if (!res.ok) throw new Error(`Erreur serveur : ${res.status}`);
                return res.json();
            })
            .then((data: EvenementJSON[]) => {
                // getAll() (admin) filtre déjà sur la date côté serveur ;
                // mes-evenements ne le fait pas, donc on filtre ici pour le cas user.
                const now = Date.now();
                const upcoming = isAdmin ? data : data.filter((e) => (e.datedebut ?? 0) > now);
                setEvents(upcoming.map((e) => ({
                    uuid: e.uuid || '',
                    datedemande: e.datedemande || 0,
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
                <CardTitle>Événements à venir ({events.length})</CardTitle>
            </CardHeader>
            <CardContent>
                {loading && <p className="text-sm text-gray-400">Chargement…</p>}
                {error && <p className="text-sm text-red-400">Erreur : {error}</p>}
                {!loading && !error && (
                    <DataTable
                        columns={eventsansstatuscolumns}
                        data={events}
                        height="300px"
                        onRowClick={(row) => router.push(`/evenements/${row.uuid}`)}
                        rowClassName={() => 'bg-emerald-800 bg-opacity-70'}
                    />
                )}
            </CardContent>
        </Card>
    );
}