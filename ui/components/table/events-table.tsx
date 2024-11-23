import React, { useMemo } from 'react';
import { ComposedChart, Bar, XAxis, YAxis, Tooltip, Rectangle, Scatter } from 'recharts';
import { DataTable } from "@/components/table/data-table";
import { eventColumns } from "@/components/table/workflow-columns";
import { Button } from "@/components/ui/button";

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
                name: event.functionName || event.category, // Fallback to category if functionName is not available
                displayName: event.functionName || `${event.category} Event`, // More readable fallback
                category: event.category,
                start: startOffset,
                duration: duration,
                left: startOffset,
                value: duration || 1,
                isPoint: !event.endTimestamp
            };
        });
    }, [events]);

    const getEventColor = (category) => {
        const colors = {
            'WORKFLOW': '#2563eb',    // Blue
            'ACTIVITY': '#0ea5e9',    // Sky blue
            'SIGNAL': '#6366f1',      // Indigo
            'AWAIT': '#8b5cf6',       // Purple
            'SLEEP': '#a855f7',       // Violet
            'DEFAULT': '#3b82f6'      // Default blue
        };
        return colors[category] || colors.DEFAULT;
    };

    const CustomBar = (props) => {
        const { x, y, width, height, payload } = props;
        const color = getEventColor(payload.category);
        const actualX = x + (payload.left * (width / payload.value));

        if (payload.isPoint) {
            return (
                <g transform={`translate(${actualX},0)`}>
                    <circle
                        cx={0}
                        cy={y + height/2}
                        r={6}
                        fill={color}
                        filter="url(#glow)"
                    />
                    <circle
                        cx={0}
                        cy={y + height/2}
                        r={4}
                        fill="white"
                        fillOpacity={0.6}
                    />
                </g>
            );
        }

        return (
            <g transform={`translate(${actualX},0)`}>
                <defs>
                    <linearGradient id={`grad-${payload.index}`} x1="0" y1="0" x2="0" y2="1">
                        <stop offset="0%" stopColor={color} stopOpacity={0.9} />
                        <stop offset="100%" stopColor={color} stopOpacity={0.7} />
                    </linearGradient>
                    <filter id="glow">
                        <feGaussianBlur stdDeviation="1" result="glow" />
                        <feMerge>
                            <feMergeNode in="glow" />
                            <feMergeNode in="glow" />
                            <feMergeNode in="SourceGraphic" />
                        </feMerge>
                    </filter>
                </defs>
                <rect
                    x={0}
                    y={y}
                    width={Math.max(width, 2)}
                    height={height}
                    fill={`url(#grad-${payload.index})`}
                    rx={2}
                    ry={2}
                    filter="url(#glow)"
                />
            </g>
        );
    };

    const maxEndTime = Math.max(...chartData.map(d => d.start + d.value));

    return (
        <div className="rounded-lg bg-slate-950 p-6">
            <div className="overflow-x-auto">
                <ComposedChart
                    width={1000}
                    height={400}
                    data={chartData}
                    layout="vertical"
                    margin={{ top: 20, right: 30, left: 200, bottom: 20 }}
                >
                    <XAxis
                        type="number"
                        domain={[0, maxEndTime]}
                        stroke="#94a3b8"
                        tick={{ fill: '#94a3b8' }}
                        tickLine={{ stroke: '#94a3b8' }}
                    />
                    <YAxis
                        type="category"
                        dataKey="name"
                        stroke="#94a3b8"
                        tick={{ fill: '#94a3b8' }}
                        width={180}
                    />
                    <Tooltip
                        content={({ active, payload }) => {
                            if (active && payload?.[0]) {
                                const data = payload[0].payload;
                                return (
                                    <div className="bg-slate-900 border border-slate-700 p-3 rounded-lg shadow-lg">
                                        <p className="text-slate-200 font-medium mb-1">{data.displayName}</p>
                                        <p className="text-slate-300 text-sm">Category: {data.category}</p>
                                        <p className="text-slate-300 text-sm">Start: {data.start.toFixed(2)}s</p>
                                        {!data.isPoint && (
                                            <p className="text-slate-300 text-sm">Duration: {data.duration.toFixed(2)}s</p>
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
                        isAnimationActive={false}
                    />
                </ComposedChart>
            </div>
        </div>
    );
};

export const EventsTable = ({ workflowEvents, selectedWorkflow, onBack }) => {
    return (
        <div className="container mx-auto px-4">
            <div className="max-w-[1200px] mx-auto space-y-8">
                <div className="relative">
                    <h2 className="text-2xl text-center">Events</h2>
                    <p className="text-sm text-muted-foreground absolute right-0 top-full mt-2">
                        Workflow ID: {selectedWorkflow.workflowId}
                    </p>
                </div>

                <div className="overflow-x-auto">
                    <WaterfallChart events={workflowEvents} />
                </div>

                <div className="overflow-x-auto">
                    <DataTable
                        columns={eventColumns}
                        data={workflowEvents}
                    />
                </div>

                <div className="text-center">
                    <Button onClick={onBack} variant="outline">
                        Back to All Workflows
                    </Button>
                </div>
            </div>
        </div>
    );
};

export default EventsTable;