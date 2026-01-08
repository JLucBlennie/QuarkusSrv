'use client';

import { useEffect, useState } from 'react';
import { EvenementEditor } from './EvenementEditor';
import { columns, EventColumn } from './Event-columns';
import { DataTable } from './Event-table';

// TODO : Ajouter les types manquants : TypeActivite, Demandeur, Moniteur, ClubStructure
type ClubStructure = {
  uuid: string;
  name: string;
}

type Demandeur = {
  uuid: string;
  name: string;
  numerostructure: string;
}

type Moniteur = {
  uuid: string;
  lastname: string;
  firstname: string;
  niveau: string;
}

type TypeEvenement = {
  uuid: string;
  name: string;
  activite: string;
  valeurforms: string;
}

type Session = {
  uuid: string;
  dateDebut: Date;
  dateFin: Date;
  typeSession: string;
}

export type EvenementJSON = {
  uuid: string;
  evtidforms: string;
  datedemande: string;
  datedebut: string;
  datefin: string;
  typeEvenement: TypeEvenement;
  demandeur: Demandeur;
  partenaire: Demandeur;
  mailcontact: string;
  lieu: string;
  presidentjury: Moniteur;
  deleguectr: Moniteur;
  repcibpl: Moniteur;
  statut: string;
  datevalidation: string;
  organisateur: ClubStructure;
  comment: string;
  calendareventid: string;
  sessions: Session[];
};

/* const SERVER_URL = 'http://51.83.78.37:9090'; */
const SERVER_URL = 'http://localhost:9090';

const EvenementsList = () => {
  const [evenements, setEvenements] = useState<EventColumn[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [rowClicked, setRowClicked] = useState<boolean>(false);
  const [selectedRow, setSelectedRow] = useState<EventColumn | null>(null);
  const [eventsData, setEventsData] = useState<EvenementJSON[]>([]);

  useEffect(() => {
    fetch(`${SERVER_URL}/ctr/evenements`, {
      method: "GET",
      redirect: "follow"
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
            uuid: evenement.uuid,
            datedemande: evenement.datedemande,
            statut: evenement.statut,
            activite: (evenement.typeEvenement === undefined) ? "Type null" : evenement.typeEvenement.activite,
            organisateur: evenement.organisateur ? evenement.organisateur.name : "",
            datedebut: evenement.datedebut,
            datefin: evenement.datefin,
            lieu: evenement.lieu
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
  }, []);

  function handleRowClick(row: EventColumn) {
    console.log('Ligne cliquée :', row);
    setRowClicked(true);
    setSelectedRow(row);
  }

  return (
    <div>
      {(!rowClicked && !error && !loading) &&
        <DataTable columns={columns} data={evenements} onRowClick={handleRowClick} />
      }
      {rowClicked && <EvenementEditor uuid={selectedRow?.uuid} onExit={() => { setRowClicked(false) }} />}
      {error && <p>Erreur : {error}</p>}
      {loading && <p> </p>}
    </div >
  );
};

export default EvenementsList;
