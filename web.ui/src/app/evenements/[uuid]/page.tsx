'use client';
import { EvenementEditor } from '@/components/EvenementEditor';
import { useParams, useRouter } from 'next/navigation';

export default function EditEvenementPage() {
    const { uuid } = useParams<{ uuid: string }>();
    const router = useRouter();
    return <EvenementEditor uuid={uuid} onExit={() => router.push('/evenements')} />;
}