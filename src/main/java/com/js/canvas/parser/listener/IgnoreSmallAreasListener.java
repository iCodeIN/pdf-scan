package com.js.canvas.parser.listener;

import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;
import com.js.canvas.parser.data.OCRTextRenderInfo;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class IgnoreSmallAreasListener implements FlushableEventListener {

    private IEventListener innerListener;
    private List<IEventData> eventDataList = new ArrayList<>();

    public IgnoreSmallAreasListener(IEventListener innerListener){
        this.innerListener = innerListener;
    }

    @Override
    public void flush() {
        List<Integer> imageSizes = new ArrayList<>();
        for(IEventData eventData : eventDataList){
            OCRTextRenderInfo ocrTextRenderInfo = (OCRTextRenderInfo) eventData;
            int w = ocrTextRenderInfo.getOCRChunk().getImage().getWidth();
            int h = ocrTextRenderInfo.getOCRChunk().getImage().getHeight();
            int s = w  * h;
            imageSizes.add(s);
        }

        // calculate average
        double avg = 0;
        for(Integer s : imageSizes)
            avg += s;
        avg /= imageSizes.size();

        // calculate standard deviation
        double stdDev = 0;
        for(Integer s : imageSizes){
            stdDev += java.lang.Math.pow(s - avg, 2);
        }
        stdDev  = java.lang.Math.sqrt(stdDev / (imageSizes.size() -1));

        double lb = avg -  2 * stdDev;
        double ub = avg + 2 * stdDev;
        for(IEventData eventData : eventDataList){
            OCRTextRenderInfo ocrTextRenderInfo = (OCRTextRenderInfo) eventData;
            int w = ocrTextRenderInfo.getOCRChunk().getImage().getWidth();
            int h = ocrTextRenderInfo.getOCRChunk().getImage().getHeight();
            int s = w  * h;
            if(s >= lb)
                innerListener.eventOccurred(eventData, EventType.RENDER_TEXT);
        }

        // flush
        if(innerListener instanceof FlushableEventListener){
            ((FlushableEventListener) innerListener).flush();
        }
    }

    @Override
    public void eventOccurred(IEventData data, EventType type) {
        if(data instanceof OCRTextRenderInfo){
            OCRTextRenderInfo ocrTextRenderInfo = (OCRTextRenderInfo) data;
            eventDataList.add(ocrTextRenderInfo);
        }else{
            innerListener.eventOccurred(data, type);
        }
    }

    @Override
    public Set<EventType> getSupportedEvents() {
        return null;
    }
}
