package com.js.canvas;

import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.pdf.canvas.CanvasGraphicsState;

/**
 * This class provides a modifiable {@link CanvasGraphicsState} implementation.
 * A lot of the fields in {@link CanvasGraphicsState} were not modifiable (probably for good reason).
 * This class provides setters for those fields that need to be editable for the OCR-flow.
 */
public class ModifiableGraphicsState extends CanvasGraphicsState {

    private Matrix ctm;

    public ModifiableGraphicsState(){ super(); }

    public Matrix getCtm() { return ctm; }
    public ModifiableGraphicsState setCtm(Matrix ctm){this.ctm = ctm; return this;};
    public void updateCtm(float a, float b, float c, float d, float e, float f) { updateCtm(new Matrix(a, b, c, d, e, f)); }
    public void updateCtm(Matrix newCtm) {
        ctm = newCtm.multiply(ctm);
    }

}

