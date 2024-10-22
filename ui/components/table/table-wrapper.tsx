"use client"

import React, {useEffect, useState} from "react";
import {Event, eventColumns, Workflow, workflowColumns} from "@/components/table/workflow-columns";
import {Button} from "@/components/button";
import {DataTable} from "@/components/table/data-table";

export default function TableWrapper() {
    const [selectedWorkflow, setSelectedWorkflow] = useState<Workflow>()
    const [workflowEvents, setWorkflowEvents] = useState<Event[]>([])
    const [workflows, setWorkflows] = useState<Workflow[]>([])

    const apiUrl = process.env.NEXT_PUBLIC_API_URL;

    const handleCellClick = async (cell: any) => {
        if (cell.column.id === "input" || cell.column.id === "output") return

        const workflow = cell.row.original

        const res = await fetch(`${apiUrl}/api/workflows/${workflow.workflowId}`)
        const json = await res.json()
        setSelectedWorkflow(workflow)
        setWorkflowEvents(json)
    }

    const handleBack = () => {
        setSelectedWorkflow(undefined)
        setWorkflowEvents([])
    }

    useEffect(() => {
        if (apiUrl) {
            fetch(`${apiUrl}/api/workflows`)
                .then(res => res.json())
                .then(data => setWorkflows(data))
        }
    }, [apiUrl])

    if (selectedWorkflow) {
        return (
            <div className="flex flex-col items-center space-y-9">
                <div className="w-full">
                    <DataTable title={"Events"} subtitle={"Workflow ID: " + selectedWorkflow.workflowId} columns={eventColumns} data={workflowEvents}/>
                </div>
                <Button onClick={handleBack} variant="outline" className="mt-4">
                    Back to All Workflows
                </Button>
            </div>
        )
    }

    return (
        <DataTable title={"Workflows"} columns={workflowColumns} data={workflows} onCellClick={handleCellClick}/>
    )
}