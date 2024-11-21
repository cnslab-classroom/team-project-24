package group24;

import java.io.Serializable;

public class Flashcard implements Serializable {
    private static final long serialVersionUID = 1L; // 직렬화 버전 ID
    private String word;
    private String meaning;

    public Flashcard(String word, String meaning) {
        this.word = word;
        this.meaning = meaning;
    }

    public String getWord() {
        return word;
    }

    public String getMeaning() {
        return meaning;
    }
}
