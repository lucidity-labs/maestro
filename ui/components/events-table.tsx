import React from 'react';
import {DataTable} from "@/components/data-table";
import {eventColumns} from "@/components/workflow-columns";
import {Button} from "@/components/ui/button";
import WaterfallChart from "@/components/waterfall-chart";

export const EventsTable = ({workflowEvents, selectedWorkflow, onBack}) => {
    return (
        <div>
            <div className="mb-4">
                <div className="text-center">
                    <h2 className="text-2xl">Events</h2>
                </div>
            </div>

            <div className="mb-8">
                <p className="text-sm text-muted-foreground text-right mb-6">
                    Workflow ID: {selectedWorkflow.workflowId}
                </p>
                <WaterfallChart events={workflowEvents}/>
            </div>

            <div className="mb-8">
                <DataTable
                    columns={eventColumns}
                    data={workflowEvents}
                />
            </div>

            <div className="text-center">
                <Button onClick={onBack} variant="outline">
                    Back to All Workflows
                </Button>
            </div>
        </div>
    );
};

export default EventsTable;