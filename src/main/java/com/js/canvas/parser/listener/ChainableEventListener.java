package com.js.canvas.parser.listener;

import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;

import java.util.Set;

public abstract class ChainableEventListener implements IEventListener {

    private IEventListener innerListener;

    @Override
    public abstract void eventOccurred(IEventData iEventData, EventType eventType);

    public IEventListener getNext() {
        return innerListener;
    }

    public ChainableEventListener setNext(IEventListener listener) {
        this.innerListener = listener;
        if (listener instanceof ChainableEventListener)
            return (ChainableEventListener) innerListener;
        return null;
    }

    public void flush() {
        if (innerListener instanceof ChainableEventListener) {
            ((ChainableEventListener) innerListener).flush();
        }
    }

    @Override
    public Set<EventType> getSupportedEvents() {
        return null;
    }

}
