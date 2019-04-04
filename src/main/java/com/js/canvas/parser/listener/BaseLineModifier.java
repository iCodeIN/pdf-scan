package com.js.canvas.parser.listener;

import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;
import com.js.canvas.parser.data.OCRTextRenderInfo;
import com.js.canvas.parser.ocr.OCRChunk;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class BaseLineModifier implements FlushableEventListener {

    private IEventListener innerListener;
    private List<OCRTextRenderInfo> ocrTextRenderInfoList = new ArrayList<>();

    public BaseLineModifier(IEventListener innerListener) {
        this.innerListener = innerListener;
    }

    @Override
    public void eventOccurred(IEventData data, EventType type) {
        if (data instanceof OCRTextRenderInfo) {
            ocrTextRenderInfoList.add((OCRTextRenderInfo) data);
        } else {
            innerListener.eventOccurred(data, type);
        }
    }

    @Override
    public void flush() {
        // baseline correction
        correctBaseLines();
        for (OCRTextRenderInfo info : ocrTextRenderInfoList)
            innerListener.eventOccurred(info, EventType.RENDER_TEXT);
        // cascade
        if (innerListener instanceof FlushableEventListener) {
            ((FlushableEventListener) innerListener).flush();
        }
    }

    private void correctBaseLines() {

        Map<Integer, List<OCRTextRenderInfo>> baselinesCandidates = new HashMap<>();
        for (OCRTextRenderInfo c : ocrTextRenderInfoList) {
            int b = c.getOCRChunk().getLocation().y;
            b = b - b % 10;
            if (!baselinesCandidates.containsKey(b))
                baselinesCandidates.put(b, new ArrayList<OCRTextRenderInfo>());
            baselinesCandidates.get(b).add(c);
        }

        // determine most likely baseline
        for (List<OCRTextRenderInfo> l : baselinesCandidates.values()) {

            // find the average baseline
            OCRTextRenderInfo chunkWithoutDescender = null;
            float avgBaseline = 0f;
            for (OCRTextRenderInfo c : l) {
                if (isRepresentativeBaseLine(c.getOCRChunk().getText())) {
                    chunkWithoutDescender = c;
                    break;
                }
                avgBaseline += c.getOCRChunk().getLocation().y;
            }
            avgBaseline /= l.size();

            float newBaseline = chunkWithoutDescender == null ? avgBaseline : chunkWithoutDescender.getOCRChunk().getLocation().y;
            for (OCRTextRenderInfo info : l) {
                OCRChunk c = info.getOCRChunk();
                c.setLocation(new Rectangle(c.getLocation().x, (int) newBaseline, c.getLocation().width, c.getLocation().height));
            }

        }
    }

    private boolean isRepresentativeBaseLine(String text) {
        if(!text.matches("[a-zA-Z0-9]+"))
            return false;
        return !text.matches(".*[gjpqy]+.*");
    }

    @Override
    public Set<EventType> getSupportedEvents() {
        return null;
    }

    private float getBaseLine(OCRTextRenderInfo ocrTextRenderInfo){
        BufferedImage img = ocrTextRenderInfo.getOCRChunk().getImage();
        int h = img.getHeight();
        int w = img.getWidth();
        for (int i = 0; i < h; i++) {
            double blackPercentage = 0;
            for (int j = 0; j < w; j++) {
                Color c = new Color(img.getRGB(j, h - 1 - i));
                blackPercentage += isCloserToDark(c) ? 1 : 0;
            }
            blackPercentage /= w;
            if(blackPercentage > 0.2)
                return w;
        }
        return (float) ocrTextRenderInfo.getOCRChunk().getLocation().getY();
    }

    public boolean isCloserToDark(Color c){
        double dW = distance(c, Color.WHITE);
        double dB = distance(c, Color.BLACK);
        return dB < dW;
    }

    private double distance(Color c0, Color c1) {
        double r = c0.getRed() - c1.getRed();
        double g = c0.getGreen() - c1.getGreen();
        double b = c0.getBlue() - c1.getBlue();
        return java.lang.Math.sqrt(r * r + g * g + b * b) / java.lang.Math.sqrt(196608);
    }
}
