'use client';

import React, { useEffect, useState } from 'react';
import { DataTable } from './Event-table';
import { columns, EventColumn } from './Event-columns';

type EvenementJSON = {
  uuid: string;
  evtidforms: string;
  datedemande: string;
  datedebut: string;
  datefin: string;
  uuidtype: string;
  uuiddemandeur: string;
  uuidpartenaire: string;
  mailcontact: string;
  lieu: string;
  uuidpresidentjury: string;
  uuiddeleguectr: string;
  uuidrepcibpl: string;
  statut: string;
  datevalidation: string;
  uuidorganisateur: string;
  comment: string;
  calendareventid: string;
};

const EvenementsList = () => {
  const [evenements, setEvenements] = useState<EventColumn[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch('http://localhost:9090/ctr/evenements', {
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
          var eventCol: EventColumn = { datedemande: "", statut: "", activite: "", organisateur: "", datedebut: "", datefin: "", lieu: "" };
          eventCol.datedebut = evenement.datedebut;
          eventCol.datedemande = evenement.datedemande;
          eventCol.datefin=evenement.datefin;
          eventCol.lieu=evenement.lieu;
        });
        setEvenements(events);
        setLoading(false);
      })
      .catch((err) => {
        console.error('Erreur fetch :', err);
        setError(err.message);
        setLoading(false);
      });
  }, []);

  if (loading) return <p>Chargement des événements...</p>;
  if (error) return <p>Erreur : {error}</p>;

  return (
    <div>
      <DataTable columns={columns} data={evenements} />
    </div>
  );
};

export default EvenementsList;
