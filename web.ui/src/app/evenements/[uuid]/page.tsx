'use client';
import { EvenementEditor } from '@/components/EvenementEditor';
import { useParams, useRouter } from 'next/navigation';

export default function EditEvenementPage() {
    const { uuid } = useParams<{ uuid: string }>();
    const router = useRouter();
    return (
        <div className="p-5 relative min-h-screen bg-logo-35op bg-no-repeat bg-center bg-contain">
            <EvenementEditor
                uuid={uuid}
                onExit={() => router.back()}
            />
        </div>
    );
}