package com.js.canvas.pdf;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;
import com.js.canvas.parser.data.OCRTextRenderInfo;
import com.js.canvas.parser.listener.FlushableEventListener;
import com.js.canvas.parser.listener.OCREventModifier;
import com.js.canvas.parser.listener.BaseLineModifier;
import com.js.canvas.parser.ocr.ColorModifier;
import com.js.canvas.parser.ocr.IOpticalCharacterRecognitionEngine;
import com.js.canvas.parser.ocr.OCRChunk;

import java.io.IOException;
import java.util.Set;

/**
 * This class acts as a wrapper around {@link PdfDocument}
 * It allows users to seamlessly integrate OCR capabilities in their iText flow.
 */
public class ScannedPdfDocument extends PdfDocument {

    private static int MARGIN = 1;
    private static PdfFont FONT;

    private IOpticalCharacterRecognitionEngine opticalCharacterRecognitionEngine;

    public ScannedPdfDocument(PdfReader reader, PdfWriter writer, IOpticalCharacterRecognitionEngine opticalCharacterRecognitionEngine){
        super(reader, writer);
        this.opticalCharacterRecognitionEngine = opticalCharacterRecognitionEngine;
        try { FONT = PdfFontFactory.createFont(); } catch (IOException e) { }
    }

    /**
     * Perform OCR on all pages
     */
    public void doOCR(){
        for (int i = 1; i <= getNumberOfPages() ; i++) {
            doOCR(i);
        }
    }

    /**
     * Perform OCR on a specific page
     * @param pageNr the page number on which to perform OCR
     */
    public void doOCR(final int pageNr){
        IEventListener anonymousListener = new IEventListener() {
            @Override
            public void eventOccurred(IEventData data, EventType type) {
                if (data instanceof OCRTextRenderInfo) {
                    writeString((OCRTextRenderInfo) data, pageNr);
                }
            }
            @Override
            public Set<EventType> getSupportedEvents() { return null; }
        };
        FlushableEventListener baselineModifier = new BaseLineModifier(anonymousListener);
        IEventListener listener = new OCREventModifier(baselineModifier, new ColorModifier(opticalCharacterRecognitionEngine));

        // process canvas
        new PdfCanvasProcessor(listener).processPageContent(getPage(pageNr));

        // flush
        baselineModifier.flush();
    }

    private void writeString(OCRTextRenderInfo data, int pageNr) {
        OCRChunk chunk = data.getOCRChunk();

        PdfCanvas canvas = new PdfCanvas(getPage(pageNr));

        // background
        canvas.setColor(new DeviceRgb(chunk.getBackgroundColor()), true);
        canvas.rectangle(chunk.getLocation().x - MARGIN,
                chunk.getLocation().y - MARGIN,
                chunk.getLocation().width + 2 * MARGIN,
                chunk.getLocation().height + 2 * MARGIN);
        canvas.fill();

        // text
        canvas.beginText();
        canvas.setColor(new DeviceRgb(chunk.getTextColor()), true);
        canvas.setFontAndSize(FONT, chunk.getFontSize());
        canvas.moveText(chunk.getLocation().x, chunk.getLocation().y);
        canvas.showText(chunk.getText());
        canvas.endText();
    }
}
