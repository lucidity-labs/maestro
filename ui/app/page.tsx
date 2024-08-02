import {Metadata} from "next"
import TableWrapper from "@/components/table/table-wrapper";

export const metadata: Metadata = {
  title: "Dashboard",
  description: "Example dashboard app built using the components.",
}

export default async function DashboardPage() {
    return (
        <div className="min-h-screen bg-background">
            <div className="flex-1 space-y-4 p-8 pt-6">
                <div className="container mx-auto">
                    <h2 className="text-3xl font-bold tracking-tight mb-6">Maestro UI</h2>
                    <TableWrapper/>
                </div>
            </div>
        </div>
    )
}
