package com.js.canvas.parser.listener;

import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;
import com.js.canvas.parser.data.OCRTextRenderInfo;

import java.util.Set;

public class IgnoreEmptyText implements FlushableEventListener {

    private IEventListener innerListener;

    public IgnoreEmptyText(IEventListener innerListener){
        this.innerListener = innerListener;
    }

    @Override
    public void flush() {
        // flush
        if(innerListener instanceof FlushableEventListener){
            ((FlushableEventListener) innerListener).flush();
        }
    }

    @Override
    public void eventOccurred(IEventData data, EventType type) {
        if(data instanceof OCRTextRenderInfo){
            OCRTextRenderInfo ocrTextRenderInfo = (OCRTextRenderInfo) data;
            // spaces
            if(ocrTextRenderInfo.getText().matches("[\\s\t]*"))
                return;
            // dashes
            if(ocrTextRenderInfo.getText().matches("---+"))
                return;
            if(ocrTextRenderInfo.getText().matches("___+"))
                return;
            if(ocrTextRenderInfo.getText().matches("\\.\\.\\.+"))
                return;
            if(ocrTextRenderInfo.getText().matches("[-\\-~_=][-\\-~_=][-\\-~_=]*"))
                return;
            // default
            innerListener.eventOccurred(data, type);
        }else{
            innerListener.eventOccurred(data, type);
        }
    }

    @Override
    public Set<EventType> getSupportedEvents() {
        return null;
    }
}
