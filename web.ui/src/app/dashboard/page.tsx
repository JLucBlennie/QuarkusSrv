'use client'

import { AdminStatsPanel } from "@/components/dashboard/AdminStatsPanel";
import { PendingEventsPanel } from "@/components/dashboard/PendingEventsPanel";
import { UpcomingEventsPanel } from "@/components/dashboard/UpcomingEventsPanel";
import { useAuth } from "@/context/AuthContext";

export default function DashboardPage() {
    const { user, hasRole, isLoading } = useAuth();

    if (isLoading) return <p>Chargement…</p>;

    const isAdmin = hasRole('admin');

    return (
        <div className="p-6 space-y-6">
            <h1 className="text-2xl font-semibold">
                Tableau de bord{user ? ` — ${user.username}` : ''}
            </h1>

            <div className="grid gap-6 md:grid-cols-2">
                <UpcomingEventsPanel />
                <PendingEventsPanel isAdmin={isAdmin} />
            </div>

            {isAdmin && (
                <div className="grid gap-6 md:grid-cols-3">
                    <AdminStatsPanel
                        title="Moniteurs"
                        endpoint="/evenements/stats/moniteurs"
                        labelFn={(m) => `${m.firstname} ${m.lastname}`}
                    />
                    <AdminStatsPanel
                        title="Demandeurs"
                        endpoint="/evenements/stats/demandeurs"
                        labelFn={(d) => String(d.name)}
                    />
                    <AdminStatsPanel
                        title="Types d'activité"
                        endpoint="/evenements/stats/types"
                        labelFn={(t) => String(t.name)}
                    />
                </div>
            )}
        </div>
    );
}