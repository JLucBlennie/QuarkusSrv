import { Key } from "react";
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "./ui/card";
import { Label } from "./ui/label";
import { Progress } from "./ui/progress";



export default function ProgressCard(props: { key: any | null | undefined; message: any; progress: any;}) {

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