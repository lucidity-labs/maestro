import {Metadata} from "next"
import TableWrapper from "@/components/table/table-wrapper";

export const metadata: Metadata = {
  title: "Dashboard",
  description: "Example dashboard app built using the components.",
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
            <TableWrapper/>
          </div>
        </div>
      </div>
    </>
  )
}
