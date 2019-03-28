package com.js;

import java.awt.*;

public class IOCRChunk {

        private Rectangle location;
        private String text;

        private int fontSize;
        private boolean italic;
        private boolean bold;
        private boolean underlined;
        private boolean monospaced;

        private Color textColor = Color.BLACK;
        private Color backgroundColor = Color.WHITE;

        private double confidence;

        public IOCRChunk(Rectangle rectangle, String text){
            this.location = rectangle;
            this.text = text;
        }

        public String getText(){ return text; }
        public IOCRChunk setText(String text){this.text=text; return this;}

        public Rectangle getLocation(){return location;}
        public IOCRChunk setLocation(Rectangle location){this.location=location; return this;}

        public int getFontSize(){return fontSize;}
        public IOCRChunk setFontSize(int fontSize){this.fontSize=fontSize; return this;}

        public boolean isItalic() { return italic; }
        public IOCRChunk setItalic(boolean isItalic){this.italic=isItalic; return this;}

        public boolean isBold() { return bold; }
        public IOCRChunk setBold(boolean isBold){this.bold=isBold; return this;}

        public boolean isUnderlined() { return underlined; }
        public IOCRChunk setUnderlined(boolean isUnderlined){this.underlined=isUnderlined; return this;}

        public boolean isMonospaced() { return monospaced; }
        public IOCRChunk setMonospaced(boolean isMonospaced){this.monospaced=isMonospaced; return this;}

        public double getConfidence(){return confidence;}
        public IOCRChunk setConfidence(double confidence){this.confidence=confidence; return this;}

        public Color getTextColor(){return textColor;}
        public IOCRChunk setTextColor(Color textColor){this.textColor = textColor; return this;}

        public Color getBackgroundColor(){return backgroundColor;}
        public IOCRChunk setBackgroundColor(Color backgroundColor){this.backgroundColor = backgroundColor; return this;}
    }