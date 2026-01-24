import { MoniteurJSON, SERVER_URL } from "@/lib/constants";
import { useEffect, useState } from "react";

type MoniteurEditorProps = {
    uuid: String | undefined;
    onExit: () => void;
}
export function MoniteurEditor({ uuid, onExit }: MoniteurEditorProps) {
    const [moniteur, setMoniteur] = useState<MoniteurJSON>();
    const [error, setError] = useState<string | null>(null);
    const [createMode, setCreateMode] = useState<boolean>(false);
    const [loading, setLoading] = useState<boolean>(true);
    const [success, setSuccess] = useState<string | null>(null);
    const [modified, setModified] = useState<boolean>(false);

    useEffect(() => {
        setLoading(true);
        if (uuid === undefined) {
            setCreateMode(true);
            setLoading(false);
        } else {
            fetch(`${SERVER_URL}/ctr/moniteurs/` + uuid, {
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
                    setMoniteur(data);
                    setLoading(false);
                })
                .catch((err) => {
                    console.error('Erreur fetch :', err);
                    setError(err.message);
                    setLoading(false);
                });
        }
    }, []);

    function handleSubmit() {
        // Logique de soumission du formulaire
        setError(null);
        setSuccess(null);

        const url = `${SERVER_URL}/ctr/moniteurs`;
        const method = uuid ? 'PUT' : 'POST';

        fetch(url, {
            method,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}`,
            },
            body: JSON.stringify(moniteur),
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
        setMoniteur((prev: MoniteurJSON | undefined) => ({
            ...(prev || {}), [name]: value
        }));
        setModified(true);
    };

    return (
        <div className="content-center">
            {error && !loading && <p>Erreur : {error}</p>}
            {createMode && !loading &&
                <p className="text-center">Mode création d'un nouveau moniteur.</p>
            }
            {!error && !loading &&
                <div>
                    <form onSubmit={handleSubmit} className="space-y-6">
                        <div className="grid grid-cols-2 grid-rows-5 gap-2 max-w-l mx-auto">
                            {/* Champ First Name */}
                            <div className="p-1">
                                <label htmlFor="firstname" className="text-sm font-medium text-white-700">
                                    Prénom
                                </label>
                                <input
                                    id="firstname"
                                    name="firstname"
                                    type="text"
                                    value={moniteur?.firstname || ''}
                                    onChange={handleChange}
                                    className="mt-1 w-full max-w-1/2 rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 p-2"
                                />
                            </div>

                            {/* Champ Last Name */}
                            <div className="p-1">
                                <label htmlFor="lastname" className="text-sm font-medium text-white-700">
                                    Nom
                                </label>
                                <input
                                    id="lastname"
                                    name="lastname"
                                    type="text"
                                    value={moniteur?.lastname || ''}
                                    onChange={handleChange}
                                    className="mt-1 w-full max-w-1/2 rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 p-2"
                                />
                            </div>

                            {/* Champ Nb d'evenements */}
                            <div className="p-1">
                                <label htmlFor="nbevent" className="text-sm font-medium text-white-700">
                                    Nombre d'événements rattachés
                                </label>
                                <input
                                    id="nbevent"
                                    name="nbevent"
                                    type="number"
                                    value={moniteur?.nbevents}
                                    className="mt-1 w-full max-w-1/2 rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 p-2"
                                    readOnly={true}
                                />
                            </div>

                            {/* Champ Niveau */}
                            <div className="p-1">
                                <label htmlFor="niveau" className="text-sm font-medium text-white-700 mb-1">
                                    Niveau *
                                </label>

                                <select
                                    id="niveau"
                                    name="niveau"
                                    value={moniteur?.niveau || ''}
                                    onChange={handleChange}
                                    className={`mt-1 w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm p-2`}
                                >
                                    <option value="">Sélectionnez un niveau</option>
                                    <option>MF1</option>
                                    <option>MF2</option>
                                    <option>IR</option>
                                    <option>IN</option>
                                    <option>TIV</option>
                                    <option>MFEH1</option>
                                    <option>MFEH2</option>
                                </select>
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
            {loading && <div className="text-center py-8">Chargement du moniteur...</div>}
        </div>
    );
}