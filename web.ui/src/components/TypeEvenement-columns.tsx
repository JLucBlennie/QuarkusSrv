"use client";

import { Button } from "@/components/ui/button";
import { ColumnDef } from "@tanstack/react-table";
import { ArrowUpDown } from "lucide-react";

export type TypeEvenementColumn = {
    uuid: string;
    name: string;
    activite: string;
    nbevents: number;
};

export const typeevenementcolumns: ColumnDef<TypeEvenementColumn>[] = [
    {
        accessorKey: "name",
        header: ({ column }) => {
            return (
                <Button
                    variant="ghost"
                    onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
                >
                    <div className="text-center">Nom</div>
                    <ArrowUpDown className="ml-2 h-4 w-4" />
                </Button>
            );
        }
    },
    {
        accessorKey: "activite",
        header: ({ column }) => {
            return (
                <Button
                    variant="ghost"
                    onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
                >
                    <div className="text-center">Nom</div>
                    <ArrowUpDown className="ml-2 h-4 w-4" />
                </Button>
            );
        }
    },
    {
        accessorKey: "nbevents",
        header: ({ column }) => {
            return (
                <Button
                    variant="ghost"
                    onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
                >
                    <div className="text-center">Nb évènements</div>
                    <ArrowUpDown className="ml-2 h-4 w-4" />
                </Button>
            );
        }
    }
];
