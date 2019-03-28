package com.js.canvas.parser.listener;

import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;

public interface FlushableEventListener extends IEventListener {

    void flush();

}
