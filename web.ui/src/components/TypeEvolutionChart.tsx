'use client';

import { authFetch } from '@/lib/authService';
import { ACTIVITE_LABELS, Demandeur, PeriodeStat, SERVER_URL, TypeEvenement } from '@/lib/constants';
import { useEffect, useState } from 'react';
import { CartesianGrid, Line, LineChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import { Card, CardContent, CardHeader, CardTitle } from './ui/card';

interface TypeEvolutionChartProps {
    saison?: string;
}

export function TypeEvolutionChart({ saison }: TypeEvolutionChartProps) {
    const [types, setTypes] = useState<TypeEvenement[]>([]);
    const [demandeurs, setDemandeurs] = useState<Demandeur[]>([]);
    const [groupByActivite, setGroupByActivite] = useState(false);
    const [selectedType, setSelectedType] = useState<string>('');
    const [selectedActivite, setSelectedActivite] = useState<string>('');
    const [selectedDemandeur, setSelectedDemandeur] = useState<string>('');
    const [data, setData] = useState<PeriodeStat[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    // Chargement des listes pour les selecteurs (une seule fois)
    useEffect(() => {
        authFetch(`${SERVER_URL}/typeevenements`, { method: 'GET', redirect: 'follow' })
            .then((res) => res.json())
            .then((d: TypeEvenement[]) => setTypes(d))
            .catch((err) => console.error('Erreur chargement des types :', err));

        authFetch(`${SERVER_URL}/demandeurs`, { method: 'GET', redirect: 'follow' })
            .then((res) => res.json())
            .then((d: Demandeur[]) => setDemandeurs(d))
            .catch((err) => console.error('Erreur chargement des demandeurs :', err));
    }, []);

    // Chargement des donnees d'evolution selon les filtres
    useEffect(() => {
        const params = new URLSearchParams();
        if (groupByActivite && selectedActivite) params.set('activite', selectedActivite);
        if (!groupByActivite && selectedType) params.set('typeUuid', selectedType);
        if (selectedDemandeur) params.set('demandeurUuid', selectedDemandeur);
        if (saison) params.set('saison', saison);

        setLoading(true);
        authFetch(`${SERVER_URL}/evenements/stats/evolution?${params.toString()}`, {
            method: 'GET',
            redirect: 'follow',
        })
            .then((res) => {
                if (!res.ok) throw new Error(`Erreur serveur : ${res.status}`);
                return res.json();
            })
            .then((d: PeriodeStat[]) => setData(d))
            .catch((err) => setError(err.message))
            .finally(() => setLoading(false));
    }, [groupByActivite, selectedType, selectedActivite, selectedDemandeur, saison]);

    const activitesDisponibles = Array.from(
        new Set(types.map((t) => t.activite).filter((a): a is string => !!a))
    );

    return (
        <Card>
            <CardHeader className="flex flex-col gap-3">
                <div className="flex items-center justify-between flex-wrap gap-2">
                    <CardTitle>Évolution du nombre d'évènements</CardTitle>
                    <label className="flex items-center gap-2 text-sm text-gray-300">
                        <input
                            type="checkbox"
                            checked={groupByActivite}
                            onChange={(e) => setGroupByActivite(e.target.checked)}
                        />
                        Regrouper par activité
                    </label>
                </div>
                <div className="flex gap-2 flex-wrap">
                    {groupByActivite ? (
                        <select
                            value={selectedActivite}
                            onChange={(e) => setSelectedActivite(e.target.value)}
                            className="rounded-md border border-gray-600 bg-gray-800 text-white text-sm px-2 py-1.5"
                        >
                            <option value="">Sélectionner une activité</option>
                            {activitesDisponibles.map((a) => (
                                <option key={a} value={a}>{ACTIVITE_LABELS[a] ?? a}</option>
                            ))}
                        </select>
                    ) : (
                        <select
                            value={selectedType}
                            onChange={(e) => setSelectedType(e.target.value)}
                            className="rounded-md border border-gray-600 bg-gray-800 text-white text-sm px-2 py-1.5"
                        >
                            <option value="">Sélectionner un type</option>
                            {types.map((t) => (
                                <option key={t.uuid} value={t.uuid}>{t.name}</option>
                            ))}
                        </select>
                    )}

                    <select
                        value={selectedDemandeur}
                        onChange={(e) => setSelectedDemandeur(e.target.value)}
                        className="rounded-md border border-gray-600 bg-gray-800 text-white text-sm px-2 py-1.5"
                    >
                        <option value="">Tous les demandeurs</option>
                        {demandeurs.map((d) => (
                            <option key={d.uuid} value={d.uuid}>{d.name}</option>
                        ))}
                    </select>
                </div>
            </CardHeader>
            <CardContent>
                {loading && <p className="text-sm text-gray-400">Chargement…</p>}
                {error && <p className="text-sm text-red-400">Erreur : {error}</p>}
                {!loading && !error && (
                    <ResponsiveContainer width="100%" height={300}>
                        <LineChart data={data}>
                            <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
                            <XAxis dataKey="periode" stroke="#9ca3af" />
                            <YAxis stroke="#9ca3af" allowDecimals={false} />
                            <Tooltip contentStyle={{ backgroundColor: '#1f2937', border: 'none' }} />
                            <Line type="monotone" dataKey="nbevents" stroke="#3b82f6" name="Évènements" strokeWidth={2} />
                        </LineChart>
                    </ResponsiveContainer>
                )}
            </CardContent>
        </Card>
    );
}