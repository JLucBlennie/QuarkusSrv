import { SERVER_URL, TypeEvenement } from "@/lib/constants";
import { useEffect, useState } from "react";

type TypeEvenementEditorProps = {
    uuid: String | undefined;
    onExit: () => void;
}
export function TypeEvenementEditor({ uuid, onExit }: TypeEvenementEditorProps) {
    const [typeEvenement, setTypeEvenement] = useState<TypeEvenement>();
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
            fetch(`${SERVER_URL}/ctr/typeevenements/` + uuid, {
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
                    setTypeEvenement(data);
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

        const url = `${SERVER_URL}/ctr/typeevenements`;
        const method = uuid ? 'PUT' : 'POST';

        fetch(url, {
            method,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}`,
            },
            body: JSON.stringify(typeEvenement),
            redirect: 'follow'
        }).then((response) => {
            if (!response.ok) {
                throw new Error(`Échec de l'opération: ${response.statusText}`);
            } else {
                setSuccess(
                    uuid
                        ? 'TypeEvenement mis à jour avec succès !'
                        : 'TypeEvenement créé avec succès !'
                );
                setModified(false);
                onExit();
            }
        });
    };

    function handleChange(e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) {
        const { name, value } = e.target;
        setTypeEvenement((prev:TypeEvenement | undefined) => ({
            ...(prev || {}), [name]: value
        }));
        setModified(true);
    };

    return (
        <div className="content-center">
            {error && !loading && <p>Erreur : {error}</p>}
            {createMode && !loading &&
                <p className="text-center">Création d'un nouveau Type d'événements.</p>
            }
            {!error && !loading &&
                <div>
                    <p className="text-center">Modification d'un Type d'événements.</p>
                    <form onSubmit={handleSubmit} className="space-y-6">
                        <div className="grid grid-cols-2 grid-rows-5 gap-2 max-w-l mx-auto">
                            {/* Champ First Name */}
                            <div className="p-1">
                                <label htmlFor="name" className="text-sm font-medium text-white-700">
                                    Nom
                                </label>
                                <input
                                    id="name"
                                    name="name"
                                    type="text"
                                    value={typeEvenement?.name || ''}
                                    onChange={handleChange}
                                    className="mt-1 w-full max-w-1/2 rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 p-2"
                                />
                            </div>

                            {/* Champ Numéro de structure */}
                            <div className="p-1">
                                <label htmlFor="activite" className="text-sm font-medium text-white-700">
                                    Activité
                                </label>
                                <select
                                    id="activite"
                                    name="activite"
                                    value={typeEvenement?.activite || ''}
                                    onChange={handleChange}
                                    className={`mt-1 w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm p-2`}
                                >
                                    <option value="">Sélectionnez un type d'activité</option>
                                    <option>Tout</option>
                                    <option>N4 - GP</option>
                                    <option>Initiateur</option>
                                    <option>TSI</option>
                                    <option>MF1</option>
                                    <option>MF2</option>
                                    <option>TIV</option>
                                    <option>Secourisme</option>
                                    <option>HandiSub</option>
                                </select>
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
                                    value={typeEvenement?.nbevents}
                                    className="mt-1 w-full max-w-1/2 rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 p-2"
                                    readOnly={true}
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
            {loading && <div className="text-center py-8">Chargement du Demandeur...</div>}
        </div>
    );
}