'use client';

import { ClubStructure, SERVER_URL } from '@/lib/constants';
import { useEffect, useState } from 'react';
import { FaPlus } from 'react-icons/fa6';
import { ClubStructureColumn, clubstructurecolumns } from './ClubStructure-columns';
import { DataTableClubStructure } from './DataTableClubStructure';
import { MoniteurEditor } from './MoniteurEditor';
import { Button } from './ui/button';

export function ClubStructureList() {
    const [clubstructures, setClubStructures] = useState<ClubStructureColumn[]>([]);
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState(true);
    const [rowClicked, setRowClicked] = useState<boolean>(false);
    const [addClicked, setAddClicked] = useState<boolean>(false);
    const [selectedRow, setSelectedRow] = useState<ClubStructureColumn | null>(null);
    const [clubstructuresData, setClubStructuresData] = useState<ClubStructure[]>([]);

    useEffect(() => {
        if (loading) {
            console.log('Chargement des club et structures depuis le serveur Quarkus...');
            fetch(`${SERVER_URL}/ctr/clubstructures`, {
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
                    let clubstructures: ClubStructureColumn[] = [];
                    data.map((clubstructure: ClubStructure) => {
                        var clubstructureCol: ClubStructureColumn = {
                            uuid: (clubstructure.uuid || ''),
                            name: (clubstructure.name || ''),
                            nbevents: (clubstructure.nbevents || 0)
                        };
                        clubstructures.push(clubstructureCol);
                    });
                    setClubStructures(clubstructures);
                    setClubStructuresData(data);
                    setLoading(false);
                })
                .catch((err) => {
                    console.error('Erreur fetch :', err);
                    setError(err.message);
                    setLoading(false);
                });
        }
    }, []);

    function handleRowClick(row: ClubStructureColumn) {
        console.log('Ligne cliquée :', row);
        setRowClicked(true);
        setSelectedRow(row);
    }

    function handleAddClick() {
        console.log('Ajouter un club/structure');
        setAddClicked(true);
        setSelectedRow(null);
    }

    return (
        <div>
            {(!rowClicked && !addClicked && !error && !loading) &&
                <div className="relative">
                    <h2 className="text-xl font-semibold mb-4">Liste des Club/Structures</h2>
                    <DataTableClubStructure columns={clubstructurecolumns} data={clubstructures.sort((a, b) => a.name.localeCompare(b.name))} onRowClick={handleRowClick} />
                    <Button className="absolute bottom-0 right-0 flex items-center justify-center w-12 h-12 bg-blue-600 text-white rounded-full shadow-lg hover:bg-blue-700 transition-colors z-10" onClick={handleAddClick}>
                        <FaPlus className="h-6 w-6" />
                    </Button>
                </div>
            }
            {rowClicked && <MoniteurEditor uuid={selectedRow?.uuid} onExit={() => { setRowClicked(false) }} />}
            {addClicked && <MoniteurEditor uuid={undefined} onExit={() => { setAddClicked(false) }} />}
            {error && <p>Erreur : {error}</p>}
            {loading && <p> </p>}
        </div >
    );
};
