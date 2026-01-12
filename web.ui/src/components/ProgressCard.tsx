import { Progress } from "./ui/progress";

interface ProgressCardProps {
    key: string;
    message: string;
    progress: number;
}

export default function ProgressCard(props: ProgressCardProps) {

    return (
        <>
        {
            <div className="transform bg-slate-900 bg-opacity-50 text-white p-4 rounded shadow-lg max-w-xs z-50 m-2">
            <h2 className="text-1xl font-bold mb-4">{props.message}</h2>
            <Progress key={props.key} value={props.progress} />
            </div>
        }
        </>
        )
}