import React from "react";
import { Workflow, workflowColumns } from "@/components/table/workflow-columns";
import { DataTable } from "@/components/table/data-table";

interface WorkflowsTableProps {
    workflows: Workflow[];
    onCellClick: (cell: any) => void;
}

export function WorkflowsTable({ workflows, onCellClick }: WorkflowsTableProps) {
    return (
        <div>
            <div className="relative mb-8">
                <h2 className="text-2xl text-center mb-2">Workflows</h2>
            </div>
            <DataTable
                columns={workflowColumns}
                data={workflows}
                onCellClick={onCellClick}
            />
        </div>
    );
}