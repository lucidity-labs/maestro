"use client"

import React, {useEffect, useState} from "react";
import {eventColumns, Workflow, Event, workflowColumns} from "@/components/table/workflow-columns";
import {Button} from "@/components/button";
import {DataTable} from "@/components/table/data-table";

export default function TableWrapper() {

    const [selectedWorkflow, setSelectedWorkflow] = useState<Workflow>()
    const [workflowEvents, setWorkflowEvents] = useState<Event[]>([])
    const [workflows, setWorkflows] = useState<Workflow[]>([])
    const handleRowClick = async (row: any) => {
        const res = await fetch(`http://localhost:8000/api/workflows/${row.workflowId}`)
        const json = await res.json()
        setSelectedWorkflow(row)
        setWorkflowEvents(json)
    }
    const handleBack = () => {
        setSelectedWorkflow(undefined)
        setWorkflowEvents([])
    }

    useEffect(() => {
        fetch('http://localhost:8000/api/workflows')
            .then(res => res.json())
            .then(data => setWorkflows(data))
    }, [])

    if (selectedWorkflow) {
        return (
            <div className="flex flex-col items-center space-y-6">
                <div className="w-full">
                    <DataTable title={"Events"} subtitle={"Workflow ID: " + selectedWorkflow.workflowId} columns={eventColumns} data={workflowEvents}/>
                </div>
                <Button onClick={handleBack} variant="outline">
                    Back to All Workflows
                </Button>
            </div>
        )
    }

    return (
        <DataTable title={"Workflows"} columns={workflowColumns} data={workflows} onRowClick={handleRowClick}/>
    )
}