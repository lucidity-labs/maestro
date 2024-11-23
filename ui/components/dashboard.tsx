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

    const handleCellClick = async (cell: any) => {
        if (cell.column.id === "input" || cell.column.id === "output") return

        const workflow = cell.row.original

        const res = await fetch(`${API_BASE}/api/workflows/${workflow.workflowId}`)
        const json = await res.json()
        setSelectedWorkflow(workflow)
        setWorkflowEvents(json)
    }

    const handleBack = () => {
        setSelectedWorkflow(undefined)
        setWorkflowEvents([])
    }

    useEffect(() => {
        fetch(`${API_BASE}/api/workflows`)
            .then(res => res.json())
            .then(data => setWorkflows(data))
    }, [])

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