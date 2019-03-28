import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.SimpleTextExtractionStrategy;
import com.js.canvas.parser.listener.OCREventModifier;
import com.js.canvas.pdf.ScannedPdfDocument;
import org.testng.annotations.Test;
import com.js.tesseract.TesseractOpticalCharacterRecognitionEngine;

import java.io.File;
import java.io.IOException;

public class TestTextPresent {

    @Test
    public void testWhetherTextIsPresent() throws IOException {

        // initialize tesseract
        TesseractOpticalCharacterRecognitionEngine ocrEngine = new TesseractOpticalCharacterRecognitionEngine(new File("C:\\Users\\joris\\Downloads\\tessdata"), "eng");

        // create document
        ScannedPdfDocument pdfDocument = new ScannedPdfDocument(
                new PdfReader(new File("C:\\Users\\joris\\Downloads\\nda_starring_jane.pdf")),
                new PdfWriter(new File("C:\\Users\\joris\\Downloads\\nda_starring_jane_out.pdf")),
                ocrEngine
        );

        // perform OCR
        pdfDocument.doOCR(1);

        // close
        pdfDocument.close();
    }
}
