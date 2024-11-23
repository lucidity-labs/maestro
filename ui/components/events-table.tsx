import React from 'react';
import {DataTable} from "@/components/data-table";
import {eventColumns} from "@/components/workflow-columns";
import {Button} from "@/components/ui/button";
import WaterfallChart from "@/components/waterfall-chart";

export const EventsTable = ({workflowEvents, selectedWorkflow, onBack}) => {
    return (
        <div className="space-y-6">
            <div>
                <div className="text-center mb-1">
                    <h2 className="text-2xl">Events</h2>
                </div>
                <p className="text-sm text-muted-foreground text-right">
                    Workflow ID: {selectedWorkflow.workflowId}
                </p>
            </div>

            <WaterfallChart events={workflowEvents}/>

            <DataTable
                columns={eventColumns}
                data={workflowEvents}
            />

            <div className="text-center">
                <Button onClick={onBack} variant="outline">
                    Back to All Workflows
                </Button>
            </div>
        </div>
    );
};

export default EventsTable;