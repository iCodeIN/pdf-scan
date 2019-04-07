package com.js.canvas.parser.listener.text.spellcheck;

import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.js.canvas.parser.data.OCRTextRenderInfo;
import com.js.canvas.parser.listener.ChainableEventListener;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class SpellCheckModifier extends ChainableEventListener {

    private BKTree<String> dictionary = new BKTree<>(new BKTree.Metric<String>() {
        @Override
        public int distance(String obj0, String obj1) {
            return Levenshtein.distance(obj0, obj1, false);
        }
    });

    public SpellCheckModifier(InputStream dictionaryStream) {
        // read dictionary
        Scanner scanner = new Scanner(dictionaryStream);
        while (scanner.hasNextLine()) {
            String word = scanner.nextLine();
            dictionary.add(word);
        }
        scanner.close();
    }

    @Override
    public void eventOccurred(IEventData data, EventType type) {
        if (data instanceof OCRTextRenderInfo) {
            getNext().eventOccurred(correctSpelling((OCRTextRenderInfo) data), type);
        } else {
            getNext().eventOccurred(data, type);
        }
    }

    private OCRTextRenderInfo correctSpelling(OCRTextRenderInfo ocrTextRenderInfo) {
        String s0 = ocrTextRenderInfo.getText();

        // word is ok
        if (dictionary.contains(s0))
            return ocrTextRenderInfo;

        CaseType caseType = CaseType.getCaseType(s0);
        if (caseType == CaseType.MIXED)
            return ocrTextRenderInfo;

        // find correction
        for (int i = 1; i <= 2; i++) {
            // find corrections
            Set<String> tmp = new HashSet<>(dictionary.get(s0.toLowerCase(), i));

            // perform correction
            if (tmp.size() == 1) {
                String s1 = tmp.iterator().next();
                s1 = CaseType.forceCase(s1, caseType);
                ocrTextRenderInfo.getOCRChunk().setText(s1);
                break;
            }
        }

        // return
        return ocrTextRenderInfo;
    }

}
