import React from "react";
import { Event, eventColumns } from "@/components/table/workflow-columns";
import { Button } from "@/components/ui/button";
import { DataTable } from "@/components/table/data-table";

interface EventsTableProps {
    workflowEvents: Event[];
    selectedWorkflow: {
        workflowId: string;
    };
    onBack: () => void;
}

export function EventsTable({ workflowEvents, selectedWorkflow, onBack }: EventsTableProps) {
    return (
        <div>
            <div className="relative mb-12">
                <h2 className="text-2xl text-center mb-2">Events</h2>
                <p className="text-sm text-muted-foreground absolute right-0 top-full mt-2">
                    {"Workflow ID: " + selectedWorkflow.workflowId}
                </p>
            </div>
            <DataTable
                columns={eventColumns}
                data={workflowEvents}
            />
            <div className="mt-4 text-center">
                <Button onClick={onBack} variant="outline">
                    Back to All Workflows
                </Button>
            </div>
        </div>
    );
}