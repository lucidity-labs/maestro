import React, {useMemo} from 'react';
import {ComposedChart, Bar, XAxis, YAxis, ResponsiveContainer} from 'recharts';
import {DataTable} from "@/components/table/data-table";
import {eventColumns} from "@/components/table/workflow-columns";
import {Button} from "@/components/ui/button";
import {Tooltip, TooltipContent, TooltipProvider, TooltipTrigger} from "@/components/ui/tooltip";

const WaterfallChart = ({events}) => {
    const chartData = useMemo(() => {
        if (!events?.length) return [];

        const filteredEvents = events.filter(event =>
            event.category.toUpperCase() !== 'WORKFLOW'
        );

        const sortedEvents = [...filteredEvents].sort((a, b) =>
            new Date(a.startTimestamp).getTime() - new Date(b.startTimestamp).getTime()
        );

        if (sortedEvents.length === 0) return [];

        const firstTimestamp = new Date(sortedEvents[0].startTimestamp).getTime();

        return sortedEvents.map((event, index) => {
            const start = new Date(event.startTimestamp).getTime();
            const startOffset = (start - firstTimestamp) / 1000;
            const duration = event.endTimestamp
                ? (new Date(event.endTimestamp).getTime() - start) / 1000
                : 0;

            return {
                index,
                name: event.functionName || event.category,
                displayName: event.functionName || `${event.category} Event`,
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
            'ACTIVITY': '#0ea5e9',    // Sky blue
            'SIGNAL': '#6366f1',      // Indigo
            'AWAIT': '#8b5cf6',       // Purple
            'SLEEP': '#a855f7',       // Violet
            'DEFAULT': '#3b82f6'      // Default blue
        };
        return colors[category] || colors.DEFAULT;
    };

    const CustomYAxisTick = ({x, y, payload}) => {
        const maxLength = 15;
        const text = payload.value;
        const displayText = text.length > maxLength ? `${text.substring(0, maxLength)}...` : text;

        return (
            <TooltipProvider delayDuration={0}>
                <Tooltip>
                    <TooltipTrigger asChild>
                        <g transform={`translate(${x},${y})`}>
                            <text
                                x={-6}
                                y={0}
                                dy={4}
                                textAnchor="end"
                                fill="#94a3b8"
                                fontSize="12px"
                                style={{cursor: 'default'}}
                            >
                                {displayText}
                            </text>
                        </g>
                    </TooltipTrigger>
                    {text.length > maxLength && (
                        <TooltipContent side="left">
                            <p>{text}</p>
                        </TooltipContent>
                    )}
                </Tooltip>
            </TooltipProvider>
        );
    };

    const CustomBar = (props) => {
        const {x, y, width, height, payload} = props;
        const color = getEventColor(payload.category);
        const actualX = x + (payload.left * (width / payload.value));

        const tooltipContent = (
            <TooltipContent>
                <div className="space-y-1">
                    <p className="font-medium">{payload.displayName}</p>
                    <p className="text-sm">Category: {payload.category}</p>
                    <p className="text-sm">Start: {payload.start.toFixed(2)}s</p>
                    {!payload.isPoint && (
                        <p className="text-sm">Duration: {payload.duration.toFixed(2)}s</p>
                    )}
                </div>
            </TooltipContent>
        );

        if (payload.isPoint) {
            return (
                <TooltipProvider delayDuration={0}>
                    <Tooltip>
                        <TooltipTrigger asChild>
                            <g transform={`translate(${actualX},0)`}>
                                <circle
                                    cx={0}
                                    cy={y + height / 2}
                                    r={6}
                                    fill={color}
                                    filter="url(#glow)"
                                    style={{cursor: 'pointer'}}
                                />
                                <circle
                                    cx={0}
                                    cy={y + height / 2}
                                    r={4}
                                    fill="white"
                                    fillOpacity={0.6}
                                    style={{cursor: 'pointer'}}
                                />
                            </g>
                        </TooltipTrigger>
                        {tooltipContent}
                    </Tooltip>
                </TooltipProvider>
            );
        }

        return (
            <TooltipProvider delayDuration={0}>
                <Tooltip>
                    <TooltipTrigger asChild>
                        <g transform={`translate(${actualX},0)`}>
                            <defs>
                                <linearGradient id={`grad-${payload.index}`} x1="0" y1="0" x2="0" y2="1">
                                    <stop offset="0%" stopColor={color} stopOpacity={0.9}/>
                                    <stop offset="100%" stopColor={color} stopOpacity={0.7}/>
                                </linearGradient>
                                <filter id="glow">
                                    <feGaussianBlur stdDeviation="1" result="glow"/>
                                    <feMerge>
                                        <feMergeNode in="glow"/>
                                        <feMergeNode in="glow"/>
                                        <feMergeNode in="SourceGraphic"/>
                                    </feMerge>
                                </filter>
                            </defs>
                            <rect
                                x={0}
                                y={y}
                                width={Math.max(width, 2)}
                                height={height}
                                rx={2}
                                ry={2}
                                fill={`url(#grad-${payload.index})`}
                                filter="url(#glow)"
                                style={{cursor: 'pointer'}}
                            />
                        </g>
                    </TooltipTrigger>
                    {tooltipContent}
                </Tooltip>
            </TooltipProvider>
        );
    };

    const maxEndTime = Math.max(...chartData.map(d => d.start + d.value));

    return (
        <div className="rounded-lg bg-slate-950">
            <div className="h-[400px] w-full">
                <ResponsiveContainer width="100%" height="100%">
                    <ComposedChart
                        data={chartData}
                        layout="vertical"
                        margin={{top: 20, right: 32, left: 32, bottom: 20}}
                    >
                        <XAxis
                            type="number"
                            domain={[0, maxEndTime]}
                            stroke="#94a3b8"
                            tick={{fill: '#94a3b8'}}
                            tickLine={{stroke: '#94a3b8'}}
                        />
                        <YAxis
                            type="category"
                            dataKey="name"
                            stroke="#94a3b8"
                            tick={CustomYAxisTick}
                            width={120}
                        />
                        <Bar
                            dataKey="value"
                            barSize={20}
                            shape={<CustomBar/>}
                            isAnimationActive={false}
                        />
                    </ComposedChart>
                </ResponsiveContainer>
            </div>
        </div>
    );
};

export const EventsTable = ({workflowEvents, selectedWorkflow, onBack}) => {
    return (
        <div className="space-y-8">
            <div className="relative">
                <h2 className="text-2xl text-center">Events</h2>
                <p className="text-sm text-muted-foreground absolute right-0 top-full mt-2">
                    Workflow ID: {selectedWorkflow.workflowId}
                </p>
            </div>

            <WaterfallChart events={workflowEvents}/>

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

export default EventsTable;