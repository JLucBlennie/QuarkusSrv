"use client";

import { ThemeToggle } from "@/components/ui/theme-toggle";
import { useAuth } from "@/context/AuthContext";
import { useRouter } from "next/navigation";
import { FormEvent, useState } from "react";

export default function LoginPage() {
    const { login } = useAuth();
    const router = useRouter();

    const [username, setUsername] = useState<string>("");
    const [password, setPassword] = useState<string>("");
    const [error, setError] = useState<string>("");
    const [loading, setLoading] = useState<boolean>(false);

    async function handleSubmit(e: FormEvent<HTMLFormElement>): Promise<void> {
        e.preventDefault();
        setError("");
        setLoading(true);

        try {
            await login(username, password);
            router.push("/");   // ← redirige vers la page principale
        } catch {
            setError("Identifiants incorrects. Veuillez réessayer.");
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="relative min-h-screen bg-logo-35op bg-no-repeat bg-center bg-contain flex items-center justify-center">

            {/* Bouton thème en haut à droite */}
            <div className="absolute top-2 right-5 bg-slate-900 bg-opacity-50 text-white p-2 rounded shadow-lg z-10">
                <ThemeToggle />
            </div>

            <div className="bg-slate-900 bg-opacity-80 text-white p-8 rounded-lg shadow-xl w-full max-w-sm">
                <h1 className="text-2xl font-bold text-center mb-6">Calendrier de la CTR</h1>
                <h2 className="text-lg font-semibold text-center mb-6">Connexion</h2>

                {error && (
                    <div className="mb-4 p-3 bg-red-600 bg-opacity-80 rounded text-sm text-center">
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label htmlFor="name" className="block text-sm font-medium mb-1">Utilisateur</label>
                        <input
                            type="text"
                            id="name"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                            autoFocus
                            className="w-full rounded-md border border-gray-600 bg-slate-800 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                    </div>

                    <div>
                        <label htmlFor="password" className="block text-sm font-medium mb-1">Mot de passe</label>
                        <input
                            type="password"
                            id="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                            className="w-full rounded-md border border-gray-600 bg-slate-800 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                    </div>

                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full py-2 px-4 bg-blue-600 hover:bg-blue-700 disabled:opacity-50 text-white font-semibold rounded-md transition-colors"
                    >
                        {loading ? "Connexion..." : "Se connecter"}
                    </button>
                </form>
            </div>
        </div>
    );
}