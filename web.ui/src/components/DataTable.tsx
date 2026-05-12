"use client";

import {
  ColumnDef, ColumnFiltersState, flexRender,
  getCoreRowModel, getFilteredRowModel, getSortedRowModel,
  Row, SortingState, useReactTable,
} from "@tanstack/react-table";

import {
  Table, TableBody, TableCell,
  TableHead, TableHeader, TableRow,
} from "@/components/ui/table";

import { useState } from "react";

interface DataTableProps<TData, TValue> {
  columns: ColumnDef<TData, TValue>[];
  data: TData[];
  onRowClick: (row: TData) => void;
  height?: string;                            // 👈 ex: "700px" ou "200px"
  rowClassName?: (row: Row<TData>) => string; // 👈 logique de coloration externalisée
  hiddenColumns?: Record<string, boolean>;    // 👈 ex: { statut: false }
}

export function DataTable<TData, TValue>({
  columns,
  data,
  onRowClick,
  height = "700px",          // valeur par défaut
  rowClassName,
  hiddenColumns,
}: DataTableProps<TData, TValue>) {
  const [sorting, setSorting] = useState<SortingState>([]);
  const [columnFilters, setColumnFilters] = useState<ColumnFiltersState>([]);

  const table = useReactTable({
    data,
    columns,
    getCoreRowModel: getCoreRowModel(),
    onSortingChange: setSorting,
    getSortedRowModel: getSortedRowModel(),
    onColumnFiltersChange: setColumnFilters,
    getFilteredRowModel: getFilteredRowModel(),
    enableHiding: !!hiddenColumns,
    state: { sorting, columnFilters },
    initialState: {
      columnVisibility: hiddenColumns ?? {},
    },
  });

  return (
    <div className="overflow-auto rounded-md border" style={{ height }}>
      <Table>
        <TableHeader className="sticky top-0 z-10 bg-background shadow-[inset_0_-1px_0] shadow-border">
          {table.getHeaderGroups().map((headerGroup) => (
            <TableRow key={headerGroup.id}>
              {headerGroup.headers.map((header) => (
                <TableHead key={header.id}>
                  {header.isPlaceholder ? null : flexRender(
                    header.column.columnDef.header,
                    header.getContext()
                  )}
                </TableHead>
              ))}
            </TableRow>
          ))}
        </TableHeader>
        <TableBody>
          {table.getRowModel().rows?.length ? (
            table.getRowModel().rows.map((row) => (
              <TableRow
                key={row.id}
                data-state={row.getIsSelected() && "selected"}
                onClick={() => onRowClick(row.original)}
                className={rowClassName?.(row) ?? ""}  // 👈 coloration déléguée à l'appelant
              >
                {row.getVisibleCells().map((cell) => (
                  <TableCell key={cell.id}>
                    {flexRender(cell.column.columnDef.cell, cell.getContext())}
                  </TableCell>
                ))}
              </TableRow>
            ))
          ) : (
            <TableRow>
              <TableCell colSpan={columns.length} className="h-24 text-center">
                Aucun résultat.
              </TableCell>
            </TableRow>
          )}
        </TableBody>
      </Table>
    </div>
  );
}