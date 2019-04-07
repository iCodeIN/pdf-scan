package com.js.canvas.parser.listener.font;

import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.js.canvas.parser.data.OCRTextRenderInfo;
import com.js.canvas.parser.listener.ChainableEventListener;
import com.js.canvas.parser.ocr.OCRChunk;

import java.awt.*;
import java.util.List;
import java.util.*;

public class ColorModifier extends ChainableEventListener {

    private static final int COLOR_REDUCTION_FACTOR = 64;

    private double distance(Color c0, Color c1) {
        double r = c0.getRed() - c1.getRed();
        double g = c0.getGreen() - c1.getGreen();
        double b = c0.getBlue() - c1.getBlue();
        return java.lang.Math.sqrt(r * r + g * g + b * b) / java.lang.Math.sqrt(196608);
    }

    @Override
    public void eventOccurred(IEventData data, EventType type) {
        if (data instanceof OCRTextRenderInfo) {
            processOCRTextRenderInfo((OCRTextRenderInfo) data);
        } else {
            getNext().eventOccurred(data, type);
        }
    }

    private void processOCRTextRenderInfo(OCRTextRenderInfo data) {
        OCRChunk chunk = data.getOCRChunk();
        Map<Color, Integer> colorHistogram = new HashMap<>();
        for (int i = 0; i < chunk.getImage().getWidth(); i++) {
            for (int j = 0; j < chunk.getImage().getHeight(); j++) {
                Color color = new Color(chunk.getImage().getRGB(i, j));
                color = new Color(color.getRed() - color.getRed() % (256 / COLOR_REDUCTION_FACTOR),
                        color.getGreen() - color.getGreen() % (256 / COLOR_REDUCTION_FACTOR),
                        color.getBlue() - color.getBlue() % (256 / COLOR_REDUCTION_FACTOR));
                if (colorHistogram.containsKey(color))
                    colorHistogram.put(color, colorHistogram.get(color) + 1);
                else
                    colorHistogram.put(color, 1);
            }
        }
        List<Map.Entry<Color, Integer>> entryList = new ArrayList<>(colorHistogram.entrySet());
        java.util.Collections.sort(entryList, new Comparator<Map.Entry<Color, Integer>>() {
            @Override
            public int compare(Map.Entry<Color, Integer> o1, Map.Entry<Color, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        if (entryList.size() >= 2) {
            Color backgroundColor = entryList.get(entryList.size() - 1).getKey();
            if (distance(backgroundColor, Color.WHITE) < 0.05)
                backgroundColor = Color.WHITE;

            Color textColor = entryList.get(entryList.size() - 2).getKey();
            if (distance(textColor, Color.BLACK) < 0.05)
                textColor = Color.BLACK;

            chunk.setBackgroundColor(backgroundColor);
            chunk.setTextColor(textColor);
        }

        // delegate
        getNext().eventOccurred(data, EventType.RENDER_TEXT);
    }

}


