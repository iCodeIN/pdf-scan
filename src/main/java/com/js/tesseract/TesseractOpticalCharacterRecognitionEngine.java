package com.js.tesseract;

import com.js.canvas.parser.ocr.IOpticalCharacterRecognitionEngine;
import com.js.canvas.parser.ocr.OCRChunk;
import com.sun.jna.Pointer;
import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.TessAPI1;
import net.sourceforge.tess4j.util.ImageIOHelper;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class TesseractOpticalCharacterRecognitionEngine implements IOpticalCharacterRecognitionEngine {

    private ITessAPI.TessBaseAPI handle = TessAPI1.TessBaseAPICreate();
    private String languageCode;
    private File tesseractDataDirectory;

    public TesseractOpticalCharacterRecognitionEngine(File tesseractDataDirectory, String languageCode) {

        // set data path
        if (!tesseractDataDirectory.exists())
            throw new IllegalArgumentException();
        this.tesseractDataDirectory = tesseractDataDirectory;

        // set language code
        if (!new File(tesseractDataDirectory, languageCode + ".traineddata").exists())
            throw new IllegalArgumentException();
        this.languageCode = languageCode;
    }

    public List<OCRChunk> doOCR(BufferedImage image) {
        List<OCRChunk> ocrChunkList = new ArrayList<>();

        ByteBuffer buf = ImageIOHelper.convertImageData(image);
        int bpp = image.getColorModel().getPixelSize();
        int bytespp = bpp / 8;
        int bytespl = (int) Math.ceil(image.getWidth() * bpp / 8.0);
        TessAPI1.TessBaseAPIInit3(handle, tesseractDataDirectory.getAbsolutePath(), languageCode);
        TessAPI1.TessBaseAPISetPageSegMode(handle, TessAPI1.TessPageSegMode.PSM_AUTO);
        TessAPI1.TessBaseAPISetImage(handle, buf, image.getWidth(), image.getHeight(), bytespp, bytespl);
        TessAPI1.TessBaseAPIRecognize(handle, null);
        TessAPI1.TessResultIterator ri = TessAPI1.TessBaseAPIGetIterator(handle);
        TessAPI1.TessPageIterator pi = TessAPI1.TessResultIteratorGetPageIterator(ri);
        TessAPI1.TessPageIteratorBegin(pi);

        do {
            Pointer ptr = TessAPI1.TessResultIteratorGetUTF8Text(ri, TessAPI1.TessPageIteratorLevel.RIL_WORD);
            if (ptr == null)
                continue;
            String word = ptr.getString(0);
            TessAPI1.TessDeleteText(ptr);
            float confidence = TessAPI1.TessResultIteratorConfidence(ri, TessAPI1.TessPageIteratorLevel.RIL_WORD);
            IntBuffer leftB = IntBuffer.allocate(1);
            IntBuffer topB = IntBuffer.allocate(1);
            IntBuffer rightB = IntBuffer.allocate(1);
            IntBuffer bottomB = IntBuffer.allocate(1);
            TessAPI1.TessPageIteratorBoundingBox(pi, TessAPI1.TessPageIteratorLevel.RIL_WORD, leftB, topB, rightB, bottomB);
            int left = leftB.get();
            int top = topB.get();
            int right = rightB.get();
            int bottom = bottomB.get();

            IntBuffer boldB = IntBuffer.allocate(1);
            IntBuffer italicB = IntBuffer.allocate(1);
            IntBuffer underlinedB = IntBuffer.allocate(1);
            IntBuffer monospaceB = IntBuffer.allocate(1);
            IntBuffer serifB = IntBuffer.allocate(1);
            IntBuffer smallcapsB = IntBuffer.allocate(1);
            IntBuffer pointSizeB = IntBuffer.allocate(1);
            IntBuffer fontIdB = IntBuffer.allocate(1);
            String fontName = TessAPI1.TessResultIteratorWordFontAttributes(ri, boldB, italicB, underlinedB,
                    monospaceB, serifB, smallcapsB, pointSizeB, fontIdB);
            boolean bold = boldB.get() == TessAPI1.TRUE;
            boolean italic = italicB.get() == TessAPI1.TRUE;
            boolean underlined = underlinedB.get() == TessAPI1.TRUE;
            boolean monospace = monospaceB.get() == TessAPI1.TRUE;
            boolean serif = serifB.get() == TessAPI1.TRUE;
            boolean smallcaps = smallcapsB.get() == TessAPI1.TRUE;
            int pointSize = pointSizeB.get();
            int fontId = fontIdB.get();

            int w = java.lang.Math.abs(left - right);
            int h = java.lang.Math.abs(top - bottom);
            if(w == 0 || h == 0)
                continue;

            BufferedImage chunkImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    chunkImage.setRGB(i, j, image.getRGB(left + i, top + j));
                }
            }

            OCRChunk ocrChunk = new OCRChunk(new Rectangle(left, top, java.lang.Math.abs(left - right), java.lang.Math.abs(top - bottom)), word)
                    .setBold(bold)
                    .setItalic(italic)
                    .setUnderlined(underlined)
                    .setMonospaced(monospace)
                    .setFontSize(pointSize)
                    .setImage(chunkImage)
                    .setConfidence(confidence / 100.0);

            ocrChunkList.add(ocrChunk);
        } while (TessAPI1.TessPageIteratorNext(pi, TessAPI1.TessPageIteratorLevel.RIL_WORD) == TessAPI1.TRUE);

        return ocrChunkList;
    }
}
