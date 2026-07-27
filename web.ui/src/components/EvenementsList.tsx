'use client';

import { authFetch } from '@/lib/authService';
import { EvenementJSON, SERVER_URL } from '@/lib/constants';
import { ColumnDef } from '@tanstack/react-table';
import { useRouter } from 'next/navigation';
import { useEffect, useMemo, useState } from 'react';
import { FaFilter, FaPlus, FaTrash } from "react-icons/fa6";
import { DataTable } from './DataTable';
import { EventColumn, eventcolumns } from './Event-columns';
import { Button } from './ui/button';
import { Checkbox } from './ui/checkbox';

interface EvenementsListProps {
  mesEvenementsOnly?: boolean;
}

interface Filters {
  organisateur: string;
  demandeur: string;
  activite: string;
  statut: string;
}

const STATUTS = ['DEMANDE', 'VALIDE', 'REFUSE'];
const EMPTY_FILTERS: Filters = { organisateur: '', demandeur: '', activite: '', statut: '' };

export function EvenementsList({ mesEvenementsOnly = false }: EvenementsListProps) {
  const router = useRouter();
  const [evenements, setEvenements] = useState<EventColumn[]>([]);
  const [eventsData, setEventsData] = useState<EvenementJSON[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  // Sélection pour suppression
  const [selectedUuids, setSelectedUuids] = useState<Set<string>>(new Set());
  const [confirmDelete, setConfirmDelete] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);

  // Filtres
  const [filters, setFilters] = useState<Filters>(EMPTY_FILTERS);
  const [showFilters, setShowFilters] = useState(false);

  useEffect(() => { updateEvenements(); }, []);

  // ── Valeurs uniques pour les selects de filtre ──────────────────────────────

  const uniqueOrganisateurs = useMemo(() =>
    [...new Set(evenements.map(e => e.organisateur).filter(Boolean))].sort(),
    [evenements]
  );
  const uniqueDemandeurs = useMemo(() =>
    [...new Set(eventsData.map(e => e.demandeur?.name ?? '').filter(Boolean))].sort(),
    [eventsData]
  );
  const uniqueActivites = useMemo(() =>
    [...new Set(evenements.map(e => e.activite).filter(Boolean))].sort(),
    [evenements]
  );

  // ── Données filtrées ────────────────────────────────────────────────────────

  const filteredEvenements = useMemo(() => {
    return evenements
      .sort((a, b) => b.datedemande - a.datedemande)
      .filter(e => {
        if (filters.organisateur && e.organisateur !== filters.organisateur) return false;
        if (filters.activite && e.activite !== filters.activite) return false;
        if (filters.statut && e.statut !== filters.statut) return false;
        if (filters.demandeur) {
          const raw = eventsData.find(ev => ev.uuid === e.uuid);
          if ((raw?.demandeur?.name ?? '') !== filters.demandeur) return false;
        }
        return true;
      });
  }, [evenements, eventsData, filters]);

  const activeFilterCount = Object.values(filters).filter(Boolean).length;
  const hasActiveFilters = activeFilterCount > 0;

  // ── Handlers ────────────────────────────────────────────────────────────────

  function handleRowClick(row: EventColumn) {
    // En mode sélection, le clic bascule la case au lieu d'ouvrir l'éditeur
    if (selectedUuids.size > 0) {
      toggleRow(row.uuid);
      return;
    }
    router.push(`/evenements/${row.uuid}`);
  }

  function handleAddClick() {
    router.push('/evenements/nouveau');
  }

  function toggleRow(uuid: string) {
    setSelectedUuids(prev => {
      const next = new Set(prev);
      next.has(uuid) ? next.delete(uuid) : next.add(uuid);
      return next;
    });
  }

  function toggleSelectAll() {
    const allVisible = filteredEvenements.map(e => e.uuid);
    const allSelected = allVisible.every(id => selectedUuids.has(id)) && allVisible.length > 0;
    if (allSelected) {
      setSelectedUuids(new Set());
    } else {
      setSelectedUuids(new Set(allVisible));
    }
  }

  async function handleDelete() {
    if (selectedUuids.size === 0) return;
    setIsDeleting(true);
    try {
      await Promise.all([...selectedUuids].map(uuid =>
        authFetch(`${SERVER_URL}/evenements/${uuid}`, { method: 'DELETE', redirect: 'follow' })
      ));
      setSelectedUuids(new Set());
      await updateEvenements();
    } catch (err: any) {
      setError(`Erreur lors de la suppression : ${err.message}`);
    } finally {
      setIsDeleting(false);
      setConfirmDelete(false);
    }
  }

  function resetFilters() {
    setFilters(EMPTY_FILTERS);
  }

  function setFilter(key: keyof Filters, value: string) {
    setFilters(prev => ({ ...prev, [key]: value }));
  }

  // ── Colonne checkbox (construite ici pour accéder au state) ─────────────────

  const allVisibleSelected =
    filteredEvenements.length > 0 &&
    filteredEvenements.every(e => selectedUuids.has(e.uuid));

  const selectionColumn: ColumnDef<EventColumn> = {
    id: 'select',
    size: 40,
    header: () => (
      <Checkbox
        checked={allVisibleSelected}
        onCheckedChange={toggleSelectAll}
        aria-label="Tout sélectionner"
      />
    ),
    cell: ({ row }) => (
      <Checkbox
        checked={selectedUuids.has(row.original.uuid)}
        onCheckedChange={() => toggleRow(row.original.uuid)}
        onClick={e => e.stopPropagation()}
        aria-label="Sélectionner la ligne"
      />
    ),
  };

  const columns = [selectionColumn, ...eventcolumns];

  // ── Fetch ───────────────────────────────────────────────────────────────────

  function updateEvenements() {
    const url = mesEvenementsOnly
      ? `${SERVER_URL}/evenements/mes-evenements`
      : `${SERVER_URL}/evenements`;

    setLoading(true);
    authFetch(url, { method: 'GET', redirect: 'follow' })
      .then(res => {
        if (!res.ok) throw new Error(`Erreur serveur : ${res.status}`);
        return res.json();
      })
      .then((data: EvenementJSON[]) => {
        const events: EventColumn[] = data.map(evenement => ({
          uuid: evenement.uuid || '',
          datedemande: evenement.datedemande || 0,
          statut: evenement.statut || '',
          activite: evenement.typeEvenement?.name ?? 'Type null',
          organisateur: evenement.organisateur?.name || '',
          datedebut: evenement.datedebut || 0,
          datefin: evenement.datefin || 0,
          lieu: evenement.lieu || '',
        }));
        setEvenements(events);
        setEventsData(data);
        setLoading(false);
      })
      .catch(err => {
        console.error('Erreur fetch :', err);
        setError(err.message);
        setLoading(false);
      });
  }

  // ── Render ──────────────────────────────────────────────────────────────────

  const selectCls = "rounded-md border border-gray-600 bg-gray-800 text-white text-sm px-2 py-1.5 focus:outline-none focus:border-blue-500 min-w-[140px]";

  return (
    <div>
      {(!error && !loading) && (
        <div className="relative">

          {/* ── En-tête ── */}
          <div className="flex items-center justify-between mb-3">
            <h2 className="text-xl font-semibold">Liste des événements</h2>
            <div className="flex items-center gap-2">
              {selectedUuids.size > 0 && (
                <Button
                  variant="destructive"
                  className="flex items-center gap-1.5 text-sm"
                  onClick={() => setConfirmDelete(true)}
                  disabled={isDeleting}
                >
                  <FaTrash className="h-3 w-3" />
                  Supprimer ({selectedUuids.size})
                </Button>
              )}
              <Button
                variant="outline"
                className={`flex items-center gap-1.5 text-sm ${hasActiveFilters ? 'border-blue-500 text-blue-400' : ''}`}
                onClick={() => setShowFilters(f => !f)}
              >
                <FaFilter className="h-3 w-3" />
                Filtres {hasActiveFilters && <span className="ml-0.5 bg-blue-600 text-white rounded-full px-1.5 text-xs">{activeFilterCount}</span>}
              </Button>
            </div>
          </div>

          {/* ── Panneau de filtres ── */}
          {showFilters && (
            <div className="mb-3 p-3 rounded-lg border border-gray-700 bg-gray-900 flex flex-wrap gap-4 items-end">
              <div className="flex flex-col gap-1">
                <label htmlFor='filtre-organisateur' className="text-xs text-gray-400">Organisateur</label>
                <select id='filtre-organisateur' className={selectCls} value={filters.organisateur} onChange={e => setFilter('organisateur', e.target.value)}>
                  <option value="">Tous</option>
                  {uniqueOrganisateurs.map(o => <option key={o} value={o}>{o}</option>)}
                </select>
              </div>
              <div className="flex flex-col gap-1">
                <label htmlFor='filtre-demandeur' className="text-xs text-gray-400">Demandeur</label>
                <select id='filtre-demandeur' className={selectCls} value={filters.demandeur} onChange={e => setFilter('demandeur', e.target.value)}>
                  <option value="">Tous</option>
                  {uniqueDemandeurs.map(d => <option key={d} value={d}>{d}</option>)}
                </select>
              </div>
              <div className="flex flex-col gap-1">
                <label htmlFor='filtre-activite' className="text-xs text-gray-400">Activité</label>
                <select id='filtre-activite' className={selectCls} value={filters.activite} onChange={e => setFilter('activite', e.target.value)}>
                  <option value="">Toutes</option>
                  {uniqueActivites.map(a => <option key={a} value={a}>{a}</option>)}
                </select>
              </div>
              <div className="flex flex-col gap-1">
                <label htmlFor='filtre-statut' className="text-xs text-gray-400">Statut</label>
                <select id='filtre-statut' className={selectCls} value={filters.statut} onChange={e => setFilter('statut', e.target.value)}>
                  <option value="">Tous</option>
                  {STATUTS.map(s => <option key={s} value={s}>{s}</option>)}
                </select>
              </div>
              <div className="flex items-end gap-3 ml-auto">
                {hasActiveFilters && (
                  <button className="text-xs text-gray-400 hover:text-white underline" onClick={resetFilters}>
                    Réinitialiser
                  </button>
                )}
                <span className="text-xs text-gray-500">
                  {filteredEvenements.length} / {evenements.length} événement{evenements.length > 1 ? 's' : ''}
                </span>
              </div>
            </div>
          )}

          {/* ── Tableau ── */}
          <DataTable
            columns={columns}
            data={filteredEvenements}
            onRowClick={handleRowClick}
            rowClassName={(row) =>
              row.getValue("statut") === "VALIDE" ? "bg-emerald-800 bg-opacity-70"
                : row.getValue("statut") === "DEMANDE" ? "bg-orange-800 bg-opacity-70"
                  : row.getValue("statut") === "REFUSE" ? "bg-red-800 bg-opacity-70"
                    : ""
            }
          />

          {/* ── Bouton ajout ── */}
          <Button
            className="absolute bottom-0 right-0 flex items-center justify-center w-12 h-12 bg-blue-600 text-white rounded-full shadow-lg hover:bg-blue-700 transition-colors z-10"
            onClick={handleAddClick}
          >
            <FaPlus className="h-6 w-6" />
          </Button>

          {/* ── Modal confirmation suppression ── */}
          {confirmDelete && (
            <div className="fixed inset-0 bg-black bg-opacity-60 flex items-center justify-center z-50">
              <div className="bg-gray-900 border border-gray-700 rounded-xl p-6 max-w-sm w-full shadow-xl">
                <h3 className="text-lg font-semibold mb-2">Confirmer la suppression</h3>
                <p className="text-gray-300 text-sm mb-5">
                  Voulez-vous vraiment supprimer{' '}
                  <strong>{selectedUuids.size} événement{selectedUuids.size > 1 ? 's' : ''}</strong> ?
                  Cette action est irréversible.
                </p>
                <div className="flex gap-3 justify-end">
                  <Button variant="outline" onClick={() => setConfirmDelete(false)} disabled={isDeleting}>
                    Annuler
                  </Button>
                  <Button variant="destructive" onClick={handleDelete} disabled={isDeleting}>
                    {isDeleting ? 'Suppression…' : 'Supprimer définitivement'}
                  </Button>
                </div>
              </div>
            </div>
          )}
        </div>
      )}

      {error && <p>Erreur : {error}</p>}
      {loading && <p>Chargement en cours…</p>}
    </div>
  );
}
