import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.SimpleTextExtractionStrategy;
import com.js.canvas.parser.listener.ChainableEventListener;
import com.js.canvas.parser.listener.font.*;
import com.js.canvas.parser.listener.geometry.DropNarrowStripsModifier;
import com.js.canvas.parser.listener.geometry.DropOutOfBoundsAreasModifier;
import com.js.canvas.parser.listener.ocr.OCREventModifier;
import com.js.canvas.parser.listener.text.Sort;
import com.js.canvas.parser.listener.text.spellcheck.SpellCheckModifier;
import com.js.tesseract.TesseractOpticalCharacterRecognitionEngine;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class ExtractTextTest {

    @Test
    public void extractText() throws IOException {
        // initialize tesseract
        TesseractOpticalCharacterRecognitionEngine ocrEngine = new TesseractOpticalCharacterRecognitionEngine(new File("/home/joris/Code/tessdata"), "eng");

        // create document
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(getClass().getClassLoader().getResourceAsStream("input_001.pdf")));

        SimpleTextExtractionStrategy listenerA = new SimpleTextExtractionStrategy();

        ChainableEventListener listener = new OCREventModifier(ocrEngine);
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
                .setNext(listenerA);


        new PdfCanvasProcessor(listener).processPageContent(pdfDocument.getPage(1));
        listener.flush();

        String text = listenerA.getResultantText();

        Assert.assertTrue(text.contains("2 Without granting any right or license the Disclosing Party agrees that the foregoing shall not apply with respect to\n" +
                "any information after five years following the disclosure thereof or any information that the Receiving Party can\n" +
                "document (i) is or becomes through no improper action or inaction by the Receiving Party or any affiliate agent\n" +
                "consultant or employee generally available to the public or (ii) was in its possession or known by it prior to receipt\n" +
                "from the Disclosing Party as evidenced in writing except to the extent that such information was unlawfully\n" +
                "appropriated"));
    }
}
