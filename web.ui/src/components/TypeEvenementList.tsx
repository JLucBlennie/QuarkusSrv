'use client';

import { SERVER_URL, TypeEvenement } from '@/lib/constants';
import { useEffect, useState } from 'react';
import { FaPlus } from 'react-icons/fa6';
import { DataTableTypeEvenement } from './DataTableTypeEvenement';
import { TypeEvenementColumn, typeevenementcolumns } from './TypeEvenement-columns';
import { TypeEvenementEditor } from './TypeEvenementEditor';
import { Button } from './ui/button';

export function TypeEvenementList() {
    const [typeevenements, setTypeEvenements] = useState<TypeEvenementColumn[]>([]);
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState(true);
    const [rowClicked, setRowClicked] = useState<boolean>(false);
    const [addClicked, setAddClicked] = useState<boolean>(false);
    const [selectedRow, setSelectedRow] = useState<TypeEvenementColumn | null>(null);
    const [typeevenementsData, setTypeEvenementsData] = useState<TypeEvenement[]>([]);

    useEffect(() => {
        if (loading) {
            console.log('Chargement des types d\'événements depuis le serveur Quarkus...');
            fetch(`${SERVER_URL}/ctr/typeevenements`, {
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
                    let typeevenements: TypeEvenementColumn[] = [];
                    data.map((typeevenement: TypeEvenement) => {
                        var typeevenementCol: TypeEvenementColumn = {
                            uuid: (typeevenement.uuid || ''),
                            name: (typeevenement.name || ''),
                            activite: (typeevenement.activite || ''),
                            nbevents: (typeevenement.nbevents || 0)
                        };
                        typeevenements.push(typeevenementCol);
                    });
                    setTypeEvenements(typeevenements);
                    setTypeEvenementsData(data);
                    setLoading(false);
                })
                .catch((err) => {
                    console.error('Erreur fetch :', err);
                    setError(err.message);
                    setLoading(false);
                });
        }
    }, []);

    function handleRowClick(row: TypeEvenementColumn) {
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
                    <h2 className="text-xl font-semibold mb-4">Liste des Types d'Evènement</h2>
                    <DataTableTypeEvenement columns={typeevenementcolumns} data={typeevenements.sort((a, b) => a.name.localeCompare(b.name))} onRowClick={handleRowClick} />
                    <Button className="absolute bottom-0 right-0 flex items-center justify-center w-12 h-12 bg-blue-600 text-white rounded-full shadow-lg hover:bg-blue-700 transition-colors z-10" onClick={handleAddClick}>
                        <FaPlus className="h-6 w-6" />
                    </Button>
                </div>
            }
            {rowClicked && <TypeEvenementEditor uuid={selectedRow?.uuid} onExit={() => { setRowClicked(false) }} />}
            {addClicked && <TypeEvenementEditor uuid={undefined} onExit={() => { setAddClicked(false) }} />}
            {error && <p>Erreur : {error}</p>}
            {loading && <p> </p>}
        </div >
    );
};
