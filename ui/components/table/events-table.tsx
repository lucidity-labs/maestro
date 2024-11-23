import React, { useMemo } from 'react';
import { BarChart, Bar, XAxis, YAxis, Tooltip, Rectangle, Scatter } from 'recharts';
import {DataTable} from "@/components/table/data-table";
import {eventColumns} from "@/components/table/workflow-columns";
import {Button} from "@/components/ui/button";

const WaterfallChart = ({ events }) => {
    const chartData = useMemo(() => {
        if (!events?.length) return [];

        const sortedEvents = [...events].sort((a, b) =>
            new Date(a.startTimestamp).getTime() - new Date(b.startTimestamp).getTime()
        );

        const firstTimestamp = new Date(sortedEvents[0].startTimestamp).getTime();

        return sortedEvents.map((event, index) => {
            const start = new Date(event.startTimestamp).getTime();
            const startOffset = (start - firstTimestamp) / 1000;
            const duration = event.endTimestamp
                ? (new Date(event.endTimestamp).getTime() - start) / 1000
                : 0;

            return {
                index,
                name: event.functionName,
                category: event.category,
                start: startOffset,
                duration: duration,
                // This is crucial - recharts needs this to render properly
                value: duration || 1,
                isPoint: !event.endTimestamp
            };
        });
    }, [events]);

    const CustomBar = (props) => {
        const { x, y, width, height, payload } = props;

        if (payload.isPoint) {
            // Render a diamond for point events
            return (
                <path
                    d={`M ${x},${y + height/2} l ${height/2},-${height/2} l ${height/2},${height/2} l -${height/2},${height/2} l -${height/2},-${height/2}`}
                    fill="#FFD700"
                    stroke="#FFFFFF"
                    strokeWidth={1}
                />
            );
        }

        // Render a rectangle for duration events
        return (
            <g>
                <rect
                    x={x}
                    y={y}
                    width={Math.max(width, 2)} // Ensure minimum width
                    height={height}
                    fill="#4CAF50"
                    stroke="#FFFFFF"
                    strokeWidth={1}
                    fillOpacity={0.8}
                />
            </g>
        );
    };

    return (
        <div className="mt-4" style={{ background: '#000' }}>
            <BarChart
                width={800}
                height={400}
                data={chartData}
                layout="vertical"
                margin={{ top: 20, right: 30, left: 200, bottom: 20 }}
            >
                <XAxis
                    type="number"
                    stroke="#FFFFFF"
                    tick={{ fill: '#FFFFFF' }}
                />
                <YAxis
                    type="category"
                    dataKey="name"
                    stroke="#FFFFFF"
                    tick={{ fill: '#FFFFFF' }}
                    width={180}
                />
                <Tooltip
                    content={({ active, payload }) => {
                        if (active && payload?.[0]) {
                            const data = payload[0].payload;
                            return (
                                <div className="bg-black border border-white p-2 rounded shadow">
                                    <p className="text-white font-medium">{data.name}</p>
                                    <p className="text-white">Start: {data.start.toFixed(2)}s</p>
                                    {!data.isPoint && (
                                        <p className="text-white">Duration: {data.duration.toFixed(2)}s</p>
                                    )}
                                </div>
                            );
                        }
                        return null;
                    }}
                />
                <Bar
                    dataKey="value"
                    barSize={20}
                    shape={<CustomBar />}
                    isAnimationActive={false}  // Disable animations for debugging
                />
            </BarChart>

            {/* Debug info */}
            <div className="text-white text-sm mt-2">
                Data points: {chartData.length}
            </div>
        </div>
    );
};

export const EventsTable = ({ workflowEvents, selectedWorkflow, onBack }) => {
    return (
        <div className="space-y-8">
            <div className="relative">
                <h2 className="text-2xl text-center">Events</h2>
                <p className="text-sm text-muted-foreground absolute right-0 top-full mt-2">
                    Workflow ID: {selectedWorkflow.workflowId}
                </p>
            </div>

            <WaterfallChart events={workflowEvents} />

            <DataTable
                columns={eventColumns}
                data={workflowEvents}
            />

            <div className="text-center">
                <Button onClick={onBack} variant="outline">
                    Back to All Workflows
                </Button>
            </div>
        </div>
    );
};