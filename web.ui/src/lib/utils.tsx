import { clsx, type ClassValue } from "clsx";
import { twMerge } from "tailwind-merge";

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

// Fonction utilitaire pour convertir un timestamp en objet Date
export function timestampToDate(timestamp: number): Date {
  return new Date(timestamp);
};

// Fonction utilitaire pour formater une date en string locale
export function formatDateLocale(date: Date, locale: string = 'fr-FR'): string {
  return date.toLocaleString(locale, {
    timeZone: 'Europe/Paris', // Fuseau horaire de l'utilisateur
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',  });
};

// Convertit une date au format YYYY-MM-DD (pour l'input) en timestamp
export function dateInputToTimestamp(dateString: string): number {
  const [year,month,day] = dateString.split('-').map(Number);
  const date = new Date(Date.UTC(year, month - 1, day)); // Les mois commencent à 0
  return date.getTime();
};

// Convertit un timestamp en format YYYY-MM-DD (pour pré-remplir l'input)
export function timestampToDateInput(timestamp: number | null | undefined): string {
  if (!timestamp) return '';
  const date = new Date(timestamp);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0'); // Les mois commencent à 0
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`; // Format YYYY-MM-DD
};