package com.js.canvas.parser.data;

import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.LineSegment;
import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.geom.Vector;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.canvas.CanvasTag;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.js.canvas.ModifiableGraphicsState;
import com.js.canvas.parser.ocr.OCRChunk;

import java.io.IOException;
import java.util.Stack;

/**
 * This class provides a convenient way to convert {@link OCRChunk} objects into
 * {@link TextRenderInfo} objects. To the outside world, it acts as {@link TextRenderInfo}, however it only keeps the state needed
 * to perform text extraction. Most of the other fields present in {@link TextRenderInfo} (such as GraphicsState) are filled with dummy data.
 */
public class OCRTextRenderInfo extends TextRenderInfo {

    private static ModifiableGraphicsState gs;

    static {
        gs = new ModifiableGraphicsState();
        gs.setCtm(new Matrix());
        try {
            gs.setFont(PdfFontFactory.createFont());
        } catch (IOException e) {
        }
    }

    private OCRChunk ocrChunk;

    /**
     * Construct a new {@link OCRTextRenderInfo} based on an {@link OCRChunk}
     *
     * @param chunk
     */
    public OCRTextRenderInfo(OCRChunk chunk) {
        super(new PdfString(chunk.getText().replaceAll("\n", "")), gs, new Matrix(), new Stack<CanvasTag>());
        this.ocrChunk = chunk;
    }

    /**
     * Get the {@link OCRChunk}
     *
     * @return
     */
    public OCRChunk getOCRChunk() {
        return ocrChunk;
    }

    @Override
    public String getText(){ return ocrChunk.getText(); }

    @Override
    public LineSegment getBaseline() {
        float x = ocrChunk.getLocation().x;
        float y = ocrChunk.getLocation().y;
        float w = (float) ocrChunk.getLocation().getWidth();
        return new LineSegment(new Vector(x, y, 1), new Vector(x + w, y, 1));
    }
}