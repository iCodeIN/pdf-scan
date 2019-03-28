package com.js;

import java.awt.image.BufferedImage;
import java.util.List;

public interface IOpticalCharacterRecognitionEngine {

    List<IOCRChunk> doOCR(BufferedImage bufferedImage);
}
