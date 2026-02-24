
/* export const SERVER_URL = 'http://api.blondy29.ovh:9090'; */
export const SERVER_URL = `${process.env.NEXT_PUBLIC_API_URL}`;
export const WS_URL = `${process.env.NEXT_PUBLIC_WS_URL}`;

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
  dateDebut: number | null;
  dateFin: number | null;
  typeSession: string;
}

export type EvenementJSON = {
  uuid?: string;
  evtidforms?: string;
  datedemande?: number | null;
  datedebut?: number | null;
  datefin?: number | null;
  typeEvenement?: TypeEvenement;
  demandeur?: Demandeur;
  partenaire?: Demandeur;
  mailcontact?: string;
  lieu?: string;
  presidentjury?: Moniteur;
  deleguectr?: Moniteur;
  repcibpl?: Moniteur;
  statut?: string;
  datevalidation?: number | null;
  organisateur?: ClubStructure;
  comment?: string;
  calendareventid?: string;
  nbparticipants?: number;
  sessions?: Session[];
};
