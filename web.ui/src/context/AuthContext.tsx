"use client";

import {
    login as apiLogin,
    logout as apiLogout,
    AuthResponse,
    getStoredUser,
    StoredUser,
} from "@/lib/authService";
import { useRouter } from "next/navigation";
import {
    createContext,
    ReactNode,
    useCallback,
    useContext,
    useEffect,
    useState,
} from "react";

interface AuthContextType {
    user: StoredUser | null;
    isAuthenticated: boolean;
    login: (username: string, password: string) => Promise<AuthResponse>;
    logout: () => void;
    hasRole: (role: string) => boolean;
}

const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
    const [user, setUser] = useState<StoredUser | null>(null);
    const router = useRouter();

    // Restaure l'utilisateur depuis le localStorage au démarrage
    useEffect(() => {
        const stored = getStoredUser();
        if (stored) {
            setUser(stored);
        } else {
            router.push("/login");
        }
    }, []);

    const login = useCallback(async (username: string, password: string): Promise<AuthResponse> => {
        const data = await apiLogin(username, password);
        setUser({ username: data.username, roles: data.roles });
        return data;
    }, []);

    const logout = useCallback((): void => {
        apiLogout();
        setUser(null);
        router.push("/login");
    }, []);

    const hasRole = useCallback((role: string): boolean => {
        return user?.roles?.includes(role) ?? false;
    }, [user]);

    return (
        <AuthContext.Provider value={{ user, login, logout, hasRole, isAuthenticated: !!user }}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth(): AuthContextType {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error("useAuth doit être utilisé dans un AuthProvider");
    }
    return context;
}