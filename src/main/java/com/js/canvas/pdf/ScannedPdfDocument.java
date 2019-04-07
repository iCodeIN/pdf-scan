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
import com.js.canvas.parser.listener.ChainableEventListener;
import com.js.canvas.parser.listener.ocr.OCREventModifier;
import com.js.canvas.parser.listener.font.*;
import com.js.canvas.parser.listener.geometry.DropNarrowStripsModifier;
import com.js.canvas.parser.listener.geometry.DropOutOfBoundsAreasModifier;
import com.js.canvas.parser.listener.text.Sort;
import com.js.canvas.parser.listener.text.spellcheck.SpellCheckModifier;
import com.js.canvas.parser.ocr.IOpticalCharacterRecognitionEngine;
import com.js.canvas.parser.ocr.OCRChunk;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class acts as a wrapper around {@link PdfDocument}
 * It allows users to seamlessly integrate OCR capabilities in their iText flow.
 */
public class ScannedPdfDocument extends PdfDocument {

    private static int MARGIN = 5;
    private static PdfFont FONT;

    private IOpticalCharacterRecognitionEngine opticalCharacterRecognitionEngine;

    public ScannedPdfDocument(PdfReader reader, PdfWriter writer, IOpticalCharacterRecognitionEngine opticalCharacterRecognitionEngine) {
        super(reader, writer);
        this.opticalCharacterRecognitionEngine = opticalCharacterRecognitionEngine;
        try {
            FONT = PdfFontFactory.createFont();
        } catch (IOException e) {
        }
    }

    /**
     * Perform OCR on all pages
     */
    public void doOCR() {
        for (int i = 1; i <= getNumberOfPages(); i++) {
            doOCR(i);
        }
    }

    /**
     * Perform OCR on a specific page
     * <p>
     * scanned  -->  tesseract  -->     color     -->    baseline
     * pdf                         information       information
     *
     * @param pageNr the page number on which to perform OCR
     */
    public void doOCR(final int pageNr) {
        IEventListener anonymousListener = new ChainableEventListener() {
            java.util.List<OCRTextRenderInfo> ocrTextRenderInfoList = new ArrayList<>();

            @Override
            public void eventOccurred(IEventData data, EventType type) {
                if (data instanceof OCRTextRenderInfo) {
                    ocrTextRenderInfoList.add((OCRTextRenderInfo) data);
                }
            }

            @Override
            public void flush() {
                for (OCRTextRenderInfo i : ocrTextRenderInfoList)
                    blankOut(i, pageNr);
                for (OCRTextRenderInfo i : ocrTextRenderInfoList)
                    writeString(i, pageNr);
            }
        };

        ChainableEventListener listener = new OCREventModifier(opticalCharacterRecognitionEngine);
        listener
                // cleaning up tesseract mess
                .setNext(new DropNarrowStripsModifier())
                .setNext(new DropOutOfBoundsAreasModifier())
                .setNext(new DropOutOfBoundsAreasModifier())

                // base line correction
                .setNext(new LocalBaselineModifier())
                .setNext(new GlobalBaselineModifier())

                // font size information
                .setNext(new LocalFontSizeModifier())
                .setNext(new GlobalFontSizeModifier())

                // font color information
                .setNext(new ColorModifier())

                // spelling
                .setNext(new SpellCheckModifier(getClass().getClassLoader().getResourceAsStream("dict_en.txt")))

                // sort (used for older PDF viewers)
                .setNext(new Sort())

                // final push
                .setNext(anonymousListener);

        // process canvas
        new PdfCanvasProcessor(listener).processPageContent(getPage(pageNr));

        // flush
        listener.flush();
    }

    private void blankOut(OCRTextRenderInfo data, int pageNr) {
        OCRChunk chunk = data.getOCRChunk();
        PdfCanvas canvas = new PdfCanvas(getPage(pageNr));

        // background
        canvas.setColor(new DeviceRgb(Color.WHITE), true);
        canvas.rectangle(chunk.getLocation().x - MARGIN,
                chunk.getLocation().y - MARGIN,
                chunk.getLocation().width + 2 * MARGIN,
                chunk.getLocation().height + 2 * MARGIN);
        canvas.fill();
    }

    private void writeString(OCRTextRenderInfo data, int pageNr) {
        OCRChunk chunk = data.getOCRChunk();
        PdfCanvas canvas = new PdfCanvas(getPage(pageNr));

        // text
        canvas.beginText();
        canvas.setColor(new DeviceRgb(chunk.getTextColor()), true);
        canvas.setFontAndSize(FONT, data.getOCRChunk().getFontSize());
        canvas.moveText(chunk.getLocation().x, chunk.getLocation().y);
        canvas.showText(chunk.getText());
        canvas.endText();
    }
}
