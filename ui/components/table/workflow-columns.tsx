import {ColumnDef} from "@tanstack/react-table";

type Event = {
    status: string
    data: string
    className: string
    functionName: string
    timestamp: string
}

export type Activity = Event

export type Workflow = Event & {
    workflowId: string
}

const eventColumns: ColumnDef<Event>[] = [
    {
        accessorKey: "status",
        header: "Status",
    },
    {
        accessorKey: "data",
        header: "Data",
    },
    {
        accessorKey: "className",
        header: "Class Name",
    },
    {
        accessorKey: "functionName",
        header: "Function Name",
    },
    {
        accessorKey: "timestamp",
        header: "Timestamp",
    },
]

export const workflowColumns: ColumnDef<Workflow>[] = [
    {
        accessorKey: "workflowId",
        header: "Workflow ID",
    },
    ...(eventColumns as ColumnDef<Workflow>[])
]

export const activityColumns: ColumnDef<Activity>[] = eventColumns