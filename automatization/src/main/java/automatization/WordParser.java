package automatization;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class WordParser {
    private static final ArrayList<String> WORD_LIST = new ArrayList<>();

    static {
        try (BufferedReader reader = new BufferedReader(new FileReader("classes.txt"))) {
            String word;
            while ((word = reader.readLine()) != null) {
                WORD_LIST.add(word.trim());
            }

            WORD_LIST.sort((a, b) -> b.length() - a.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isInList(String word) {
        return WORD_LIST.contains(word);
    }

    public static String getMatchingWord(String candidate) {
    	ArrayList<String> matchedWords = new ArrayList<String>();
        for (String word : WORD_LIST) {
            if (word.startsWith(candidate)) {
                matchedWords.add(word);
            }
        }
        if (matchedWords.size() == 1) return matchedWords.get(0);
        return null;
    }
    
    public static String reconstructWord(String fragmentedWord) {
        StringBuilder result = new StringBuilder();
        String[] parts = fragmentedWord.split("\\s+");
        
        
        for (String part : parts) {
            if (isInList(part)) {
                result.append(part).append(" ");
            } else {
                String match = getMatchingWord(part);
                if (match != null) {
                    result.append(match).append(" ");
                }
            }
        }
        return result.toString().trim();
    }


}

