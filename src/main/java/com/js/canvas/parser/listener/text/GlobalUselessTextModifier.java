package com.js.canvas.parser.listener.text;

import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.js.canvas.parser.data.OCRTextRenderInfo;
import com.js.canvas.parser.listener.ChainableEventListener;

import java.util.ArrayList;
import java.util.List;

public class GlobalUselessTextModifier extends ChainableEventListener {

    private List<OCRTextRenderInfo> textRenderInfoList = new ArrayList<>();

    @Override
    public void eventOccurred(IEventData iEventData, EventType eventType) {
        if (iEventData instanceof OCRTextRenderInfo) {
            textRenderInfoList.add((OCRTextRenderInfo) iEventData);
        } else {
            getNext().eventOccurred(iEventData, eventType);
        }
    }

    @Override
    public void flush() {
        for (OCRTextRenderInfo i : textRenderInfoList) {
            // only check single characters
            if (i.getText().length() > 1) {
                getNext().eventOccurred(i, EventType.RENDER_TEXT);
                continue;
            }
            // check location
            if (!isIsolated(i)) {
                getNext().eventOccurred(i, EventType.RENDER_TEXT);
            }
        }
        super.flush();
    }

    private boolean isIsolated(OCRTextRenderInfo i) {
        double distance = Double.MAX_VALUE;

        double x0 = i.getOCRChunk().getLocation().x + i.getOCRChunk().getLocation().width / 2;
        double y0 = i.getOCRChunk().getLocation().y + i.getOCRChunk().getLocation().height / 2;

        for (OCRTextRenderInfo i2 : textRenderInfoList) {
            if (i2.equals(i))
                continue;

            double x1 = i2.getOCRChunk().getLocation().x + i2.getOCRChunk().getLocation().width / 2;
            double y1 = i2.getOCRChunk().getLocation().y + i2.getOCRChunk().getLocation().height / 2;

            double d = java.lang.Math.sqrt(java.lang.Math.pow(x0 - x1, 2) + java.lang.Math.pow(y0 - y1, 2));
            distance = java.lang.Math.min(d, distance);
        }

        return distance > 10;
    }
}
