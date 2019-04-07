package com.js.canvas.parser.listener.font;

import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.js.canvas.parser.data.OCRTextRenderInfo;
import com.js.canvas.parser.listener.ChainableEventListener;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GlobalFontSizeModifier extends ChainableEventListener {

    private List<OCRTextRenderInfo> ocrTextRenderInfoList = new ArrayList<>();

    @Override
    public void eventOccurred(IEventData data, EventType type) {
        if (data instanceof OCRTextRenderInfo) {
            ocrTextRenderInfoList.add((OCRTextRenderInfo) data);
        } else {
            getNext().eventOccurred(data, type);
        }
    }

    @Override
    public void flush() {
        // baseline correction
        correctFontSize();

        // send to next
        for (OCRTextRenderInfo info : ocrTextRenderInfoList)
            getNext().eventOccurred(info, EventType.RENDER_TEXT);

        /// flush
        super.flush();
    }

    private void correctFontSize() {

        // correct each one
        Set<OCRTextRenderInfo> done = new HashSet<>();
        for (OCRTextRenderInfo i0 : ocrTextRenderInfoList) {

            if (done.contains(i0))
                continue;

            int y0 = i0.getOCRChunk().getLocation().y;
            int h0 = i0.getOCRChunk().getLocation().height;
            Rectangle r0 = new Rectangle(0, y0, Integer.MAX_VALUE, h0);

            // find everything on the same line
            List<OCRTextRenderInfo> line = new ArrayList<>();
            for (OCRTextRenderInfo i1 : ocrTextRenderInfoList) {
                if (i1.getOCRChunk().getLocation().intersects(r0)) {
                    line.add(i1);
                }
            }

            // determine font size
            double averageFontSize = 0;
            for (OCRTextRenderInfo i1 : line) {
                averageFontSize += i1.getFontSize();
            }
            averageFontSize /= line.size();

            // set font size
            for (OCRTextRenderInfo i1 : line) {
                i1.getOCRChunk().setFontSize((int) averageFontSize);
                done.add(i1);
            }
        }

    }

}
