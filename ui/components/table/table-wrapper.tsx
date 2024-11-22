"use client"

import React, {useEffect, useState} from "react";
import {Event, eventColumns, Workflow, workflowColumns} from "@/components/table/workflow-columns";
import {Button} from "@/components/ui/button";
import {DataTable} from "@/components/table/data-table";
import {API_BASE} from "@/lib/constants";

export default function TableWrapper() {
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

    return selectedWorkflow ? (
        <div className="flex flex-col items-center space-y-9">
            <DataTable
                title="Events"
                subtitle={"Workflow ID: " + selectedWorkflow.workflowId}
                columns={eventColumns}
                data={workflowEvents}
            />
            <Button onClick={handleBack} variant="outline" className="mt-4">
                Back to All Workflows
            </Button>
        </div>
    ) : (
        <DataTable
            title="Workflows"
            columns={workflowColumns}
            data={workflows}
            onCellClick={handleCellClick}
        />
    )
}