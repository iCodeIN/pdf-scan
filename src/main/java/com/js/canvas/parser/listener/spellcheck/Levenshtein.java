package com.js.canvas.parser.listener.spellcheck;

public class Levenshtein {

    /**
     * Calculates the Levenshtein distance between two Strings
     *
     * @param a first String
     * @param b second String
     * @return the minimal number of edits (character inserts, character changes, character deletions) needed to transform a into b
     */
    public static int distance(String a, String b, boolean caseSensitive) {
        // i == 0
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int transitionCost = characterTransitionCost(a.charAt(i - 1), b.charAt(j - 1), caseSensitive) + nw;
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), transitionCost);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }

    private static int characterTransitionCost(char a, char b, boolean caseSensitive) {
        if (a == b)
            return 0;
        if (!caseSensitive && Character.toUpperCase(a) == Character.toUpperCase(b))
            return 0;
        if (Character.isAlphabetic(a) && Character.isAlphabetic(b))
            return 1;
        return 2;
    }

}