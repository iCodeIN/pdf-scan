package com.js.canvas.parser.listener;

import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;

/**
 * This class allows {@link IEventListener} implementations to 'wait' for
 * input to have finished. This may be needed.
 * e.g. The {@link BaseLineModifier} needs to wait for all its chunks to have
 * been processed before it can re-output the (modified) {@link com.js.canvas.parser.data.OCRTextRenderInfo} objects.
 */
public interface FlushableEventListener extends IEventListener {

    void flush();

}
