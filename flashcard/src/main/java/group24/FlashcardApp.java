package group24;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class FlashcardApp extends Application {

    private VocabularyManager vocabularyManager; // VocabularyManager 객체

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Flashcard App");

        // VocabularyManager 초기화 (JSON 데이터 불러오기)
        vocabularyManager = new VocabularyManager();

        // GridPane layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(10);

        // Word input field
        Label wordLabel = new Label("Word:");
        GridPane.setConstraints(wordLabel, 0, 0);
        TextField wordInput = new TextField();
        GridPane.setConstraints(wordInput, 1, 0);

        // Meaning input field
        Label meaningLabel = new Label("Meaning:");
        GridPane.setConstraints(meaningLabel, 0, 1);
        TextField meaningInput = new TextField();
        GridPane.setConstraints(meaningInput, 1, 1);

        // Add Button
        Button addButton = new Button("Add Flashcard");
        GridPane.setConstraints(addButton, 1, 2);
        addButton.setOnAction(e -> {
            String term = wordInput.getText();
            String meaning = meaningInput.getText();
            if (!term.isEmpty() && !meaning.isEmpty()) {
                vocabularyManager.addWord(term, meaning); // 단어 추가 및 JSON 저장
                wordInput.clear();
                meaningInput.clear();
                showAlert(Alert.AlertType.INFORMATION, "Flashcard added successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Word and meaning cannot be empty!");
            }
        });

        // Delete Button
        Button deleteButton = new Button("Delete Flashcard");
        GridPane.setConstraints(deleteButton, 1, 3);
        deleteButton.setOnAction(e -> {
            String term = wordInput.getText();
            if (!term.isEmpty()) {
                vocabularyManager.removeWord(term); // 단어 삭제 및 JSON 저장
                wordInput.clear();
                showAlert(Alert.AlertType.INFORMATION, "Flashcard deleted successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Word cannot be empty!");
            }
        });

        // Flashcard display
        Label flashcardLabel = new Label("Press 'Next' to start learning!");
        GridPane.setConstraints(flashcardLabel, 0, 4, 2, 1);

        // Next Button
        Button nextButton = new Button("Next");
        GridPane.setConstraints(nextButton, 1, 5);
        nextButton.setOnAction(e -> {
            List<VocabularyManager.Word> words = vocabularyManager.getWordList();
            if (!words.isEmpty()) {
                VocabularyManager.Word word = words.get((int) (Math.random() * words.size())); // 랜덤 단어 선택
                flashcardLabel.setText("Word: " + word.getTerm() + "\nMeaning: " + word.getMeaning());
            } else {
                flashcardLabel.setText("No flashcards available!");
            }
        });

        // Search Button
        Button searchButton = new Button("Search Flashcard");
        GridPane.setConstraints(searchButton, 1, 6);
        searchButton.setOnAction(e -> {
            String term = wordInput.getText();
            String meaning = meaningInput.getText();

            // 검색 결과 필터링
            List<VocabularyManager.Word> results;
            if (!term.isEmpty()) {
                results = vocabularyManager.searchByTerm(term);
            } else if (!meaning.isEmpty()) {
                results = vocabularyManager.searchByMeaning(meaning);
            } else {
                showAlert(Alert.AlertType.ERROR, "Enter a word or meaning to search!");
                return;
            }

            if (!results.isEmpty()) {
                StringBuilder resultText = new StringBuilder("Search Results:\n");
                results.forEach(word -> resultText.append(word.getTerm()).append(" - ").append(word.getMeaning()).append("\n"));
                flashcardLabel.setText(resultText.toString());
            } else {
                flashcardLabel.setText("No matching flashcards found!");
            }
        });

        grid.getChildren().addAll(wordLabel, wordInput, meaningLabel, meaningInput, addButton, deleteButton, flashcardLabel, nextButton, searchButton);

        Scene scene = new Scene(grid, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Utility method to show alerts
    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
