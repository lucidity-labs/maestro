import {Metadata} from "next"
import {workflowColumns, Workflow} from "@/components/table/workflowColumns";
import {DataTable} from "@/components/table/data-table";

export const metadata: Metadata = {
  title: "Dashboard",
  description: "Example dashboard app built using the components.",
}

async function getData(): Promise<Workflow[]> {
  // Fetch data from your API here.
  return [
    {
      workflowId: "b5647c7b-5e16-40dd-8726-48bd0ad40cac",
      status: "STARTED",
      data: "{\"someString\":\"someInput\"}",
      className: "OrderWorkflowImpl",
      functionName: "submitOrder",
      timestamp: "2024-07-30 01:03:13.506655",
    },
  ]
}

export default async function DashboardPage() {
  return (
    <>
      <div className="hidden flex-col md:flex">
        <div className="flex-1 space-y-4 p-8 pt-6">
          <div className="flex items-center justify-between space-y-2">
            <h2 className="text-3xl font-bold tracking-tight">Maestro UI</h2>
          </div>
          <div className="container mx-auto py-10">
            <DataTable columns={workflowColumns} data={await getData()}/>
          </div>
        </div>
      </div>
    </>
  )
}
