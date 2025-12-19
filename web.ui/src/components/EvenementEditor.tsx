import { useEffect, useState } from "react";
import { EvenementJSON } from "./EvenementsList";
import { EventColumn } from "./Event-columns";
import { Button } from "./ui/button";

type EventEditorProps = {
    uuid: String | undefined;
    onExit: () => void;
}
export function EvenementEditor({ uuid, onExit }: EventEditorProps) {
    const [event, setEvent] = useState<EvenementJSON>();
    const [error, setError] = useState<string | null>(null);
    const [createMode, setCreateMode] = useState<boolean>(false);
    const [loading, setLoading] = useState<boolean>(true);

    useEffect(() => {
        if (uuid === undefined) {
            setCreateMode(true);
        } else {
            fetch('http://localhost:9090/ctr/evenements/' + uuid, {
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
                    setEvent(data);
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
                <p className="text-center">Vous avez cliqué sur une ligne ! {event === null ? "Aucune ligne sélectionnée" : "UUID" + event?.uuid}</p>
            }
            {loading && <p> </p>}
            <div>
                <Button onClick={onExit}>OK</Button>
            </div>
        </div>
    );
}