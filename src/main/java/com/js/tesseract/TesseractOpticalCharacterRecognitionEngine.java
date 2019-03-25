package com.js.tesseract;

import com.js.IOpticalCharacterRecognitionEngine;
import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.Tesseract;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TesseractOpticalCharacterRecognitionEngine implements IOpticalCharacterRecognitionEngine {

    private Tesseract tesseract;

    public TesseractOpticalCharacterRecognitionEngine(File tesseractDataDirectory, String languageCode){
        tesseract = new Tesseract();

        // set data path
        if(!tesseractDataDirectory.exists())
            throw new IllegalArgumentException();
        tesseract.setDatapath(tesseractDataDirectory.getAbsolutePath());

        // set language code
        if(!new File(tesseractDataDirectory, languageCode + ".traineddata").exists())
            throw new IllegalArgumentException();
        tesseract.setLanguage(languageCode);
    }

    public List<OCRChunk> doOCR(BufferedImage bufferedImage) {
        List<OCRChunk> textChunkLocationList = new ArrayList<>();
        try {
            for(Rectangle rectangle : tesseract.getSegmentedRegions(bufferedImage, ITessAPI.TessPageIteratorLevel.RIL_WORD)){
                String text = tesseract.doOCR(bufferedImage, rectangle);
                textChunkLocationList.add(new OCRChunk(rectangle, text));
            }
        } catch (Exception e) { }
        return textChunkLocationList;
    }
}
