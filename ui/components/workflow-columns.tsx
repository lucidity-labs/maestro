import { ColumnDef } from "@tanstack/react-table";
import ReactJson from '@microlink/react-json-view';
import DynamicJsonViewer from "@/components/DynamicJsonViewer";

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

const tryParseJSON = (jsonString: string) => {
    try {
        const json = JSON.parse(jsonString);
        if (json && typeof json === "object") {
            return <DynamicJsonViewer src={json} />
        }
    } catch (e) {
        console.error('Invalid JSON:', e);
    }
    return jsonString;
};

const formatTimestamp = (isoString: string) => {
    if (!isoString) return undefined

    let split = new Date(isoString).toISOString().split('.');
    return split[0] + split[1].slice(-1)
};

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
        cell: ({ row }: any) => tryParseJSON(row.original.input)
    },
    {
        accessorKey: "output",
        header: "Output",
        cell: ({ row }: any) => tryParseJSON(row.original.output)
    },
    {
        accessorKey: "startTimestamp",
        header: "Start",
        cell: ({ row }: any) => formatTimestamp(row.original.startTimestamp)
    },
    {
        accessorKey: "endTimestamp",
        header: "End",
        cell: ({ row }: any) => formatTimestamp(row.original.endTimestamp)
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