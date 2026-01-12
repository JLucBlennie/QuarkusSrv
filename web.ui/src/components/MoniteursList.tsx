'use client';

import { MoniteurJSON, SERVER_URL } from '@/app/page';
import { useEffect, useState } from 'react';
import { FaPlus } from 'react-icons/fa6';
import { DataTableMoniteur } from './DataTableMoniteur';
import { MoniteurColumn, moniteurcolumns } from './Moniteur-columns';
import { MoniteurEditor } from './MoniteurEditor';
import { Button } from './ui/button';

const MoniteursList = () => {
    const [moniteurs, setMoniteurs] = useState<MoniteurColumn[]>([]);
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState(true);
    const [rowClicked, setRowClicked] = useState<boolean>(false);
    const [addClicked, setAddClicked] = useState<boolean>(false);
    const [selectedRow, setSelectedRow] = useState<MoniteurColumn | null>(null);
    const [moniteursData, setMoniteursData] = useState<MoniteurJSON[]>([]);

    useEffect(() => {
        if (loading) {
            console.log('Chargement des moniteurs depuis le serveur Quarkus...');
            fetch(`${SERVER_URL}/ctr/moniteurs`, {
                method: "GET",
                redirect: "follow",
                cache: "force-cache"
            })
                .then((res) => {
                    if (!res.ok) {
                        throw new Error(`Erreur serveur : ${res.status}`);
                    }
                    return res.json();
                })
                .then((data) => {
                    console.log('Réponse du serveur Quarkus :', data);
                    let moniteurs: MoniteurColumn[] = [];
                    let id = 0;
                    data.map((moniteur: MoniteurJSON) => {
                        var moniteurCol: MoniteurColumn = {
                            uuid: moniteur.uuid,
                            lastname: moniteur.lastname,
                            firstname: moniteur.firstname,
                            niveau: moniteur.niveau,
                        };
                        moniteurs.push(moniteurCol);
                    });
                    setMoniteurs(moniteurs);
                    setMoniteursData(data);
                    setLoading(false);
                })
                .catch((err) => {
                    console.error('Erreur fetch :', err);
                    setError(err.message);
                    setLoading(false);
                });
        }
    }, []);

    function handleRowClick(row: MoniteurColumn) {
        console.log('Ligne cliquée :', row);
        setRowClicked(true);
        setSelectedRow(row);
    }

    function handleAddClick() {
        console.log('Ajouter un moniteur');
        setAddClicked(true);
        setSelectedRow(null);
    }

    return (
        <div>
            {(!rowClicked && !addClicked && !error && !loading) &&
                <div>
                    <DataTableMoniteur columns={moniteurcolumns} data={moniteurs} onRowClick={handleRowClick} />
                    <Button className="fixed bottom-6 right-6 flex items-center justify-center w-12 h-12 bg-blue-600 text-white rounded-full shadow-lg hover:bg-blue-700 transition-colors z-10" onClick={handleAddClick}>
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

export default MoniteursList;
