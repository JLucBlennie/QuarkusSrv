'use client';

import { Demandeur, SERVER_URL } from '@/lib/constants';
import { useEffect, useState } from 'react';
import { FaPlus } from 'react-icons/fa6';
import { DataTableDemandeur } from './DataTableDemandeur';
import { DemandeurColumn, demandeurcolumns } from './Demandeur-columns';
import { DemandeurEditor } from './DemandeurEditor';
import { Button } from './ui/button';

export function DemandeurList() {
    const [demandeurs, setDemandeurs] = useState<DemandeurColumn[]>([]);
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState(true);
    const [rowClicked, setRowClicked] = useState<boolean>(false);
    const [addClicked, setAddClicked] = useState<boolean>(false);
    const [selectedRow, setSelectedRow] = useState<DemandeurColumn | null>(null);
    const [demandeursData, setDemandeursData] = useState<Demandeur[]>([]);

    useEffect(() => {
        if (loading) {
            console.log('Chargement des demandeurs depuis le serveur Quarkus...');
            fetch(`${SERVER_URL}/ctr/demandeurs`, {
                method: "GET",
                redirect: "follow",
            })
                .then((res) => {
                    if (!res.ok) {
                        throw new Error(`Erreur serveur : ${res.status}`);
                    }
                    return res.json();
                })
                .then((data) => {
                    console.log('Réponse du serveur Quarkus :', data);
                    let demandeurs: DemandeurColumn[] = [];
                    data.map((demandeur: Demandeur) => {
                        var demandeurCol: DemandeurColumn = {
                            uuid: (demandeur.uuid || ''),
                            name: (demandeur.name || ''),
                            numerostructure: (demandeur.numerostructure || ''),
                            nbevents: (demandeur.nbevents || 0)
                        };
                        demandeurs.push(demandeurCol);
                    });
                    setDemandeurs(demandeurs);
                    setDemandeursData(data);
                    setLoading(false);
                })
                .catch((err) => {
                    console.error('Erreur fetch :', err);
                    setError(err.message);
                    setLoading(false);
                });
        }
    }, []);

    function handleRowClick(row: DemandeurColumn) {
        console.log('Ligne cliquée :', row);
        setRowClicked(true);
        setSelectedRow(row);
    }

    function handleAddClick() {
        console.log('Ajouter un demandeur');
        setAddClicked(true);
        setSelectedRow(null);
    }

    return (
        <div>
            {(!rowClicked && !addClicked && !error && !loading) &&
                <div className="relative">
                    <h2 className="text-xl font-semibold mb-4">Liste des Demandeurs</h2>
                    <DataTableDemandeur columns={demandeurcolumns} data={demandeurs.sort((a, b) => a.name.localeCompare(b.name))} onRowClick={handleRowClick} />
                    <Button className="absolute bottom-0 right-0 flex items-center justify-center w-12 h-12 bg-blue-600 text-white rounded-full shadow-lg hover:bg-blue-700 transition-colors z-10" onClick={handleAddClick}>
                        <FaPlus className="h-6 w-6" />
                    </Button>
                </div>
            }
            {rowClicked && <DemandeurEditor uuid={selectedRow?.uuid} onExit={() => { setRowClicked(false) }} />}
            {addClicked && <DemandeurEditor uuid={undefined} onExit={() => { setAddClicked(false) }} />}
            {error && <p>Erreur : {error}</p>}
            {loading && <p> </p>}
        </div >
    );
};
