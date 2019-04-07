import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.SimpleTextExtractionStrategy;
import com.js.canvas.pdf.ScannedPdfDocument;
import com.js.tesseract.TesseractOpticalCharacterRecognitionEngine;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class ConvertScannedDocumentTest {

    @Test
    public void convert() throws IOException {

        // initialize tesseract
        TesseractOpticalCharacterRecognitionEngine ocrEngine = new TesseractOpticalCharacterRecognitionEngine(new File("/home/joris/Code/tessdata"), "eng");

        String[] documents = {"input_001.pdf", "input_002.pdf", "input_003.pdf", "input_004.pdf"};

        for (String inputPDF : documents) {

            // create document
            ScannedPdfDocument pdfDocument = new ScannedPdfDocument(
                    new PdfReader(getClass().getClassLoader().getResourceAsStream(inputPDF)),
                    new PdfWriter(new File("out_" + inputPDF)),
                    ocrEngine
            );

            // perform OCR
            pdfDocument.doOCR(1);

            // close
            pdfDocument.close();
        }

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
