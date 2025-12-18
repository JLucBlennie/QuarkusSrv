import { EvenementJSON } from "./EvenementsList";
import { EventColumn } from "./Event-columns";
import { Button } from "./ui/button";

type EventEditorProps = {
    selectedRow: EventColumn | null;
    events: EvenementJSON[];
    onExit: () => void;
}
export function EvenementEditor({ selectedRow, events, onExit }: EventEditorProps) {

    function getEvemenentByUuid(uuid: string): EvenementJSON | undefined {
        console.log('Recherche de l\'événement avec UUID :', uuid);
        const returnVal: EvenementJSON | undefined = events.find(event => event.uuid === uuid);
        console.log('Événement trouvé :', returnVal);
        return returnVal;
    }
    return (
        <div className="content-center">
            <p className="text-center">Vous avez cliqué sur une ligne ! {selectedRow === null ? "Aucune ligne sélectionnée" : getEvemenentByUuid(selectedRow.uuid)?.datedemande}</p>
            <div>
                <Button onClick={onExit}>OK</Button>
            </div>
        </div>
    );
}