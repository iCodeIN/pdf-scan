package com.js.canvas.parser.listener.text;

import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.js.canvas.parser.data.OCRTextRenderInfo;
import com.js.canvas.parser.listener.ChainableEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Sort extends ChainableEventListener {

    private List<OCRTextRenderInfo> ocrTextRenderInfoList = new ArrayList<>();

    @Override
    public void eventOccurred(IEventData iEventData, EventType eventType) {
        if(iEventData instanceof OCRTextRenderInfo){
            ocrTextRenderInfoList.add((OCRTextRenderInfo) iEventData);
        }else{
            getNext().eventOccurred(iEventData, eventType);
        }
    }

    @Override
    public void flush() {
        // sort
        java.util.Collections.sort(ocrTextRenderInfoList, new Comparator<OCRTextRenderInfo>() {
            @Override
            public int compare(OCRTextRenderInfo o0, OCRTextRenderInfo o1) {
                int y0 = o0.getOCRChunk().getLocation().y;
                int y1 = o1.getOCRChunk().getLocation().y;
                if(y0 == y1){
                    int x0 = o0.getOCRChunk().getLocation().x;
                    int x1 = o0.getOCRChunk().getLocation().x;
                    return (x0 - x1);
                }else{
                    return y0 > y1 ? -1 : 1;
                }
            }
        });

        // process by next
        for(OCRTextRenderInfo i : ocrTextRenderInfoList)
            getNext().eventOccurred(i, EventType.RENDER_TEXT);

        // flush
        super.flush();
    }
}
