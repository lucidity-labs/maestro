'use client';

import dynamic from 'next/dynamic';
import React from 'react';

const ReactJson = dynamic(() => import('@microlink/react-json-view'), { ssr: false });

interface DynamicJsonViewerProps {
    src: any;
}

const DynamicJsonViewer: React.FC<DynamicJsonViewerProps> = ({ src }) => (
    <ReactJson
        src={src}
        name={null}
        theme="isotope"
        collapsed={3}
        collapseStringsAfterLength={10}
        enableClipboard={false}
        displayDataTypes={false}
        displayObjectSize={false}
    />
);

export default DynamicJsonViewer;