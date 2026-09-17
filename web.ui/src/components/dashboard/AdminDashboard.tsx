'use client';

import { authFetch } from '@/lib/authService';
import { SERVER_URL } from '@/lib/constants';
import Link from 'next/dist/client/link';
import { useRouter } from 'next/navigation';
import { useState } from 'react';
import { FaArrowRotateLeft, FaChartBar, FaPlus } from 'react-icons/fa6';
import { Button } from '../ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '../ui/card';
import { HistoriqueEvenementsPanel } from './HistoriqueEvenementsPanel';
import { PendingEventsPanel } from './PendingEventsPanel';
import { UpcomingEventsPanel } from './UpcomingEventsPanel';

export function AdminDashboard() {
    const router = useRouter();
    const [updating, setUpdating] = useState(false);

    function handleUpdateClick() {
        setUpdating(true);
        authFetch(`${SERVER_URL}/evenements/updatebdd/`, { method: 'GET', redirect: 'follow' })
            .then((res) => {
                if (!res.ok) throw new Error(`Erreur serveur : ${res.status}`);
            })
            .catch((err) => alert(`Erreur lors de la mise à jour : ${err.message}`))
            .finally(() => setUpdating(false));
    }

    return (
        <div className="p-6 space-y-6">
            <div className="flex items-center justify-between flex-wrap gap-2">
                <h1 className="text-2xl font-semibold">Tableau de bord — Admin</h1>
                <div className="flex gap-2">
                    <Button variant="outline" className="flex items-center gap-1.5" onClick={handleUpdateClick} disabled={updating}>
                        <FaArrowRotateLeft className="h-4 w-4" />
                        {updating ? 'Mise à jour…' : 'Récupérer depuis Google Forms'}
                    </Button>
                    <Button className="flex items-center gap-1.5" onClick={() => router.push('/evenements/nouveau')}>
                        <FaPlus className="h-4 w-4" /> Évènement
                    </Button>
                </div>
            </div>

            <div className="grid gap-6 ">
                <Card>
                    <CardHeader className="cursor-pointer hover:bg-muted/50 transition-colors rounded-t-lg"
                        onClick={() => router.push('/evenements')}>
                        <CardTitle>Événements →</CardTitle>
                    </CardHeader>
                    <CardContent className="grid gap-2 md:grid-cols-2">
                        <UpcomingEventsPanel isAdmin={true} />
                        <PendingEventsPanel isAdmin={true} />
                    </CardContent>
                </Card>
            </div>

            <div className="grid gap-6 md:grid-cols-3">
                <Button variant="outline" className="flex items-center gap-1.5" asChild>
                    <Link href="/statistiques">
                        <FaChartBar className="h-4 w-4" /> Statistiques
                    </Link>
                </Button>
            </div>
            <HistoriqueEvenementsPanel isAdmin={true} />
        </div>
    );
}