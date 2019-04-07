package com.js.canvas.parser.ocr;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * This interface represents a generic implementation of an OCR tool.
 * As long as the OCR tool can provide this interface, the remainder of the code ought to work.
 */
public interface IOpticalCharacterRecognitionEngine {

    /**
     * An implementation of {@link IOpticalCharacterRecognitionEngine} should be able to provide
     * a {@link List<OCRChunk>} representing the text chunks found in a given {@link BufferedImage}
     * @param bufferedImage the input image
     * @return the output text chunks
     */
    List<OCRChunk> doOCR(BufferedImage bufferedImage);
}
