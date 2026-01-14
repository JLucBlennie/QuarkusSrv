'use client';

import { EvenementJSON, SERVER_URL } from '@/lib/constants';
import { useEffect, useState } from 'react';
import { FaPlus } from "react-icons/fa6";
import { DataTable } from './DataTable';
import { EvenementEditor } from './EvenementEditor';
import { EventColumn, eventcolumns } from './Event-columns';
import { Button } from './ui/button';

const EvenementsList = () => {
  const [evenements, setEvenements] = useState<EventColumn[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [rowClicked, setRowClicked] = useState<boolean>(false);
  const [addClicked, setAddClicked] = useState<boolean>(false);
  const [selectedRow, setSelectedRow] = useState<EventColumn | null>(null);
  const [eventsData, setEventsData] = useState<EvenementJSON[]>([]);

  useEffect(() => {
    if (loading) {
      console.log('Chargement des événements depuis le serveur Quarkus...');
      fetch(`${SERVER_URL}/ctr/evenements`, {
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
          let events: EventColumn[] = [];
          let id = 0;
          data.map((evenement: EvenementJSON) => {
            var eventCol: EventColumn = {
              uuid: (evenement.uuid || ''),
              datedemande: (evenement.datedemande?.split('T')[0] || ''),
              statut: (evenement.statut || ''),
              activite: (evenement.typeEvenement === undefined) ? "Type null" : evenement.typeEvenement.name,
              organisateur: evenement.organisateur ? evenement.organisateur.name : "",
              datedebut: (evenement.datedebut?.split('T')[0] || ''),
              datefin: (evenement.datefin?.split('T')[0] || ''),
              lieu: (evenement.lieu || '')
            };
            events.push(eventCol);
          });
          setEvenements(events);
          setEventsData(data);
          setLoading(false);
        })
        .catch((err) => {
          console.error('Erreur fetch :', err);
          setError(err.message);
          setLoading(false);
        });
    }
  }, []);

  function handleRowClick(row: EventColumn) {
    console.log('Ligne cliquée :', row);
    setRowClicked(true);
    setSelectedRow(row);
  }

  function handleAddClick() {
    console.log('Ajouter un événement');
    setAddClicked(true);
    setSelectedRow(null);
  }

  return (
    <div>
      {(!rowClicked && !addClicked && !error && !loading) &&
        <div>
          <DataTable columns={eventcolumns} data={evenements} onRowClick={handleRowClick} />
          <Button className="fixed bottom-6 right-6 flex items-center justify-center w-12 h-12 bg-blue-600 text-white rounded-full shadow-lg hover:bg-blue-700 transition-colors z-10" onClick={handleAddClick}>
            <FaPlus className="h-6 w-6" />
          </Button>
        </div>
      }
      {rowClicked && <EvenementEditor uuid={selectedRow?.uuid} onExit={() => { setRowClicked(false) }} />}
      {addClicked && <EvenementEditor uuid={undefined} onExit={() => { setAddClicked(false) }} />}
      {error && <p>Erreur : {error}</p>}
      {loading && <p> </p>}
    </div >
  );
};

export default EvenementsList;
