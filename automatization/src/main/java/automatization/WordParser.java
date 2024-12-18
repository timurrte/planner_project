package automatization;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class WordParser {
    private static final ArrayList<String> WORD_LIST = new ArrayList<>();

    static {
        try (InputStream inputStream = WordParser.class.getResourceAsStream("/classes.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            
            if (inputStream == null) {
                throw new IOException("Resource 'classes.txt' not found.");
            }

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
        for (String word : WORD_LIST) {
            if (candidate.startsWith(word)) {
                return word;
            }
        }
        return null;
    }
    
    public static String reconstructWord(String fragmentedWord) {
        StringBuilder result = new StringBuilder();
        StringBuilder word = new StringBuilder();
        String[] parts = fragmentedWord.replaceAll("[`]", "").split("\\s+");

        for (String part : parts) {
            word.append(part);
            String match = getMatchingWord(word.toString());
            if (match != null) {
                result.append(match).append(" ");
                word.setLength(0);
            }
        }

        if (word.length() > 0) {
            String finalMatch = getMatchingWord(word.toString().trim());
            if (finalMatch != null) {
                result.append(finalMatch);
            } else {
                result.append(word.toString().trim());
            }
        }

        return result.toString().trim();
    }




}