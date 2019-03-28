package com.js.canvas.parser.ocr;

import java.awt.*;
import java.awt.image.BufferedImage;

public class OCRChunk {

    private Rectangle location;
    private String text;

    private BufferedImage image;

    private int fontSize;
    private boolean italic;
    private boolean bold;
    private boolean underlined;
    private boolean monospaced;

    private Color textColor = Color.BLACK;
    private Color backgroundColor = Color.WHITE;

    private double confidence;

    public OCRChunk(Rectangle rectangle, String text) {
        this.location = rectangle;
        this.text = text;
    }

    public BufferedImage getImage() {
        return image;
    }

    public OCRChunk setImage(BufferedImage bufferedImage) {
        this.image = bufferedImage;
        return this;
    }

    public String getText() {
        return text;
    }

    public OCRChunk setText(String text) {
        this.text = text;
        return this;
    }

    public Rectangle getLocation() {
        return location;
    }

    public OCRChunk setLocation(Rectangle location) {
        this.location = location;
        return this;
    }

    public int getFontSize() {
        return fontSize;
    }

    public OCRChunk setFontSize(int fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public boolean isItalic() {
        return italic;
    }

    public OCRChunk setItalic(boolean isItalic) {
        this.italic = isItalic;
        return this;
    }

    public boolean isBold() {
        return bold;
    }

    public OCRChunk setBold(boolean isBold) {
        this.bold = isBold;
        return this;
    }

    public boolean isUnderlined() {
        return underlined;
    }

    public OCRChunk setUnderlined(boolean isUnderlined) {
        this.underlined = isUnderlined;
        return this;
    }

    public boolean isMonospaced() {
        return monospaced;
    }

    public OCRChunk setMonospaced(boolean isMonospaced) {
        this.monospaced = isMonospaced;
        return this;
    }

    public double getConfidence() {
        return confidence;
    }

    public OCRChunk setConfidence(double confidence) {
        this.confidence = confidence;
        return this;
    }

    public Color getTextColor() {
        return textColor;
    }

    public OCRChunk setTextColor(Color textColor) {
        this.textColor = textColor;
        return this;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public OCRChunk setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }
}