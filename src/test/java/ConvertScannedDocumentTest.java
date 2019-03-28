import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.SimpleTextExtractionStrategy;
import com.js.canvas.pdf.ScannedPdfDocument;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.js.tesseract.TesseractOpticalCharacterRecognitionEngine;

import java.io.File;
import java.io.IOException;

public class ConvertScannedDocumentTest {

    @Test
    public void convert() throws IOException {

        // initialize tesseract
        TesseractOpticalCharacterRecognitionEngine ocrEngine = new TesseractOpticalCharacterRecognitionEngine(new File("C:\\Users\\joris\\Downloads\\tessdata"), "eng");

        // create document
        ScannedPdfDocument pdfDocument = new ScannedPdfDocument(
                new PdfReader(getClass().getClassLoader().getResourceAsStream("input_document.pdf")),
                new PdfWriter(new File("output_document.pdf")),
                ocrEngine
        );

        // perform OCR
        pdfDocument.doOCR(1);

        // close
        pdfDocument.close();
    }

    @Test
    public void readConvertedDocument() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new File("output_document.pdf")));

        // set up simple text extraction strategy
        SimpleTextExtractionStrategy textExtractionStrategy = new SimpleTextExtractionStrategy();

        // process page
        new PdfCanvasProcessor(textExtractionStrategy).processPageContent(pdfDocument.getPage(1));

        // get text
        String text = textExtractionStrategy.getResultantText();

        // test
        Assert.assertTrue(text.contains("THIS AGREEMENT is made on 19-11-2018"));
    }
}
