"use client";

import { EvenementEditor } from "@/components/EvenementEditor";
import { useRouter } from "next/navigation";

export default function NouvelEvenementPage() {
    const router = useRouter();

    return (
        <div className="p-5 relative min-h-screen bg-logo-35op bg-no-repeat bg-center bg-contain">
            <EvenementEditor
                uuid={undefined}
                onExit={() => router.back()}
            />
        </div>
    );
}