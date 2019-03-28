import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.listener.SimpleTextExtractionStrategy;
import com.js.canvas.parser.listener.BaseLineModifier;
import com.js.canvas.parser.listener.FlushableEventListener;
import com.js.canvas.parser.listener.OCREventModifier;
import com.js.tesseract.TesseractOpticalCharacterRecognitionEngine;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

public class ExtractTextTest {

    @Test
    public void extractText() throws IOException {
        // initialize tesseract
        TesseractOpticalCharacterRecognitionEngine ocrEngine = new TesseractOpticalCharacterRecognitionEngine(new File("C:\\Users\\joris\\Downloads\\tessdata"), "eng");

        // create document
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(getClass().getClassLoader().getResourceAsStream("input_document.pdf")));

        SimpleTextExtractionStrategy strategyA = new SimpleTextExtractionStrategy();
        IEventListener strategyB = new BaseLineModifier(strategyA);
        FlushableEventListener strategyC = new OCREventModifier(strategyB, ocrEngine);

        new PdfCanvasProcessor(strategyC).processPageContent(pdfDocument.getPage(1));
        strategyC.flush();

        String text = strategyA.getResultantText();

        Assert.assertTrue(text.contains("2. Without granting any right or license, the Disclosing Party agrees that the foregoing shall not apply with respect to\n" +
                "any information after five years following the disclosure thereof or any information that the Receiving Party can\n" +
                "document (i) is or becomes (through no improper action or inaction by the Receiving Party or any affiliate, agent,\n" +
                "consultant or employee) generally available to the public, or (ii) was in its possession or known by it prior to receipt"));
    }
}
