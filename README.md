# pdf-scan

![pdf-scan logo](loupe.svg)

## about OCR
Optical character recognition or optical character reader, often abbreviated as OCR, is the mechanical or electronic conversion of images of typed, handwritten or printed text into machine-encoded text, whether from a scanned document, a photo of a document, a scene-photo (for example the text on signs and billboards in a landscape photo) or from subtitle text superimposed on an image (for example from a television broadcast).

## about iText
iText is a library for creating and manipulating PDF files in Java and .NET.  

iText was written by Bruno Lowagie. The source code was initially distributed as open source under the Mozilla Public License or the GNU Library General Public License open source licenses. However, as of version 5.0.0 (released Dec 7, 2009) it is distributed under the Affero General Public License version 3. A fork of the LGPL/MPL licensed version of iText is currently actively maintained as the OpenPDF library on GitHub. iText is also available through a proprietary license, distributed by iText Software NV.

## example code

    // initialize tesseract
    TesseractOpticalCharacterRecognitionEngine ocrEngine = new TesseractOpticalCharacterRecognitionEngine(new File("tessdata"), "eng");

    // create document
    PdfDocument pdfDocument = new PdfDocument(new PdfReader(new File("scan_0001.pdf")));

    // extract text
    SimpleTextExtractionStrategy simpleTextExtractionStrategy = new SimpleTextExtractionStrategy();
    OCRTextExtractionStrategy ocrTextExtractionStrategy = new OCRTextExtractionStrategy(simpleTextExtractionStrategy, ocrEngine);
    new PdfCanvasProcessor(ocrTextExtractionStrategy).processPageContent(pdfDocument.getPage(1));

    // display
    System.out.println(simpleTextExtractionStrategy.getResultantText());

