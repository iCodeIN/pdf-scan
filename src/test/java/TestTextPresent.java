import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.SimpleTextExtractionStrategy;
import com.js.OCRTextExtractionStrategy;
import org.testng.annotations.Test;
import com.js.tesseract.TesseractOpticalCharacterRecognitionEngine;

import java.io.File;
import java.io.IOException;

public class TestTextPresent {

    @Test
    public void testWhetherTextIsPresent() throws IOException {

        // initialize tesseract
        TesseractOpticalCharacterRecognitionEngine ocrEngine = new TesseractOpticalCharacterRecognitionEngine(new File("C:\\Users\\joris\\Downloads\\tessdata_fast"), "nld");

        // create document
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new File("C:\\Users\\joris\\Downloads\\nda_starring_jane.pdf")));

        // extract text
        SimpleTextExtractionStrategy simpleTextExtractionStrategy = new SimpleTextExtractionStrategy();
        OCRTextExtractionStrategy ocrTextExtractionStrategy = new OCRTextExtractionStrategy(simpleTextExtractionStrategy, ocrEngine);
        new PdfCanvasProcessor(ocrTextExtractionStrategy).processPageContent(pdfDocument.getPage(1));

        // display
        System.out.println(simpleTextExtractionStrategy.getResultantText());
    }
}
