import { SERVER_URL } from "./constants";

export interface AuthResponse {
    token: string;
    username: string;
    roles: string[];
}

export interface StoredUser {
    username: string;
    roles: string[];
}

export async function login(username: string, password: string): Promise<AuthResponse> {
    const response = await fetch(`${SERVER_URL}/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password }),
    });

    if (!response.ok) {
        throw new Error("Identifiants incorrects");
    }

    const data: AuthResponse = await response.json();
    localStorage.setItem("token", data.token);
    localStorage.setItem("user", JSON.stringify({
        username: data.username,
        roles: data.roles,
    }));

    return data;
}

export function getToken(): string | null {
    if (typeof window === "undefined") return null;
    return localStorage.getItem("token");
}

export function getStoredUser(): StoredUser | null {
    if (typeof window === "undefined") return null;
    const raw = localStorage.getItem("user");
    return raw ? JSON.parse(raw) as StoredUser : null;
}

export function logout(): void {
    if (typeof window === "undefined") return;
    localStorage.removeItem("token");
    localStorage.removeItem("user");
}

export function authFetch(url: string, options: RequestInit = {}): Promise<Response> {
    return fetch(url, {
        ...options,
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${getToken()}`,
            ...options.headers,
        },
    });
}