package com.js.canvas.parser.listener.geometry;

import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.js.canvas.parser.data.OCRTextRenderInfo;
import com.js.canvas.parser.listener.ChainableEventListener;

public class DropNarrowStripsModifier extends ChainableEventListener {

    @Override
    public void eventOccurred(IEventData iEventData, EventType eventType) {
        if (iEventData instanceof OCRTextRenderInfo) {
            int w = ((OCRTextRenderInfo) iEventData).getOCRChunk().getLocation().width;
            int h = ((OCRTextRenderInfo) iEventData).getOCRChunk().getLocation().height;
            if (w > 2 && h > 2) {
                getNext().eventOccurred(iEventData, eventType);
            }
        } else {
            getNext().eventOccurred(iEventData, eventType);
        }
    }
}
