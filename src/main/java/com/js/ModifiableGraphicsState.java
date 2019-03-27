package com.js;

import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.pdf.canvas.CanvasGraphicsState;

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

