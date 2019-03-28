package com.js.canvas.parser.data;

import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.LineSegment;
import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.geom.Vector;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.canvas.CanvasTag;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.js.IOpticalCharacterRecognitionEngine;
import com.js.canvas.ModifiableGraphicsState;

import java.io.IOException;
import java.util.Stack;

/**
 * This class provides a convenient way to convert {@link com.js.IOpticalCharacterRecognitionEngine.OCRChunk} objects into
 * {@link TextRenderInfo} objects. To the outside world, it acts as {@link TextRenderInfo}, however it only keeps the state needed
 * to perform text extraction. Most of the other fields present in {@link TextRenderInfo} (such as GraphicsState) are filled with dummy data.
 */
public class OCRTextRenderInfo extends TextRenderInfo {

    private IOpticalCharacterRecognitionEngine.OCRChunk ocrChunk;
    private static ModifiableGraphicsState gs;

    static{
        gs = new ModifiableGraphicsState();
        gs.setCtm(new Matrix());
        try { gs.setFont(PdfFontFactory.createFont()); } catch (IOException e) { }
    }

    public OCRTextRenderInfo(IOpticalCharacterRecognitionEngine.OCRChunk chunk){
        super(new PdfString(chunk.getText().replaceAll("\n","")), gs, new Matrix(), new Stack<CanvasTag>());
        this.ocrChunk = chunk;
    }

    @Override
    public LineSegment getBaseline(){
        float x = ocrChunk.getLocation().x;
        float y = ocrChunk.getLocation().y;
        float w = (float) ocrChunk.getLocation().getWidth();
        return new LineSegment(new Vector(x, y, 1), new Vector(x + w, y, 1));
    }
}