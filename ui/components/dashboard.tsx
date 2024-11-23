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

    useEffect(() => {
        // Handle browser back/forward
        const handlePopState = (event: PopStateEvent) => {
            const workflowState = event.state?.workflow;
            if (workflowState) {
                setSelectedWorkflow(workflowState);
                // Refetch events for the workflow
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
            .then(data => setWorkflows(data));

        return () => {
            window.removeEventListener('popstate', handlePopState);
        };
    }, []);

    const fetchWorkflowEvents = async (workflowId: string) => {
        const res = await fetch(`${API_BASE}/api/workflows/${workflowId}`);
        const json = await res.json();
        setWorkflowEvents(json);
    };

    const handleCellClick = async (cell: any) => {
        if (cell.column.id === "input" || cell.column.id === "output") return;

        const workflow = cell.row.original;

        // Push the new state to browser history
        window.history.pushState(
            { workflow }, // State object
            '', // Title (unused)
            `?workflow=${workflow.workflowId}` // URL (optional)
        );

        await fetchWorkflowEvents(workflow.workflowId);
        setSelectedWorkflow(workflow);
    }

    const handleBack = () => {
        // Push the "no workflow" state to browser history
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