package automatization;

import java.util.HashSet;
import java.util.Set;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class WordParser {
    private static final Set<String> WORD_LIST = new HashSet<>();

    static {
        try (BufferedReader reader = new BufferedReader(new FileReader("classes.txt"))) {
            String word;
            while ((word = reader.readLine()) != null) {
                WORD_LIST.add(word.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isInList(String word) {
        return WORD_LIST.contains(word);
    }
}
