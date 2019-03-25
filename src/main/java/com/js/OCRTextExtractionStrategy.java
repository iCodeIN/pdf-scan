package com.js;

import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.canvas.CanvasGraphicsState;
import com.itextpdf.kernel.pdf.canvas.CanvasTag;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.ImageRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextExtractionStrategy;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Logger;

/**
 * This class converts images found in PDF documents to TextRender events.
 * This allows other {@link ITextExtractionStrategy} implementations to handle this text.
 */
public class OCRTextExtractionStrategy implements ITextExtractionStrategy {

    private final ITextExtractionStrategy innerStrategy;
    private final IOpticalCharacterRecognitionEngine opticalCharacterRecognitionEngine;
    private final Logger logger = Logger.getLogger(OCRTextExtractionStrategy.class.getSimpleName());

    public OCRTextExtractionStrategy(ITextExtractionStrategy innerStrategy, IOpticalCharacterRecognitionEngine opticalCharacterRecognitionEngine){
        this.innerStrategy = innerStrategy;
        this.opticalCharacterRecognitionEngine = opticalCharacterRecognitionEngine;
    }

    public String getResultantText() {
        return innerStrategy.getResultantText();
    }

    public void eventOccurred(IEventData iEventData, EventType eventType) {
        // handle images
        if(eventType == EventType.RENDER_IMAGE){

            // extract coordinates
            ImageRenderInfo imageRenderInfo  = (ImageRenderInfo) iEventData;
            float x = imageRenderInfo.getImageCtm().get(Matrix.I11);
            float y = imageRenderInfo.getImageCtm().get(Matrix.I22);

            // attempt to parse image
            try {
                BufferedImage bufferedImage = imageRenderInfo.getImage().getBufferedImage();
                for(IOpticalCharacterRecognitionEngine.OCRChunk chunk : opticalCharacterRecognitionEngine.doOCR(bufferedImage)){
                    if(chunk.getText() != null && !chunk.getText().isEmpty()) {
                        chunk.getLocation().translate((int) x, (int) y);
                        TextRenderInfo textRenderInfo = pseudoTextRenderInfo(chunk);
                        if(textRenderInfo !=  null)
                            innerStrategy.eventOccurred( textRenderInfo, EventType.RENDER_TEXT);
                    }
                }
            } catch (IOException e) { logger.severe(e.getLocalizedMessage()); }

        }
        // handle anything else
        else {
            innerStrategy.eventOccurred(iEventData, eventType);
        }
    }

    private TextRenderInfo pseudoTextRenderInfo(IOpticalCharacterRecognitionEngine.OCRChunk chunk){

        // dummy graphics state
        ModifiableGraphicsState mgs = new ModifiableGraphicsState();
        try {
            mgs.setFont(PdfFontFactory.createFont());
            mgs.setCtm(new Matrix(  1,0,0,
                                    0,1,0,
                                    0,0,1));
        } catch (IOException e) { }

        // dummy text matrix
        float x = chunk.getLocation().x;
        float y = chunk.getLocation().y;
        Matrix textMatrix = new Matrix( x, 0,0,
                                    0, y, 0,
                                    0,0,0);

        // return TextRenderInfo object
        return new TextRenderInfo(
                new PdfString(chunk.getText(), ""),
                mgs,
                textMatrix,
                new Stack<CanvasTag>()

        );
    }

    public Set<EventType> getSupportedEvents() { return null; }

}

class ModifiableGraphicsState extends CanvasGraphicsState{

    private Matrix ctm;

    public ModifiableGraphicsState(){ super(); }

    public Matrix getCtm() { return ctm; }
    public ModifiableGraphicsState setCtm(Matrix ctm){this.ctm = ctm; return this;};
    public void updateCtm(float a, float b, float c, float d, float e, float f) { updateCtm(new Matrix(a, b, c, d, e, f)); }
    public void updateCtm(Matrix newCtm) {
        ctm = newCtm.multiply(ctm);
    }

}