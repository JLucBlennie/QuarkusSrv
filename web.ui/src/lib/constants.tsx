
/* export const SERVER_URL = 'http://api.blondy29.ovh:9090'; */
export const SERVER_URL = 'http://localhost:9090';

// TODO : Ajouter les types manquants : TypeActivite, Demandeur, Moniteur, ClubStructure
export type MoniteurJSON = {
  uuid?: string;
  lastname?: string;
  firstname?: string;
  niveau?: string;
  nbevents?: number;
};

export type ClubStructure = {
  uuid?: string;
  name?: string;
  nbevents?: number;
}

export type Demandeur = {
  uuid?: string;
  name?: string;
  numerostructure?: string;
  nbevents?: number;
}

export type Moniteur = {
  uuid: string;
  lastname: string;
  firstname: string;
  niveau: string;
}

export type TypeEvenement = {
  uuid?: string;
  name?: string;
  activite?: string;
  valeurforms?: string;
  nbevents?: number;
}

export type Session = {
  uuid: string;
  dateDebut: string;
  dateFin: string;
  typeSession: string;
}

export type EvenementJSON = {
  uuid?: string;
  evtidforms?: string;
  datedemande?: string;
  datedebut?: string;
  datefin?: string;
  typeEvenement?: TypeEvenement;
  demandeur?: Demandeur;
  partenaire?: Demandeur;
  mailcontact?: string;
  lieu?: string;
  presidentjury?: Moniteur;
  deleguectr?: Moniteur;
  repcibpl?: Moniteur;
  statut?: string;
  datevalidation?: string;
  organisateur?: ClubStructure;
  comment?: string;
  calendareventid?: string;
  sessions?: Session[];
};
