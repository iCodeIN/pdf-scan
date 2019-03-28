# pdf-scan

<img src="loupe.svg" width="100" height="100">

## about OCR
Optical character recognition or optical character reader, often abbreviated as OCR, is the mechanical or electronic conversion of images of typed, handwritten or printed text into machine-encoded text, whether from a scanned document, a photo of a document, a scene-photo (for example the text on signs and billboards in a landscape photo) or from subtitle text superimposed on an image (for example from a television broadcast).

## about iText
iText is a library for creating and manipulating PDF files in Java and .NET.  

iText was written by Bruno Lowagie. The source code was initially distributed as open source under the Mozilla Public License or the GNU Library General Public License open source licenses. However, as of version 5.0.0 (released Dec 7, 2009) it is distributed under the Affero General Public License version 3. A fork of the LGPL/MPL licensed version of iText is currently actively maintained as the OpenPDF library on GitHub. iText is also available through a proprietary license, distributed by iText Software NV.

## example code

    // initialize tesseract
    TesseractOpticalCharacterRecognitionEngine ocrEngine = new TesseractOpticalCharacterRecognitionEngine(new File("C:\\Users\\joris\\Downloads\\tessdata"), "eng");

     // create document
     PdfDocument pdfDocument = new PdfDocument(new PdfReader(getClass().getClassLoader().getResourceAsStream("input_document.pdf")));

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

