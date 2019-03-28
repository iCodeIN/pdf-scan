package com.js.canvas.parser.ocr;

import java.awt.image.BufferedImage;
import java.util.List;

public interface IOpticalCharacterRecognitionEngine {

    List<OCRChunk> doOCR(BufferedImage bufferedImage);
}
