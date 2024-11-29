package group24;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VocabularyManager {

    private List<Word> wordList; // 단어 리스트
    private final String DATA_FILE = "vocabulary.json"; // JSON 데이터 파일 경로

    // Jackson ObjectMapper 인스턴스
    private final ObjectMapper mapper;

    // 생성자
    public VocabularyManager() {
        mapper = new ObjectMapper(); // ObjectMapper 초기화
        wordList = new ArrayList<>();
        loadWords(); // 기존 데이터를 로드 (JSON 파일이 있으면 읽음)
    }

    // 단어 추가
    public void addWord(String term, String meaning) {
        wordList.add(new Word(term, meaning));
        saveWords(); // 데이터 저장 (JSON 파일)
    }

    // 단어 삭제
    public void removeWord(String term) {
        wordList.removeIf(word -> word.getTerm().equalsIgnoreCase(term));
        saveWords(); // 데이터 저장 (JSON 파일)
    }

    // 단어 리스트 반환
    public List<Word> getWordList() {
        return wordList;
    }

// JSON 파일로 단어 저장(인코딩 UTF-8로 변경)
public void saveWords() {
    try (Writer writer = new OutputStreamWriter(new FileOutputStream(DATA_FILE), "UTF-8")) {
        mapper.writerWithDefaultPrettyPrinter().writeValue(writer, wordList);
    } catch (IOException e) {
        System.err.println("Error saving words: " + e.getMessage());
    }
}

// JSON 파일에서 단어 불러오기(인코딩 UTF-8로 변경)
public void loadWords() {
    File file = new File(DATA_FILE);
    if (!file.exists()) {
        return; // 파일이 없으면 불러오지 않음
    }

    try (Reader reader = new InputStreamReader(new FileInputStream(file), "UTF-8")) {
        wordList = mapper.readValue(reader, new TypeReference<List<Word>>() {});
    } catch (IOException e) {
        System.err.println("Error loading words: " + e.getMessage());
    }
}

    // 단어 클래스
    public static class Word {
        private String term;     // 단어
        private String meaning;  // 뜻

        // Jackson 직렬화를 위한 기본 생성자
        public Word() {}

        public Word(String term, String meaning) {
            this.term = term;
            this.meaning = meaning;
        }

        public String getTerm() {
            return term;
        }

        public String getMeaning() {
            return meaning;
        }

        @Override
        public String toString() {
            return term + " - " + meaning;
        }
    }

    // 단어(term)로 검색
    public List<Word> searchByTerm(String term) {
        return wordList.stream()
                .filter(word -> word.getTerm().toLowerCase().contains(term.toLowerCase()))
                .collect(Collectors.toList());
    }

    // 뜻(meaning)으로 검색
    public List<Word> searchByMeaning(String meaning) {
        return wordList.stream()
                .filter(word -> word.getMeaning().toLowerCase().contains(meaning.toLowerCase()))
                .collect(Collectors.toList());
    }
}
