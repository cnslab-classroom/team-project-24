import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.stream.Collectors;

public class VocabularyManager {

    private List<Word> wordList; // 단어 리스트
    private final String DATA_FILE = "vocabulary.json"; // JSON 데이터 파일 경로

    // 프로그램 시작 시 JSON 파일에서 단어 데이터를 불러옴
    public VocabularyManager() {
        wordList = new ArrayList<>();
        loadWords(); //기존 데이터를 로드 (JSON 파일이 있으면 읽음)
    }

    // 단어 추가 (GUI에서 추가 버튼 클릭 시 호출)
    // ex) manager.addWord(term, meaning);
    public void addWord(String term, String meaning) {
        wordList.add(new Word(term, meaning));
        saveWords(); // 데이터 저장( JSON파일 )
    }

    // 단어 삭제 (GUI에서 삭제 버튼 클릭 시 호출)
    // ex) manager.removeWord(term, meaning);
    public void removeWord(String term) {
        wordList.removeIf(word -> word.getTerm().equalsIgnoreCase(term));
        saveWords(); // 데이터 저장( JSON파일 )
    }

    // 현재 단어 리스트를 반환 (GUI에서 사용)
    public List<Word> getWordList() {
        return wordList;
    }

    // JSON 파일로 단어 저장
    public void saveWords() {
        JSONArray jsonArray = new JSONArray();
        for (Word word : wordList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("term", word.getTerm());
            jsonObject.put("meaning", word.getMeaning());
            jsonArray.put(jsonObject);
        }

        try (FileWriter writer = new FileWriter(DATA_FILE)) {
            writer.write(jsonArray.toString(4)); // JSON 데이터를 파일에 저장
        } catch (IOException e) {
            System.err.println("Error saving words: " + e.getMessage());
        }
    }

    // JSON 파일에서 단어 불러오기
    public void loadWords() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return; // 파일이 없으면 불러오지 않음
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder jsonData = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonData.append(line);
            }

            JSONArray jsonArray = new JSONArray(jsonData.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String term = jsonObject.getString("term");
                String meaning = jsonObject.getString("meaning");
                wordList.add(new Word(term, meaning));
            }
        } catch (IOException e) {
            System.err.println("Error loading words: " + e.getMessage());
        }
    }

    // 단어 클래스
    public static class Word {
        private String term;     // 단어
        private String meaning;  // 뜻

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
