package com.js.canvas.parser.listener.font;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.js.canvas.parser.data.OCRTextRenderInfo;
import com.js.canvas.parser.listener.ChainableEventListener;

import java.io.IOException;

/**
 * This class modifies the baseline of a {@link OCRTextRenderInfo} object.
 * It does so by calculating the ratio height / decender for the text in each {@link OCRTextRenderInfo}
 * in the {@link PdfFont} Helvetica.
 */
public class LocalBaselineModifier extends ChainableEventListener {

    @Override
    public void eventOccurred(IEventData data, EventType type) {
        if (data instanceof OCRTextRenderInfo) {
            getNext().eventOccurred(correctBaseline((OCRTextRenderInfo) data), EventType.RENDER_TEXT);
        } else {
            getNext().eventOccurred(data, type);
        }
    }

    private IEventData correctBaseline(OCRTextRenderInfo data) {
        String s0 = data.getText();
        try {
            PdfFont helvetica = PdfFontFactory.createFont();
            double ascent = helvetica.getAscent(s0, 10);
            double descent = helvetica.getDescent(s0, 10);

            double deltaUp = (double) data.getOCRChunk().getLocation().height * ((double) -descent / (double) ascent);
            data.getOCRChunk().getLocation().y += deltaUp;

        } catch (IOException e) {
        }
        return data;
    }


}
