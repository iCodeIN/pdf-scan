package com.js.canvas.parser.listener.spellcheck;

import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;
import com.js.canvas.parser.data.OCRTextRenderInfo;
import com.js.canvas.parser.listener.FlushableEventListener;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class SpellCheckModifier implements FlushableEventListener {

    private IEventListener innerListener;
    private BKTree<String> dictionary = new BKTree<>(new BKTree.Metric<String>() {
        @Override
        public int distance(String obj0, String obj1) { return Levenshtein.distance(obj0, obj1, true); }
    });

    public SpellCheckModifier(IEventListener innerListener, InputStream dictionaryStream){
        this.innerListener = innerListener;

        // read dictionary
        Scanner scanner = new Scanner(dictionaryStream);
        while(scanner.hasNextLine()){
            String word = scanner.nextLine();
            dictionary.add(word);
        }
        scanner.close();
    }

    @Override
    public void flush() {
        if(innerListener instanceof FlushableEventListener){
            ((FlushableEventListener) innerListener).flush();
        }
    }

    @Override
    public void eventOccurred(IEventData data, EventType type) {
        if(data instanceof OCRTextRenderInfo){
            innerListener.eventOccurred(correctSpelling((OCRTextRenderInfo) data), type);
        }else{
            innerListener.eventOccurred(data, type);
        }
    }

    private OCRTextRenderInfo correctSpelling(OCRTextRenderInfo ocrTextRenderInfo){
        String s0 = ocrTextRenderInfo.getText();

        // word is ok
        if(dictionary.contains(s0))
            return ocrTextRenderInfo;

        // word is uppercase only and lowercase version exists
        boolean capsOnly = s0.toUpperCase().equals(s0);
        if(capsOnly && dictionary.contains(s0.toLowerCase()))
            return ocrTextRenderInfo;

        // find correction
        Set<String> suggestedWords = new HashSet<>(dictionary.get(s0, 1));
        if(capsOnly){
            for(String s : dictionary.get(s0.toLowerCase(), 1))
                suggestedWords.add(s.toUpperCase());
        }

        // perform correction
        if(suggestedWords.size() == 1){
            ocrTextRenderInfo.getOCRChunk().setText(suggestedWords.iterator().next());
        }

        // return
        return ocrTextRenderInfo;
    }

    @Override
    public Set<EventType> getSupportedEvents() {
        return null;
    }
}
