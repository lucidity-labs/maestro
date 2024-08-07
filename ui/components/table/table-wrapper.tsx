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
        const res = await fetch(`http://localhost:8000/api/workflows/${row.workflowId}`) // TODO: get host from env
        const json = await res.json()
        const formatted = formatModel(json)
        setSelectedWorkflow(row)
        setWorkflowEvents(formatted)
    }
    const handleBack = () => {
        setSelectedWorkflow(undefined)
        setWorkflowEvents([])
    }

    useEffect(() => {
        fetch('http://localhost:8000/api/workflows') // TODO: get host from env
            .then(res => res.json())
            .then(data => formatModel(data))
            .then(data => setWorkflows(data))
    }, [])

    const formatModel = (json: any[]) => json.map((object: any) => ({
        ...object,
        startTimestamp: formatTimestamp(object.startTimestamp),
        endTimestamp: formatTimestamp(object.endTimestamp),
    }));

    const formatTimestamp = (isoString: string) => {
        if (!isoString) return undefined

        let split = new Date(isoString).toISOString().split('.');
        return split[0] + split[1].slice(-1)
    };

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
        <DataTable title={"Workflows"} columns={workflowColumns} data={workflows} onRowClick={handleRowClick}/>
    )
}