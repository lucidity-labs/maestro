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
        header: "Class Name",
    },
    {
        accessorKey: "functionName",
        header: "Function Name",
    },
    {
        accessorKey: "startTimestamp",
        header: "Start Timestamp",
    },
    {
        accessorKey: "endTimestamp",
        header: "End Timestamp",
    },
    {
        accessorKey: "input",
        header: "Input",
    },
    {
        accessorKey: "output",
        header: "Output",
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
