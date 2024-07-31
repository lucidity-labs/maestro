"use client"

import React, {useEffect, useState} from "react";
import {activityColumns, Workflow, workflowColumns} from "@/components/table/workflow-columns";
import {Button} from "@/components/button";
import {DataTable} from "@/components/table/data-table";

export default function TableWrapper() {

    const [selectedRow, setSelectedRow] = useState<any | null>(null)
    const [workflows, setWorkflows] = useState<Workflow[]>([])
    const handleRowClick = (row: any) => setSelectedRow(row)
    const handleBack = () => setSelectedRow(null)

    useEffect(() => {
        setWorkflows([
            {
                workflowId: "b5647c7b-5e16-40dd-8726-48bd0ad40cac",
                status: "STARTED",
                data: "{\"someString\":\"someInput\"}",
                className: "OrderWorkflowImpl",
                functionName: "submitOrder",
                timestamp: "2024-07-30 01:03:13.506655",
            },
        ])
    }, [])

    if (selectedRow) {
        return (
            <div className="flex flex-col items-center">
                <div className="w-full mb-8">
                    <DataTable columns={activityColumns} data={[selectedRow as any]}/>
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