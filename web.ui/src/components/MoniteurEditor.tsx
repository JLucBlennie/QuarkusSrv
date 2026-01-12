import { MoniteurJSON, SERVER_URL } from "@/app/page";
import { useEffect, useState } from "react";
import { MoniteurColumn } from "./Moniteur-columns";
import { Button } from "./ui/button";

type MoniteurEditorProps = {
    uuid: String | undefined;
    onExit: () => void;
}
export function MoniteurEditor({ uuid, onExit }: MoniteurEditorProps) {
    const [moniteur, setMoniteur] = useState<MoniteurJSON>();
    const [error, setError] = useState<string | null>(null);
    const [createMode, setCreateMode] = useState<boolean>(false);
    const [loading, setLoading] = useState<boolean>(true);

    useEffect(() => {
        if (uuid === undefined) {
            setCreateMode(true);
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
                    let moniteurs: MoniteurColumn[] = [];
                    let id = 0;
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

    return (
        <div className="content-center">
            {error && !loading && <p>Erreur : {error}</p>}
            {!error && !createMode && !loading &&
                <p className="text-center">Vous avez cliqué sur une ligne ! {moniteur === null ? "Aucune ligne sélectionnée" : "UUID" + moniteur?.uuid}</p>
            }
            {createMode && !loading &&
                <p className="text-center">Mode création d'un nouvel événement.</p>
            }
            {loading && <p> </p>}
            <div>
                <Button onClick={onExit}>OK</Button>
            </div>
        </div>
    );
}