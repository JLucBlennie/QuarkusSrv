import { ClubStructure, Demandeur, EvenementJSON, Moniteur, SERVER_URL, Session, TypeEvenement } from "@/lib/constants";
import { useEffect, useState } from "react";
import { FaPlus } from "react-icons/fa6";
import { Button } from "./ui/button";

type EventEditorProps = {
    uuid: String | undefined;
    onExit: () => void;
}

// Fonction utilitaire pour parser les dates ISO
function parseDateForInput(isoDate: string): string {
    return isoDate.split('T')[0]; // "2024-12-31T12:00:00" → "2024-12-31"
};

// Fonction utilitaire pour formater les dates pour l'API
function formatDateForAPI(date: string): string {
    return `${date}T23:00:00.000+00:00`; // "2024-12-31" → "2024-12-31T23:00:00.000+00:00"
};

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
    const today = new Date();

    useEffect(() => {
        setLoading(true);
        if (uuid === undefined) {
            setCreateMode(true);
            setLoading(false);
        } else {
            fetch(`${SERVER_URL}/ctr/evenements/` + uuid, {
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
                })
                .catch((err) => {
                    console.error('Erreur fetch pour l\'evenement : ' + uuid, err);
                    setError(err.message);
                    setLoading(false);
                });
        }
        fetch(`${SERVER_URL}/ctr/typeevenements/`, {
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
        fetch(`${SERVER_URL}/ctr/demandeurs/`, {
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
        fetch(`${SERVER_URL}/ctr/clubstructures/`, {
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
        fetch(`${SERVER_URL}/ctr/moniteurs/`, {
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

    function handleSubmit() {
        // Logique de soumission du formulaire
        setError(null);
        setSuccess(null);

        const url = `${SERVER_URL}/ctr/evenements`;
        const method = uuid ? 'PUT' : 'POST';

        fetch(url, {
            method,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}`,
            },
            body: JSON.stringify(event),
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

    function handleChange(e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) {
        const { name, value } = e.target;
        setEvent((prev: EvenementJSON | undefined) => ({
            ...(prev || {}), [name]: value
        }));
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

        const url = `${SERVER_URL}/ctr/evenements/validate?id=${uuid}`;
        const method = 'PUT';

        fetch(url, {
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

        const url = `${SERVER_URL}/ctr/evenements/refuse?id=${uuid}`;
        const method = 'PUT';

        fetch(url, {
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
            uuid: '',
            dateDebut: '',
            dateFin: '',
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
        const { name, value } = e.target;
        const sessionIndex = parseInt(name.match(/\d+/)?.[0] || '0', 10) - 1;
        const fieldName = name.replace(`session${sessionIndex + 1}`, '');
        var valueToSet = value;
        let dateOk = false;
        if (fieldName.startsWith('date')) {
            dateOk = (value.split('-')[0] === today.getFullYear().toString());
            valueToSet = formatDateForAPI(value);
        }
        setEvent((prev: EvenementJSON | undefined) => {
            if (!prev) return prev;
            const updatedSessions = prev.sessions ? [...prev.sessions] : [];
            if (updatedSessions[sessionIndex]) {
                (updatedSessions[sessionIndex] as any)[fieldName] = valueToSet;
            }
            if (fieldName === 'dateDebut' && valueToSet && dateOk) {
                if (!prev.datedebut || prev.datedebut.localeCompare(valueToSet) > 0) {
                    const updatedEvent = { ...prev, datedebut: valueToSet, sessions: updatedSessions };
                    return updatedEvent;
                } else {
                    return { ...prev, sessions: updatedSessions };
                }
            } else if (fieldName === 'dateFin' && valueToSet && dateOk) {
                if (!prev.datefin || prev.datefin.localeCompare(valueToSet) < 0) {
                    const updatedEvent = { ...prev, datefin: valueToSet, sessions: updatedSessions };
                    return updatedEvent;
                } else {
                    return { ...prev, sessions: updatedSessions };
                }
            } else {
                return { ...prev, sessions: updatedSessions };
            }
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
                    <form className="space-y-6">
                        <div className="grid grid-cols-2 grid-rows-5 gap-2 max-w-l mx-auto">
                            {/* Champ Date de Demande */}
                            <div className="p-1">
                                <label htmlFor="datedemande" className="text-sm font-medium text-white-700">
                                    Date de demande
                                </label>
                                <input
                                    id="datedemande"
                                    name="datedemande"
                                    type="date"
                                    value={event?.datedemande?.split('T')[0] || today.toISOString().split('T')[0]}
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
                                    value={event?.datedebut?.split('T')[0]}
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
                                    value={event?.datefin?.split('T')[0]}
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
                            <div className="col-span-2 grid grid-cols-3 grid-rows-1 gap-2 w-full max-w-l mx-auto">
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
                            <div className="col-span-2">
                                {event?.sessions && event.sessions.length > 0 && (
                                    <label className="text-sm font-medium text-white-700 mb-1">
                                        Les Sessions :
                                    </label>
                                )}
                                {event?.sessions && event.sessions.map((session, index) => (
                                    <div key={index} className="grid grid-cols-3 grid-rows-1 gap-2 w-full max-w-l mx-auto">
                                        <div className="p-1">
                                            <label htmlFor={`session${index + 1}dateDebut`} className="text-sm font-medium text-white-700 mb-1">
                                                Session {index + 1} - Date de début *
                                            </label>
                                            <input
                                                id={`session${index + 1}dateDebut`}
                                                name={`session${index + 1}dateDebut`}
                                                type="date"
                                                value={session?.dateDebut?.split('T')[0] || ''}
                                                onChange={handleSessionChange}
                                                required
                                                className="mt-1 w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 p-2"
                                            />
                                        </div>
                                        <div className="p-1">
                                            <label htmlFor={`session${index + 1}dateFin`} className="text-sm font-medium text-white-700 mb-1">
                                                Session {index + 1} - Date de fin *
                                            </label>
                                            <input
                                                id={`session${index + 1}dateFin`}
                                                name={`session${index + 1}dateFin`}
                                                type="date"
                                                value={session?.dateFin?.split('T')[0] || ''}
                                                onChange={handleSessionChange}
                                                required
                                                className="mt-1 w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 p-2"
                                            />
                                        </div>
                                        <div className="p-1">
                                            <label htmlFor={`session${index + 1}typeSession`} className="text-sm font-medium text-white-700 mb-1">
                                                Session {index + 1} - Type *
                                            </label>
                                            <select
                                                id={`session${index + 1}typeSession`}
                                                name={`session${index + 1}typeSession`}
                                                onChange={handleSessionChange}
                                                className={`mt-1 w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm p-2`}
                                            >
                                                <option>PRESENTIEL</option>
                                                <option>DISTANCIEL</option>
                                                <option>MIXTE</option>
                                            </select>
                                        </div>
                                    </div>
                                ))}
                                {/* Bouton Ajouter une session */}
                                <Button className="fixed bottom-56 right-6 flex items-center justify-center w-12 h-12 bg-blue-600 text-white rounded-full shadow-lg hover:bg-blue-700 transition-colors z-10" onClick={handleAddClick}>
                                    <FaPlus className="h-6 w-6" />
                                </Button>
                            </div>
                        </div>

                        {/* Tableau des conflits d'evenement */}

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
                                className="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-white-700 enabled:hover:bg-gray-50 disabled:opacity-50"
                            >
                                Valider
                            </button>
                            <button
                                type="button"
                                onClick={onRefuse}
                                disabled={uuid === undefined || modified}
                                className="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-white-700 enabled:hover:bg-gray-50 disabled:opacity-50"
                            >
                                Refuser
                            </button>
                            <button
                                type="submit"
                                onClick={handleSubmit}
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