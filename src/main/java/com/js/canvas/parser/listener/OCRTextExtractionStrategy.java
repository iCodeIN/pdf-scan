package com.js.canvas.parser.listener;

import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.ImageRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextExtractionStrategy;
import com.js.IOpticalCharacterRecognitionEngine;
import com.js.canvas.parser.data.OCRTextRenderInfo;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Set;
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
                        TextRenderInfo textRenderInfo = new OCRTextRenderInfo(chunk);
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

    public Set<EventType> getSupportedEvents() { return null; }

}