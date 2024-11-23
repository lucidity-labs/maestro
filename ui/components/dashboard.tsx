"use client"

import React, {useEffect, useState} from "react";
import {Event, Workflow} from "@/components/workflow-columns";
import {API_BASE} from "@/lib/constants";
import {EventsTable} from "@/components/events-table";
import {WorkflowsTable} from "@/components/workflows-table";

export default function Dashboard() {
    const [selectedWorkflow, setSelectedWorkflow] = useState<Workflow>()
    const [workflowEvents, setWorkflowEvents] = useState<Event[]>([])
    const [workflows, setWorkflows] = useState<Workflow[]>([])

    const fetchWorkflowEvents = async (workflowId: string) => {
        const res = await fetch(`${API_BASE}/api/workflows/${workflowId}`);
        const json = await res.json();
        setWorkflowEvents(json);
    };

    useEffect(() => {
        // Handle browser back/forward
        const handlePopState = (event: PopStateEvent) => {
            const workflowState = event.state?.workflow;
            if (workflowState) {
                setSelectedWorkflow(workflowState);
                fetchWorkflowEvents(workflowState.workflowId);
            } else {
                setSelectedWorkflow(undefined);
                setWorkflowEvents([]);
            }
        };

        window.addEventListener('popstate', handlePopState);

        // Fetch initial workflows
        fetch(`${API_BASE}/api/workflows`)
            .then(res => res.json())
            .then(async data => {
                setWorkflows(data);

                // Check for workflow ID in URL
                const params = new URLSearchParams(window.location.search);
                const workflowId = params.get('workflow');

                if (workflowId) {
                    // Find the workflow in our data
                    const workflow = data.find((w: Workflow) => w.workflowId === workflowId);
                    if (workflow) {
                        setSelectedWorkflow(workflow);
                        await fetchWorkflowEvents(workflowId);
                        // Set initial history state
                        window.history.replaceState({ workflow }, '', `?workflow=${workflowId}`);
                    }
                }
            });

        return () => {
            window.removeEventListener('popstate', handlePopState);
        };
    }, []);

    const handleCellClick = async (cell: any) => {
        if (cell.column.id === "input" || cell.column.id === "output") return;

        const workflow = cell.row.original;

        window.history.pushState(
            { workflow },
            '',
            `?workflow=${workflow.workflowId}`
        );

        await fetchWorkflowEvents(workflow.workflowId);
        setSelectedWorkflow(workflow);
    }

    const handleBack = () => {
        window.history.pushState(
            { workflow: null },
            '',
            '/'
        );

        setSelectedWorkflow(undefined);
        setWorkflowEvents([]);
    }

    return selectedWorkflow ?
        <EventsTable
            workflowEvents={workflowEvents}
            selectedWorkflow={selectedWorkflow}
            onBack={handleBack}
        />
        :
        <WorkflowsTable
            workflows={workflows}
            onCellClick={handleCellClick}
        />
}