import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextExtractionStrategy;
import com.itextpdf.kernel.pdf.canvas.parser.listener.SimpleTextExtractionStrategy;
import com.js.canvas.parser.listener.OCREventModifier;
import com.js.canvas.parser.listener.BaseLineModifier;
import com.js.canvas.parser.ocr.ColorModifier;
import com.js.tesseract.TesseractOpticalCharacterRecognitionEngine;
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

        IEventListener strategyA = new SimpleTextExtractionStrategy();
        IEventListener strategyB = new OCREventModifier(strategyA, ocrEngine);

        new PdfCanvasProcessor(strategyB).processPageContent(pdfDocument.getPage(1));

        System.out.println(((SimpleTextExtractionStrategy) strategyA).getResultantText());

    }
}
