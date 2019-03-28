package com.js.canvas.parser.ocr;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseLineModifier implements IOpticalCharacterRecognitionEngine {

    private IOpticalCharacterRecognitionEngine opticalCharacterRecognitionEngine;

    public BaseLineModifier(IOpticalCharacterRecognitionEngine opticalCharacterRecognitionEngine){
        this.opticalCharacterRecognitionEngine = opticalCharacterRecognitionEngine;
    }

    @Override
    public List<OCRChunk> doOCR(BufferedImage bufferedImage) {
        List<OCRChunk> ocrChunkList = opticalCharacterRecognitionEngine.doOCR(bufferedImage);

        Map<Integer, List<OCRChunk>> baselinesCandidates = new HashMap<>();
        for(OCRChunk c : ocrChunkList){
            int b = c.getLocation().y;
            b = b - b % 20;
            if(!baselinesCandidates.containsKey(b))
                baselinesCandidates.put(b, new ArrayList<OCRChunk>());
            baselinesCandidates.get(b).add(c);
        }

        PdfFont helvetica = null;
        try { helvetica = PdfFontFactory.createFont(); } catch (IOException e) { }

        // determine most likely baseline
        for(List<OCRChunk> l : baselinesCandidates.values()){
            for(OCRChunk c : l){
                c.getLocation().y += helvetica.getDescent(c.getText(), c.getFontSize());
            }
        }
        return ocrChunkList;
    }

}
