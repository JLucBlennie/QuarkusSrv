"use client";

import { EvenementEditor } from "@/components/EvenementEditor";
import { useRouter } from "next/navigation";

export default function NouvelEvenementPage() {
    const router = useRouter();

    return (
        <div className="p-5">
            <EvenementEditor
                uuid={undefined}
                onExit={() => router.back()}
            />
        </div>
    );
}