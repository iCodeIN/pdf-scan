package com.js.canvas.parser.ocr;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class ColorModifier implements IOpticalCharacterRecognitionEngine {

    private static final int COLOR_REDUCTION_FACTOR = 64;
    private IOpticalCharacterRecognitionEngine opticalCharacterRecognitionEngine;

    public ColorModifier(IOpticalCharacterRecognitionEngine innerEngine){
        this.opticalCharacterRecognitionEngine = innerEngine;
    }

    private double distance(Color c0, Color c1){
        double r = c0.getRed() - c1.getRed();
        double g = c0.getGreen() - c1.getGreen();
        double b = c0.getBlue() - c1.getBlue();
        return java.lang.Math.sqrt(r*r + g*g + b*b) / java.lang.Math.sqrt(196608);
    }

    @Override
    public List<OCRChunk> doOCR(BufferedImage bufferedImage){

        List<OCRChunk> chunkList = opticalCharacterRecognitionEngine.doOCR(bufferedImage);

        // match colors
        for(OCRChunk chunk : chunkList){

            int left = chunk.getLocation().x;
            int right = chunk.getLocation().x + chunk.getLocation().width;
            int top = chunk.getLocation().y;
            int bottom = chunk.getLocation().y + chunk.getLocation().height;

            Map<Color, Integer> colorHistogram = new HashMap<>();
            for (int i = left; i <= right; i++) {
                for (int j = top; j <= bottom ; j++) {
                    Color color = new Color(bufferedImage.getRGB(i, j));
                    color = new Color(color.getRed() - color.getRed() % (256 / COLOR_REDUCTION_FACTOR),
                            color.getGreen() - color.getGreen() % (256 / COLOR_REDUCTION_FACTOR),
                            color.getBlue() - color.getBlue() % (256 / COLOR_REDUCTION_FACTOR));
                    if(colorHistogram.containsKey(color))
                        colorHistogram.put(color, colorHistogram.get(color) + 1);
                    else
                        colorHistogram.put(color, 1);
                }
            }
            List<Map.Entry<Color, Integer>> entryList = new ArrayList<>(colorHistogram.entrySet());
            java.util.Collections.sort(entryList, new Comparator<Map.Entry<Color, Integer>>() {
                @Override
                public int compare(Map.Entry<Color, Integer> o1, Map.Entry<Color, Integer> o2) { return o1.getValue().compareTo(o2.getValue()); }
            });

            Color backgroundColor = entryList.get(entryList.size() -1).getKey();
            if(distance(backgroundColor, Color.WHITE) < 0.05)
                backgroundColor = Color.WHITE;

            Color textColor = entryList.get(entryList.size() - 2).getKey();
            if(distance(textColor, Color.BLACK) < 0.05)
                textColor = Color.BLACK;

            chunk.setBackgroundColor(backgroundColor);
            chunk.setTextColor(textColor);
        }

        // return
        return chunkList;
    }
}
