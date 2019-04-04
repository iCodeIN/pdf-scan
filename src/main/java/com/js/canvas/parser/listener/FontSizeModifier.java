package com.js.canvas.parser.listener;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;
import com.js.canvas.parser.data.OCRTextRenderInfo;

import java.io.IOException;
import java.util.Set;

public class FontSizeModifier implements FlushableEventListener {

    private IEventListener innerListener;

    public FontSizeModifier(IEventListener innerListener){
        this.innerListener = innerListener;
    }

    @Override
    public void eventOccurred(IEventData data, EventType type) {
        if(data instanceof OCRTextRenderInfo){
            innerListener.eventOccurred(correctFontSize((OCRTextRenderInfo) data), type);
        }else{
            innerListener.eventOccurred(data, type);
        }
    }

    private OCRTextRenderInfo correctFontSize(OCRTextRenderInfo ocrTextRenderInfo){
        int h = (int) ocrTextRenderInfo.getOCRChunk().getLocation().getHeight();
        int w = (int) ocrTextRenderInfo.getOCRChunk().getLocation().getWidth();

        String s = ocrTextRenderInfo.getText();

        try {
            PdfFont helvetica = PdfFontFactory.createFont();

            int bestFontSize = (int) ocrTextRenderInfo.getOCRChunk().getFontSize() / 2;
            int bestHeightDiff = java.lang.Math.abs(h - helvetica.getAscent(s, bestFontSize));

            for(int i = (int) Math.max(1, ocrTextRenderInfo.getFontSize() - 10); i<=ocrTextRenderInfo.getFontSize()+10; i++) {
                int h2 = helvetica.getAscent(s, i);
                int w2 = (int) helvetica.getWidth(s, i);

                int heightDiff = java.lang.Math.abs(h - h2);

                if(heightDiff < bestHeightDiff && h2 <= h && w2 <= w){
                    bestHeightDiff = heightDiff;
                    bestFontSize = i;
                }
            }

            // set font size
            ocrTextRenderInfo.getOCRChunk().setFontSize(bestFontSize);

        } catch (IOException e) { }

        return ocrTextRenderInfo;
    }

    @Override
    public Set<EventType> getSupportedEvents() {
        return null;
    }

    @Override
    public void flush() {
        if(innerListener instanceof FlushableEventListener){ ((FlushableEventListener) innerListener).flush(); }
    }
}
