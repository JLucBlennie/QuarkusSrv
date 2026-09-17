'use client';

import { useRouter } from 'next/navigation';
import { FaPlus } from 'react-icons/fa6';
import { Button } from '../ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '../ui/card';
import { HistoriqueEvenementsPanel } from './HistoriqueEvenementsPanel';
import { PendingEventsPanel } from './PendingEventsPanel';
import { UpcomingEventsPanel } from './UpcomingEventsPanel';

export function UserDashboard() {
    const router = useRouter();

    return (
        <div className="p-6 space-y-6">
            <div className="flex items-center justify-between">
                <h1 className="text-2xl font-semibold">Tableau de bord</h1>
                <Button className="flex items-center gap-1.5" onClick={() => router.push('/evenements/nouveau')}>
                    <FaPlus className="h-4 w-4" /> Nouvel évènement
                </Button>
            </div>
            <div className="grid gap-6 ">
                <Card>
                    <CardHeader className="cursor-pointer hover:bg-muted/50 transition-colors rounded-t-lg"
                        onClick={() => router.push('/evenements')}>
                        <CardTitle>Événements →</CardTitle>
                    </CardHeader>
                    <CardContent className="grid gap-2 md:grid-cols-2">
                        <UpcomingEventsPanel isAdmin={false} />
                        <PendingEventsPanel isAdmin={false} />
                    </CardContent>
                </Card>
            </div>
            <HistoriqueEvenementsPanel isAdmin={false} />
        </div>
    );
}