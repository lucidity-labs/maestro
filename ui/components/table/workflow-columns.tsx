import {ColumnDef} from "@tanstack/react-table";

export type Event = {
    workflowId: string
    category: string
    className: string
    functionName: string
    startTimestamp: string
    endTimestamp: string
    input: string
    output: string
}

export type Workflow = {
    workflowId: string
    className: string
    functionName: string
    startTimestamp: string
    endTimestamp: string
    input: string
    output: string
}

const commonColumns = [
    {
        accessorKey: "className",
        header: "Class",
    },
    {
        accessorKey: "functionName",
        header: "Function",
    },
    {
        accessorKey: "input",
        header: "Input",
    },
    {
        accessorKey: "output",
        header: "Output",
    },
    {
        accessorKey: "startTimestamp",
        header: "Start",
    },
    {
        accessorKey: "endTimestamp",
        header: "End",
    },
]

export const eventColumns: ColumnDef<Event>[] = [
    {
        accessorKey: "category",
        header: "Category",
    },
    ...(commonColumns as ColumnDef<Event>[])
]

export const workflowColumns: ColumnDef<Workflow>[] = [
    {
        accessorKey: "workflowId",
        header: "Workflow ID",
    },
    ...(commonColumns as ColumnDef<Workflow>[])
]
