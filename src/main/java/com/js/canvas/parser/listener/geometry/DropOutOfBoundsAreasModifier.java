package com.js.canvas.parser.listener.geometry;

import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.js.canvas.parser.data.OCRTextRenderInfo;
import com.js.canvas.parser.listener.ChainableEventListener;

import java.util.ArrayList;
import java.util.List;

public class DropOutOfBoundsAreasModifier extends ChainableEventListener {

    private List<IEventData> eventDataList = new ArrayList<>();

    @Override
    public void flush() {
        List<Integer> imageSizes = new ArrayList<>();
        for (IEventData eventData : eventDataList) {
            OCRTextRenderInfo ocrTextRenderInfo = (OCRTextRenderInfo) eventData;
            int w = ocrTextRenderInfo.getOCRChunk().getImage().getWidth();
            int h = ocrTextRenderInfo.getOCRChunk().getImage().getHeight();
            int s = w * h;
            imageSizes.add(s);
        }

        // calculate average
        double avg = 0;
        for (Integer s : imageSizes)
            avg += s;
        avg /= imageSizes.size();

        // calculate standard deviation
        double stdDev = 0;
        for (Integer s : imageSizes) {
            stdDev += java.lang.Math.pow(s - avg, 2);
        }
        stdDev = java.lang.Math.sqrt(stdDev / (imageSizes.size() - 1));

        double lb = java.lang.Math.max(0, avg - 2 * stdDev);
        double ub = avg + 3 * stdDev;
        for (IEventData eventData : eventDataList) {
            OCRTextRenderInfo ocrTextRenderInfo = (OCRTextRenderInfo) eventData;
            int w = ocrTextRenderInfo.getOCRChunk().getImage().getWidth();
            int h = ocrTextRenderInfo.getOCRChunk().getImage().getHeight();
            int s = w * h;
            if (s >= lb && s <= ub) {
                getNext().eventOccurred(eventData, EventType.RENDER_TEXT);
            }
        }

        // flush
        super.flush();
    }

    @Override
    public void eventOccurred(IEventData data, EventType type) {
        if (data instanceof OCRTextRenderInfo) {
            OCRTextRenderInfo ocrTextRenderInfo = (OCRTextRenderInfo) data;
            eventDataList.add(ocrTextRenderInfo);
        } else {
            getNext().eventOccurred(data, type);
        }
    }

}
