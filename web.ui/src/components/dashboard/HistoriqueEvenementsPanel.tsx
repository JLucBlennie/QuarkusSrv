'use client';

import { authFetch } from '@/lib/authService';
import { EvenementJSON, SERVER_URL } from '@/lib/constants';
import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';
import { DataTable } from '../DataTable';
import { EventColumn, eventcolumns } from '../Event-columns';
import { SaisonSelector } from '../SaisonSelector';
import { Card, CardContent, CardHeader, CardTitle } from '../ui/card';

function getCurrentSaison(): string {
    const now = new Date();
    const mois = now.getMonth();
    const annee = now.getFullYear();
    const anneeDebut = mois >= 8 ? annee : annee - 1;
    return `${anneeDebut}-${anneeDebut + 1}`;
}

interface HistoriqueEvenementsPanelProps {
    isAdmin?: boolean;
}

export function HistoriqueEvenementsPanel({ isAdmin }: HistoriqueEvenementsPanelProps) {
    const router = useRouter();
    const [saison, setSaison] = useState<string>(getCurrentSaison());
    const [events, setEvents] = useState<EventColumn[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        setLoading(true);
        const url = isAdmin ? `${SERVER_URL}/evenements?saison=${saison}`
            : `${SERVER_URL}/evenements/mes-evenements?saison=${saison}`;
        authFetch(url, { method: 'GET', redirect: 'follow' })
            .then((res) => {
                if (!res.ok) throw new Error(`Erreur serveur : ${res.status}`);
                return res.json();
            })
            .then((data: EvenementJSON[]) => {
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
    }, [saison]);

    return (
        <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0">
                <CardTitle>Historique des évènements ({events.length})</CardTitle>
                <SaisonSelector value={saison} onChange={setSaison} />
            </CardHeader>
            <CardContent>
                {loading && <p className="text-sm text-gray-400">Chargement…</p>}
                {error && <p className="text-sm text-red-400">Erreur : {error}</p>}
                {!loading && !error && (
                    <DataTable
                        columns={eventcolumns}
                        data={events}
                        height="400px"
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