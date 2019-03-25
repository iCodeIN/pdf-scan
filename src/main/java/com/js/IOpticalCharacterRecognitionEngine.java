package com.js;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public interface IOpticalCharacterRecognitionEngine {

    class OCRChunk {
        private Rectangle location;
        private String text;
        public OCRChunk(Rectangle rectangle, String text){
            this.location = rectangle;
            this.text = text;
        }
        public String getText(){ return text; }
        public Rectangle getLocation(){return location;}
    }

    List<OCRChunk> doOCR(BufferedImage bufferedImage);
}
