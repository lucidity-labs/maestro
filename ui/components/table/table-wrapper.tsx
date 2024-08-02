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
            <div className="flex flex-col items-center">
                <div className="w-full mb-8">
                    Workflow ID: {selectedWorkflow.workflowId}
                </div>
                <div className="w-full mb-8">
                    <DataTable columns={eventColumns} data={workflowEvents}/>
                </div>
                <Button onClick={handleBack} className="px-4 py-2">
                    Back
                </Button>
            </div>
        )
    }

    return (
        <DataTable columns={workflowColumns} data={workflows} onRowClick={handleRowClick}/>
    )
}