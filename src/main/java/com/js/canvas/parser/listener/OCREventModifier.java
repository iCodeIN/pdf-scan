package com.js.canvas.parser.listener;

import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.ImageRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextExtractionStrategy;
import com.js.canvas.parser.ocr.OCRChunk;
import com.js.canvas.parser.ocr.IOpticalCharacterRecognitionEngine;
import com.js.canvas.parser.data.OCRTextRenderInfo;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * This class converts images found in PDF documents to TextRender events.
 * This allows other {@link ITextExtractionStrategy} implementations to handle this text.
 */
public class OCREventModifier implements FlushableEventListener {

    private final IEventListener innerListener;
    private final IOpticalCharacterRecognitionEngine opticalCharacterRecognitionEngine;
    private final Logger logger = Logger.getLogger(OCREventModifier.class.getSimpleName());

    public OCREventModifier(IEventListener innerListener, IOpticalCharacterRecognitionEngine opticalCharacterRecognitionEngine){
        this.innerListener = innerListener;
        this.opticalCharacterRecognitionEngine = opticalCharacterRecognitionEngine;
    }

    public void eventOccurred(IEventData iEventData, EventType eventType) {
        // handle images
        if(eventType == EventType.RENDER_IMAGE){

            // extract coordinates
            ImageRenderInfo imageRenderInfo  = (ImageRenderInfo) iEventData;
            float x0 = imageRenderInfo.getImageCtm().get(Matrix.I31);
            float y0 = imageRenderInfo.getImageCtm().get(Matrix.I32);
            float w0 = imageRenderInfo.getImageCtm().get(Matrix.I11);
            float h0 = imageRenderInfo.getImageCtm().get(Matrix.I22);

            // attempt to parse image
            try {
                BufferedImage bufferedImage = imageRenderInfo.getImage().getBufferedImage();
                float w1 = bufferedImage.getWidth();
                float h1 = bufferedImage.getHeight();

                List<OCRChunk> ocrChunkList = opticalCharacterRecognitionEngine.doOCR(bufferedImage);

                for(OCRChunk chunk : ocrChunkList){
                    if(chunk.getText() != null && !chunk.getText().isEmpty()) {

                        float x1 = chunk.getLocation().x;
                        float y1 = chunk.getLocation().y;

                        float x = x1 * (w0 / w1);
                        float y = y1 * (h0 / h1);
                        float w = (float) chunk.getLocation().getWidth() * (w0 / w1);
                        float h = (float) chunk.getLocation().getHeight() * (h0 / h1);

                        chunk.getLocation().x = (int) (x + x0);
                        chunk.getLocation().y = (int) (y0 + h0 - y - h);
                        chunk.getLocation().width = (int) w;
                        chunk.getLocation().height = (int) h;

                        TextRenderInfo textRenderInfo = new OCRTextRenderInfo(chunk);
                        innerListener.eventOccurred( textRenderInfo, EventType.RENDER_TEXT);
                    }
                }

            } catch (IOException e) { logger.severe(e.getLocalizedMessage()); }

        }
        // handle anything else
        else {
            innerListener.eventOccurred(iEventData, eventType);
        }
    }

    public Set<EventType> getSupportedEvents() { return null; }

    @Override
    public void flush() {
        if(innerListener instanceof FlushableEventListener)
            ((FlushableEventListener) innerListener).flush();
    }
}