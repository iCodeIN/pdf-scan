package com.js.canvas.parser.listener.text;

import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.js.canvas.parser.data.OCRTextRenderInfo;
import com.js.canvas.parser.listener.ChainableEventListener;

public class LocalUselessTextModifier extends ChainableEventListener {

    @Override
    public void eventOccurred(IEventData data, EventType type) {
        if (data instanceof OCRTextRenderInfo) {
            OCRTextRenderInfo ocrTextRenderInfo = (OCRTextRenderInfo) data;
            // spaces
            if (ocrTextRenderInfo.getText().matches("[\\s\t]*"))
                return;
            // dashes
            if (ocrTextRenderInfo.getText().matches("---+"))
                return;
            if (ocrTextRenderInfo.getText().matches("___+"))
                return;
            if (ocrTextRenderInfo.getText().matches("\\.\\.\\.+"))
                return;
            if (ocrTextRenderInfo.getText().matches("[-\\-~_=][-\\-~_=][-\\-~_=]*"))
                return;
            // default
            getNext().eventOccurred(data, type);
        } else {
            getNext().eventOccurred(data, type);
        }
    }

}
