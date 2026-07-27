import { useAuth } from "@/context/AuthContext";
import { authFetch } from "@/lib/authService";
import { ClubStructure, Demandeur, EvenementJSON, Moniteur, SERVER_URL, Session, TypeEvenement } from "@/lib/constants";
import { dateInputToTimestamp, timestampToDateInput } from "@/lib/utils";
import { useEffect, useState } from "react";
import { FaPlus, FaTrash } from "react-icons/fa6";
import { DataTable } from "./DataTable";
import { EventColumn } from "./Event-columns";
import { eventconflictcolumns } from "./EventConflict-columns";
import { Button } from "./ui/button";

type EventEditorProps = {
    uuid: String | undefined;
    onExit: () => void;
}

export function EvenementEditor({ uuid, onExit }: EventEditorProps) {
    const [event, setEvent] = useState<EvenementJSON>();
    const [eventTypes, setEventTypes] = useState<TypeEvenement[]>([]);
    const [eventDemandeurs, setEventDemandeurs] = useState<Demandeur[]>([]);
    const [eventOrganisateurs, setEventOrganisateurs] = useState<ClubStructure[]>([]);
    const [eventMoniteurs, setEventMoniteurs] = useState<Moniteur[]>([]);
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState<string | null>(null);
    const [createMode, setCreateMode] = useState<boolean>(false);
    const [loading, setLoading] = useState<boolean>(true);
    const [modified, setModified] = useState<boolean>(false);
    const [eventConflict, setEventConflict] = useState<EventColumn[]>([]);
    const [sessionErrors, setSessionErrors] = useState<Record<string, string>>({});
    const { hasRole } = useAuth();
    const today = new Date();

    useEffect(() => {
        setLoading(true);
        if (uuid === undefined) {
            setCreateMode(true);
            setLoading(false);
        } else {
            authFetch(`${SERVER_URL}/evenements/` + uuid, {
                method: "GET",
                redirect: "follow"
            })
                .then((res) => {
                    if (!res.ok) {
                        throw new Error(`Erreur serveur pour l\'evenement : ${res.status}`);
                    }
                    return res.json();
                })
                .then((data) => {
                    console.log('Réponse du serveur Quarkus pour l\'evenement :', data);
                    setEvent(data);
                    setLoading(false);
                    updateConflicts(data);
                })
                .catch((err) => {
                    console.error('Erreur fetch pour l\'evenement : ' + uuid, err);
                    setError(err.message);
                    setLoading(false);
                });
        }
        authFetch(`${SERVER_URL}/typeevenements/`, {
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
                setEventTypes(data);
                setLoading(false);
            })
            .catch((err) => {
                console.error('Erreur fetch :', err);
                setError(err.message);
                setLoading(false);
            });
        authFetch(`${SERVER_URL}/demandeurs/`, {
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
                setEventDemandeurs(data);
                setLoading(false);
            })
            .catch((err) => {
                console.error('Erreur fetch :', err);
                setError(err.message);
                setLoading(false);
            });
        authFetch(`${SERVER_URL}/clubstructures/`, {
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
                setEventOrganisateurs(data);
                setLoading(false);
            })
            .catch((err) => {
                console.error('Erreur fetch :', err);
                setError(err.message);
                setLoading(false);
            });
        authFetch(`${SERVER_URL}/moniteurs/`, {
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
                setEventMoniteurs(data);
                setLoading(false);
            })
            .catch((err) => {
                console.error('Erreur fetch :', err);
                setError(err.message);
                setLoading(false);
            });
    }, []);

    function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
        e.preventDefault();
        // Logique de soumission du formulaire
        setError(null);
        setSuccess(null);

        // Validation des sessions avant soumission
        const newSessionErrors: Record<string, string> = {};
        (event?.sessions || []).forEach(s => {
            if (!s.dateDebut) {
                newSessionErrors[s.uuid] = 'Date de début requise';
            } else if (!s.dateFin) {
                newSessionErrors[s.uuid] = 'Date de fin requise';
            } else if (s.dateDebut > s.dateFin) {
                newSessionErrors[s.uuid] = 'La date de début doit être antérieure à la date de fin';
            }
        });
        if (Object.keys(newSessionErrors).length > 0) {
            setSessionErrors(newSessionErrors);
            setError('Veuillez corriger les erreurs dans les sessions avant de soumettre.');
            return;
        }

        if (event?.datedemande == undefined) {
            setEvent((prev: EvenementJSON | undefined) => ({
                ...(prev || {}), datedemande: today.getTime()
            }));
        }

        // Nettoyer les UUIDs temporaires (temp_xxx) avant envoi :
        // le backend génère un vrai UUID si null est reçu (SessionDTO.toEntity)
        const eventToSend = {
            ...event,
            sessions: event?.sessions?.map(s => ({
                ...s,
                uuid: s.uuid?.startsWith('temp_') ? null : s.uuid
            }))
        };

        const url = `${SERVER_URL}/evenements`;
        const method = uuid ? 'PUT' : 'POST';

        authFetch(url, {
            method,
            body: JSON.stringify(eventToSend),
            redirect: 'follow'
        }).then((response) => {
            if (!response.ok) {
                throw new Error(`Échec de l'opération: ${response.statusText}`);
            } else {
                setSuccess(
                    uuid
                        ? 'Événement mis à jour avec succès !'
                        : 'Événement créé avec succès !'
                );
                setModified(false);
                onExit();
            }
        });
    };

    function updateConflicts(event?: EvenementJSON) {
        console.log("Update des conflits pour l'événement : ", event);
        if (event?.datedebut && event?.datefin) {
            authFetch(`${SERVER_URL}/evenements/conflict?debut=${timestampToDateInput(event.datedebut)}&fin=${timestampToDateInput(event.datefin)}`, {
                method: "GET",
                redirect: "follow"
            }).then((res) => {
                if (!res.ok && res.status !== 204) {
                    throw new Error(`Erreur serveur : ${res.status}`);
                } else if (res.status === 204) {
                    return [];
                }
                return res.json();
            }).then((data) => {
                console.log('Réponse du serveur Quarkus pour les conflits :', data);
                const conflicts: EventColumn[] = data.filter((evenement: EvenementJSON) => evenement.uuid !== event.uuid).map((evenement: EvenementJSON) => ({
                    uuid: (evenement.uuid || ''),
                    datedemande: (evenement.datedemande || 0),
                    statut: (evenement.statut || ''),
                    activite: (evenement.typeEvenement?.name === undefined) ? "Type null" : evenement.typeEvenement.name,
                    organisateur: evenement.organisateur?.name || "",
                    datedebut: (evenement.datedebut || 0),
                    datefin: (evenement.datefin || 0),
                    lieu: (evenement.lieu || '')
                }));
                setEventConflict(conflicts);
            }).catch((err) => {
                console.error('Erreur fetch pour les conflits :', err);
                setError(err.message);
            });
        }
    }

    function handleChange(e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) {
        const { name, value } = e.target;
        if (name.startsWith('date')) {
            setEvent((prev: EvenementJSON | undefined) => ({
                ...(prev || {}), [name]: dateInputToTimestamp(value)
            }));
        } else {
            setEvent((prev: EvenementJSON | undefined) => ({
                ...(prev || {}), [name]: value
            }));
        }
        setModified(true);
    };

    function handleTypeEvenementChange(e: React.ChangeEvent<HTMLSelectElement>) {
        const selectedTypeUUID = e.target.value;
        const selectedType = eventTypes.find(type => type.uuid === selectedTypeUUID);
        if (selectedType) {
            setEvent((prev: EvenementJSON | undefined) => ({
                ...(prev || {}), typeEvenement: selectedType
            }));
            setModified(true);
        }
    };

    function handleDemandeurChange(e: React.ChangeEvent<HTMLSelectElement>) {
        const selectedDemandeurUUID = e.target.value;
        const selectedDemandeur = eventDemandeurs.find(demandeur => demandeur.uuid === selectedDemandeurUUID);
        if (selectedDemandeur) {
            setEvent((prev: EvenementJSON | undefined) => ({
                ...(prev || {}), demandeur: selectedDemandeur
            }));
            setModified(true);
        }
    };

    function handleOrganisateurChange(e: React.ChangeEvent<HTMLSelectElement>) {
        const selectedOrganisationUUID = e.target.value;
        const selectedOrganisateur = eventOrganisateurs.find(organisateur => organisateur.uuid === selectedOrganisationUUID);
        if (selectedOrganisateur) {
            setEvent((prev: EvenementJSON | undefined) => ({
                ...(prev || {}), organisateur: selectedOrganisateur
            }));
            setModified(true);
        }
    };
    function handlePresidentJuryChange(e: React.ChangeEvent<HTMLSelectElement>) {
        const selectedMoniteurUUID = e.target.value;
        const selectedMoniteur = eventMoniteurs.find(moniteur => moniteur.uuid === selectedMoniteurUUID);
        if (selectedMoniteur) {
            setEvent((prev: EvenementJSON | undefined) => ({
                ...(prev || {}), presidentjury: selectedMoniteur
            }));
            setModified(true);
        }
    };
    function handleDelegueCTRChange(e: React.ChangeEvent<HTMLSelectElement>) {
        const selectedDelegueCTRUUID = e.target.value;
        const selectedDelegueCTR = eventMoniteurs.find(delegueCTR => delegueCTR.uuid === selectedDelegueCTRUUID);
        if (selectedDelegueCTR) {
            setEvent((prev: EvenementJSON | undefined) => ({
                ...(prev || {}), deleguectr: selectedDelegueCTR
            }));
            setModified(true);
        }
    };
    function handleRepCIBPLChange(e: React.ChangeEvent<HTMLSelectElement>) {
        const selectedRepCIBPLUUID = e.target.value;
        const selectedRepCIBPL = eventMoniteurs.find(repcibpl => repcibpl.uuid === selectedRepCIBPLUUID);
        if (selectedRepCIBPL) {
            setEvent((prev: EvenementJSON | undefined) => ({
                ...(prev || {}), repcibpl: selectedRepCIBPL
            }));
            setModified(true);
        }
    };

    function onValidate() {
        console.log('Validation de l\'événement :', event);
        setError(null);
        setSuccess(null);

        if (event?.datedemande == undefined) {
            setEvent((prev: EvenementJSON | undefined) => ({
                ...(prev || {}), datedemande: today.getTime()
            }));
        }

        const url = `${SERVER_URL}/evenements/validate?id=${uuid}`;
        const method = 'PUT';

        authFetch(url, {
            method,
            redirect: 'follow'
        }).then((response) => {
            if (!response.ok) {
                throw new Error(`Échec de l'opération: ${response.statusText}`);
            } else {
                setSuccess('Événement validé avec succès !');
            }
        });
    }
    function onRefuse() {
        console.log('Refus de l\'événement :', event);
        setError(null);
        setSuccess(null);

        const url = `${SERVER_URL}/evenements/refuse?id=${uuid}`;
        const method = 'PUT';

        authFetch(url, {
            method,
            redirect: 'follow'
        }).then((response) => {
            if (!response.ok) {
                throw new Error(`Échec de l'opération: ${response.statusText}`);
            } else {
                setSuccess('Événement refusé avec succès !');
            }
        });
    }

    function handleAddClick() {
        console.log('Ajouter une Session');
        const newSession: Session = {
            uuid: `temp_${Date.now()}`,
            dateDebut: 0,
            dateFin: 0,
            typeSession: 'PRESENTIEL'
        };
        setEvent((prev: EvenementJSON | undefined) => {
            if (!prev) return prev;
            const updatedSessions = prev.sessions ? [...prev.sessions, newSession] : [newSession];
            return { ...prev, sessions: updatedSessions };
        });
        setModified(true);
    }

    function handleSessionChange(e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) {
        const sessionId = e.target.dataset.sessionId!;
        const fieldName = e.target.dataset.field!;
        const value = e.target.value;

        const valueToSet = fieldName.startsWith('date')
            ? dateInputToTimestamp(value)
            : value;

        // Validation en temps réel sur les champs de dates
        if (fieldName === 'dateDebut' || fieldName === 'dateFin') {
            const currentSession = event?.sessions?.find(s => s.uuid === sessionId);
            if (currentSession) {
                const newDateDebut = fieldName === 'dateDebut' ? (valueToSet as number) : currentSession.dateDebut;
                const newDateFin = fieldName === 'dateFin' ? (valueToSet as number) : currentSession.dateFin;
                if (newDateDebut && newDateFin && newDateDebut > newDateFin) {
                    setSessionErrors(prev => ({ ...prev, [sessionId]: 'La date de début doit être antérieure à la date de fin' }));
                } else {
                    setSessionErrors(prev => {
                        const { [sessionId]: _, ...rest } = prev;
                        return rest;
                    });
                }
            }
        }

        setEvent((prev: EvenementJSON | undefined) => {
            if (!prev) return prev;
            const updatedSessions = (prev.sessions || []).map(s =>
                s.uuid !== sessionId ? s : { ...s, [fieldName]: valueToSet }
            );
            // Recalcul des dates de l'événement : min des débuts, max des fins
            const allDebuts = updatedSessions.map(s => s.dateDebut).filter(d => d && d > 0) as number[];
            const allFins = updatedSessions.map(s => s.dateFin).filter(d => d && d > 0) as number[];
            const newDebut = allDebuts.length > 0 ? Math.min(...allDebuts) : prev.datedebut;
            const newFin = allFins.length > 0 ? Math.max(...allFins) : prev.datefin;
            const updatedEvent = { ...prev, sessions: updatedSessions, datedebut: newDebut, datefin: newFin };
            updateConflicts(updatedEvent);
            return updatedEvent;
        });
        setModified(true);
    }

    function handleDeleteSession(sessionId: string) {
        setEvent((prev: EvenementJSON | undefined) => {
            if (!prev) return prev;
            const updatedSessions = (prev.sessions || []).filter(s => s.uuid !== sessionId);
            const allDebuts = updatedSessions.map(s => s.dateDebut).filter(d => d && d > 0) as number[];
            const allFins = updatedSessions.map(s => s.dateFin).filter(d => d && d > 0) as number[];
            const newDebut = allDebuts.length > 0 ? Math.min(...allDebuts) : undefined;
            const newFin = allFins.length > 0 ? Math.max(...allFins) : undefined;
            return { ...prev, sessions: updatedSessions, datedebut: newDebut, datefin: newFin };
        });
        setSessionErrors(prev => {
            const { [sessionId]: _, ...rest } = prev;
            return rest;
        });
        setModified(true);
    }


    return (
        <div className="content-center">
            {error && !loading && <p>Erreur : {error}</p>}
            {createMode && !loading &&
                <p className="text-center">Mode création d'un nouvel événement.</p>
            }
            {!error && !loading &&
                <div>
                    <form className="flex flex-col space-y-6" onSubmit={handleSubmit}>
                        <div className="grid grid-cols-4 grid-rows-3 gap-2 max-w-l mx-auto">
                            {/* Champ Date de Demande */}
                            <div className="p-1">
                                <label htmlFor="datedemande" className="text-sm font-medium text-white-700">
                                    Date de demande
                                </label>
                                <input
                                    id="datedemande"
                                    name="datedemande"
                                    type="date"
                                    value={event?.datedemande ? timestampToDateInput(event.datedemande) : timestampToDateInput(today.getTime())}
                                    onChange={handleChange}
                                    className="mt-1 w-full max-w-1/2 rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 p-2"
                                    readOnly={uuid !== undefined}
                                />
                            </div>

                            {/* Champ Demandeur */}
                            <div className="p-1">
                                <label htmlFor="demandeur" className="text-sm font-medium text-white-700 mb-1">
                                    Demandeur *
                                </label>

                                {loading ? (
                                    <select
                                        id="demandeur"
                                        disabled
                                        className={`mt-1 w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm p-2 bg-gray-100`}
                                    >
                                        <option>Chargement...</option>
                                    </select>
                                ) : error ? (
                                    <div className="p-2 border border-red-500 rounded bg-red-50 text-red-700">
                                        {error}
                                    </div>
                                ) : (
                                    <select
                                        id="demandeur"
                                        value={event?.demandeur?.uuid || undefined}
                                        onChange={(e) => { handleDemandeurChange(e); }}
                                        className={`mt-1 w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm p-2`}
                                    >
                                        <option value="">Sélectionnez un demandeur</option>
                                                {eventDemandeurs.sort((a, b) => a.name != undefined ? a.name.localeCompare(b.name != undefined ? b.name : '') : 0).map((demandeur) => (
                                            <option value={demandeur.uuid}>
                                                {demandeur.name} ({demandeur.numerostructure})
                                            </option>
                                        ))}
                                    </select>
                                )}
                            </div>

                            {/* Champ Date de début */}
                            <div className="p-1">
                                <label htmlFor="datedebut" className="text-sm font-medium text-white-700">
                                    Date de début
                                </label>
                                <input
                                    id="datedebut"
                                    name="datedebut"
                                    type="date"
                                    value={timestampToDateInput(event?.datedebut)}
                                    className="mt-1 w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 p-2"
                                    readOnly={true}
                                />
                            </div>

                            {/* Champ Date de fin */}
                            <div className="p-1">
                                <label htmlFor="datefin" className="text-sm font-medium text-white-700">
                                    Date de fin
                                </label>
                                <input
                                    id="datefin"
                                    name="datefin"
                                    type="date"
                                    value={timestampToDateInput(event?.datefin)}
                                    className="mt-1 w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 p-2"
                                    readOnly={true}
                                />
                            </div>

                            {/* Champ Contact */}
                            <div className="p-1">
                                <label htmlFor="mailcontact" className="text-sm font-medium text-white-700">
                                    Contact
                                </label>
                                <input
                                    id="mailcontact"
                                    name="mailcontact"
                                    type="email"
                                    value={event?.mailcontact || ''}
                                    onChange={handleChange}
                                    className="mt-1 w-full max-w-1/2 rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 p-2"
                                />
                            </div>

                            {/* Champ type événement */}
                            <div className="p-1">
                                <label htmlFor="event-type" className="text-sm font-medium text-white-700 mb-1">
                                    Type d'événement *
                                </label>

                                {loading ? (
                                    <select
                                        id="event-type"
                                        disabled
                                        className={`mt-1 w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm p-2 bg-gray-100`}
                                    >
                                        <option>Chargement...</option>
                                    </select>
                                ) : error ? (
                                    <div className="p-2 border border-red-500 rounded bg-red-50 text-red-700">
                                        {error}
                                    </div>
                                ) : (
                                    <select
                                        id="event-type"
                                        value={event?.typeEvenement?.uuid || undefined}
                                        onChange={(e) => { handleTypeEvenementChange(e); }}
                                        className={`mt-1 w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm p-2`}
                                    >
                                        <option value="">Sélectionnez un type</option>
                                                {eventTypes.sort((a, b) => a.name != undefined ? a.name.localeCompare(b.name != undefined ? b.name : '') : 0).map((type) => (
                                            <option value={type.uuid}>
                                                {type.name}
                                            </option>
                                        ))}
                                    </select>
                                )}
                            </div>

                            {/* Champ Lieu */}
                            <div className="p-1">
                                <label htmlFor="lieu" className="text-sm font-medium text-white-700">
                                    Lieu
                                </label>
                                <input
                                    id="lieu"
                                    name="lieu"
                                    type="text"
                                    value={event?.lieu || ''}
                                    onChange={handleChange}
                                    className="mt-1 w-full max-w-1/2 rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 p-2"
                                />
                            </div>

                            {/* Champ Organisateur */}
                            <div className="p-1">
                                <label htmlFor="organisateur" className="text-sm font-medium text-white-700 mb-1">
                                    Organisateur *
                                </label>

                                {loading ? (
                                    <select
                                        id="organisateur"
                                        disabled
                                        className={`mt-1 w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm p-2 bg-gray-100`}
                                    >
                                        <option>Chargement...</option>
                                    </select>
                                ) : error ? (
                                    <div className="p-2 border border-red-500 rounded bg-red-50 text-red-700">
                                        {error}
                                    </div>
                                ) : (
                                    <select
                                        id="organisateur"
                                        value={event?.organisateur?.uuid || undefined}
                                        onChange={(e) => { handleOrganisateurChange(e); }}
                                        className={`mt-1 w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm p-2`}
                                    >
                                        <option value="">Sélectionnez un organisateur</option>
                                                {eventOrganisateurs.sort((a, b) => a.name != undefined ? a.name.localeCompare(b.name != undefined ? b.name : '') : 0).map((organisateur) => (
                                            <option value={organisateur.uuid}>
                                                {organisateur.name}
                                            </option>
                                        ))}
                                    </select>
                                )}
                            </div>
                            <div className="col-span-4 grid grid-cols-3 grid-rows-1 gap-2 w-full max-w-l mx-auto">
                                <div className="p-1">
                                    <label htmlFor="presidentjury" className="text-sm font-medium text-white-700 mb-1">
                                        Président du jury *
                                    </label>
                                    {loading ? (
                                        <select
                                            id="presidentjury"
                                            disabled
                                            className={`mt-1 w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm p-2 bg-gray-100`}
                                        >
                                            <option>Chargement...</option>
                                        </select>
                                    ) : error ? (
                                        <div className="p-2 border border-red-500 rounded bg-red-50 text-red-700">
                                            {error}
                                        </div>
                                    ) : (
                                        <select
                                            id="presidentjury"
                                            value={event?.presidentjury?.uuid || undefined}
                                            onChange={(e) => { handlePresidentJuryChange(e); }}
                                            className={`mt-1 w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm p-2`}
                                        >
                                            <option value="">Sélectionnez un président du jury</option>
                                            {eventMoniteurs.sort((a, b) => a.lastname.localeCompare(b.lastname)).map((moniteur) => (
                                                <option value={moniteur.uuid}>
                                                    {moniteur.firstname} {moniteur.lastname} ({moniteur.niveau})
                                                </option>
                                            ))}
                                        </select>
                                    )}
                                </div>
                                <div className="p-1">
                                    <label htmlFor="deleguectr" className="text-sm font-medium text-white-700 mb-1">
                                        Délégué CTR *
                                    </label>
                                    {loading ? (
                                        <select
                                            id="deleguectr"
                                            disabled
                                            className={`mt-1 w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm p-2 bg-gray-100`}
                                        >
                                            <option>Chargement...</option>
                                        </select>
                                    ) : error ? (
                                        <div className="p-2 border border-red-500 rounded bg-red-50 text-red-700">
                                            {error}
                                        </div>
                                    ) : (
                                        <select
                                            id="deleguectr"
                                            value={event?.deleguectr?.uuid || undefined}
                                            onChange={(e) => { handleDelegueCTRChange(e); }}
                                            className={`mt-1 w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm p-2`}
                                        >
                                            <option value="">Sélectionnez un délégué CTR</option>
                                            {eventMoniteurs.sort((a, b) => a.lastname.localeCompare(b.lastname)).map((moniteur) => (
                                                <option value={moniteur.uuid}>
                                                    {moniteur.firstname} {moniteur.lastname} ({moniteur.niveau})
                                                </option>
                                            ))}
                                        </select>
                                    )}
                                </div>
                                <div className="p-1">
                                    <label htmlFor="repcibpl" className="text-sm font-medium text-white-700 mb-1">
                                        Représentant du Comité *
                                    </label>
                                    {loading ? (
                                        <select
                                            id="repcibpl"
                                            disabled
                                            className={`mt-1 w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm p-2 bg-gray-100`}
                                        >
                                            <option>Chargement...</option>
                                        </select>
                                    ) : error ? (
                                        <div className="p-2 border border-red-500 rounded bg-red-50 text-red-700">
                                            {error}
                                        </div>
                                    ) : (
                                        <select
                                            id="repcibpl"
                                            value={event?.repcibpl?.uuid || undefined}
                                            onChange={(e) => { handleRepCIBPLChange(e); }}
                                            className={`mt-1 w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm p-2`}
                                        >
                                            <option value="">Sélectionnez un représentant du comité</option>
                                            {eventMoniteurs.sort((a, b) => a.lastname.localeCompare(b.lastname)).map((moniteur) => (
                                                <option value={moniteur.uuid}>
                                                    {moniteur.firstname} {moniteur.lastname} ({moniteur.niveau})
                                                </option>
                                            ))}
                                        </select>
                                    )}
                                </div>
                            </div>
                            <div className="col-span-4 relative">
                                <div className="max-h-[200px] overflow-auto">
                                    {event?.sessions && event.sessions.length > 0 && (
                                        <label className="text-sm font-medium text-white-700 mb-1">
                                            Les Sessions :
                                        </label>
                                    )}
                                    {event?.sessions && [...event.sessions].sort((a, b) => (a.dateDebut && b.dateDebut) ? a.dateDebut - b.dateDebut : 0).map((session, index) => (
                                        <div key={session.uuid}>
                                            <div className="grid grid-cols-4 grid-rows-1 gap-2 w-full max-w-l mx-auto items-end">
                                                <div className="p-1">
                                                    <label htmlFor={`session${index + 1}dateDebut`} className="text-sm font-medium text-white-700 mb-1">
                                                        Session {index + 1} - Date de début *
                                                    </label>
                                                    <input
                                                        type="date"
                                                        data-session-id={session.uuid}
                                                        data-field="dateDebut"
                                                        id={`session${index + 1}dateDebut`}
                                                        value={timestampToDateInput(session?.dateDebut)}
                                                        onChange={handleSessionChange}
                                                        required
                                                        className={`mt-1 w-full rounded-md border shadow-sm focus:border-blue-500 focus:ring-blue-500 p-2 ${sessionErrors[session.uuid] ? 'border-red-500' : 'border-gray-300'}`}
                                                    />
                                                </div>
                                                <div className="p-1">
                                                    <label htmlFor={`session${index + 1}dateFin`} className="text-sm font-medium text-white-700 mb-1">
                                                        Session {index + 1} - Date de fin *
                                                    </label>
                                                    <input
                                                        type="date"
                                                        data-session-id={session.uuid}
                                                        data-field="dateFin"
                                                        id={`session${index + 1}dateFin`}
                                                        value={timestampToDateInput(session?.dateFin)}
                                                        onChange={handleSessionChange}
                                                        required
                                                        className={`mt-1 w-full rounded-md border shadow-sm focus:border-blue-500 focus:ring-blue-500 p-2 ${sessionErrors[session.uuid] ? 'border-red-500' : 'border-gray-300'}`}
                                                    />
                                                </div>
                                                <div className="p-1">
                                                    <label htmlFor={`session${index + 1}typeSession`} className="text-sm font-medium text-white-700 mb-1">
                                                        Session {index + 1} - Type *
                                                    </label>
                                                    <select
                                                        id={`session${index + 1}typeSession`}
                                                        data-session-id={session.uuid}
                                                        data-field="typeSession"
                                                        onChange={handleSessionChange}
                                                        value={session?.typeSession || 'PRESENTIEL'}
                                                        className="mt-1 w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm p-2"
                                                    >
                                                        <option>PRESENTIEL</option>
                                                        <option>DISTANCIEL</option>
                                                        <option>MIXTE</option>
                                                    </select>
                                                </div>
                                                <div className="p-1 flex items-end">
                                                    <button
                                                        type="button"
                                                        onClick={() => handleDeleteSession(session.uuid)}
                                                        title="Supprimer la session"
                                                        className="mt-1 w-full flex items-center justify-center gap-1 px-2 py-2 rounded-md border border-red-400 text-red-400 hover:bg-red-900 hover:text-white transition-colors text-sm"
                                                    >
                                                        <FaTrash className="h-3 w-3" />
                                                        Supprimer
                                                    </button>
                                                </div>
                                            </div>
                                            {sessionErrors[session.uuid] && (
                                                <p className="text-red-400 text-xs px-1 pb-1">
                                                    ⚠ {sessionErrors[session.uuid]}
                                                </p>
                                            )}
                                        </div>
                                    ))}
                                </div>
                                {/* Bouton Ajouter une session */}
                                <Button className="absolute bottom-6 right-6 flex items-center justify-center w-12 h-12 bg-blue-600 text-white rounded-full shadow-lg hover:bg-blue-700 transition-colors z-10" onClick={handleAddClick}>
                                    <FaPlus className="h-6 w-6" />
                                </Button>
                            </div>
                        </div>
                        <div className="grid grid-cols-2 grid-rows-1 gap-2 w-full max-w-l mx-auto">
                            {/* Tableau des conflits d'evenement */}
                            <div className="p-1">
                                <label className="text-sm font-medium text-white-700">Conflits d'événement</label>
                                <DataTable
                                    columns={eventconflictcolumns}
                                    data={eventConflict}
                                    onRowClick={() => { }}
                                    height="200px"
                                    hiddenColumns={{ statut: false }}
                                    rowClassName={(row) =>
                                        row.getValue("statut") === "VALIDE" ? "bg-emerald-800 bg-opacity-70"
                                            : row.getValue("statut") === "DEMANDE" ? "bg-orange-800 bg-opacity-70"
                                                : row.getValue("statut") === "REFUSE" ? "bg-red-800 bg-opacity-70"
                                                    : ""
                                    }
                                />
                            </div>

                            {/* Champ Commentaire */}
                            <div className="p-1">
                                <label htmlFor="comment" className="text-sm font-medium text-white-700">
                                    Commentaire
                                </label>
                                <textarea
                                    id="comment"
                                    name="comment"
                                    value={event?.comment || ''}
                                    onChange={handleChange}
                                    className="mt-1 w-full max-w-1/2 rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 p-2"
                                />
                            </div>
                        </div>

                        {/* Boutons d'action */}
                        <div className="flex justify-end space-x-3">
                            <button
                                type="button"
                                onClick={onExit}
                                className="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-white-700 hover:bg-gray-50"
                            >
                                Annuler
                            </button>
                            <button
                                type="button"
                                onClick={onValidate}
                                disabled={uuid === undefined || modified}
                                hidden={!hasRole("admin")}
                                className="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-white-700 enabled:hover:bg-gray-50 disabled:opacity-50"
                            >
                                Valider
                            </button>
                            <button
                                type="button"
                                onClick={onRefuse}
                                disabled={uuid === undefined || modified}
                                hidden={!hasRole("admin")}
                                className="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-white-700 enabled:hover:bg-gray-50 disabled:opacity-50"
                            >
                                Refuser
                            </button>
                            <button
                                type="submit"
                                disabled={!modified}
                                className="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 enabled:hover:bg-gray-50 disabled:opacity-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                            >
                                {uuid ? 'Mettre à jour' : 'Créer'}
                            </button>
                        </div>
                    </form>
                </div >
            }
            {loading && <div className="text-center py-8">Chargement de l'événement...</div>}
        </div>
    );
}