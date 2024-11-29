package group24;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.*;

public class FlashcardApp extends Application {

    private VocabularyManager vocabularyManager; // 단어 데이터 관리
    private Map<String, Integer> incorrectCount; // 오답 횟수 기록
    private int totalQuestions; // 전체 학습 질문 수
    private int correctAnswers; // 맞춘 정답 수
    private VocabularyManager.Word currentWord; // 현재 학습/복습 중인 단어

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        vocabularyManager = new VocabularyManager(); // 단어 데이터 관리 초기화
        incorrectCount = new HashMap<>(); // 오답 기록 초기화
        totalQuestions = 0;
        correctAnswers = 0;

        primaryStage.setTitle("Flashcard App");
        showMainMenu(primaryStage);
    }

    // 메인 메뉴 화면
    private void showMainMenu(Stage stage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(10);

        Button learningModeButton = new Button("학습 모드");
        GridPane.setConstraints(learningModeButton, 0, 0);
        learningModeButton.setOnAction(e -> startLearningMode(stage));

        Button reviewModeButton = new Button("복습 모드");
        GridPane.setConstraints(reviewModeButton, 1, 0);
        reviewModeButton.setOnAction(e -> startReviewMode(stage));

        Button addWordButton = new Button("단어 추가");
        GridPane.setConstraints(addWordButton, 0, 1);
        addWordButton.setOnAction(e -> showAddWordScreen(stage));

        grid.getChildren().addAll(learningModeButton, reviewModeButton, addWordButton);

        Scene scene = new Scene(grid, 400, 200);
        stage.setScene(scene);
        stage.show();
    }

    // 학습 모드 화면
    private void startLearningMode(Stage stage) {
        List<VocabularyManager.Word> wordList = new ArrayList<>(vocabularyManager.getWordList());
        Collections.shuffle(wordList);
        Iterator<VocabularyManager.Word> learningIterator = wordList.iterator();

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(10);

        Label wordLabel = new Label("단어: ");
        GridPane.setConstraints(wordLabel, 0, 0);

        Label displayedWord = new Label();
        GridPane.setConstraints(displayedWord, 1, 0);

        Label resultLabel = new Label();
        GridPane.setConstraints(resultLabel, 0, 2, 2, 1);

        TextField answerField = new TextField();
        GridPane.setConstraints(answerField, 0, 1, 2, 1);

        Button submitButton = new Button("제출");
        GridPane.setConstraints(submitButton, 0, 3);

        Button nextButton = new Button("다음");
        GridPane.setConstraints(nextButton, 1, 3);

        Button backButton = new Button("메인 메뉴로");
        GridPane.setConstraints(backButton, 0, 4);
        backButton.setOnAction(e -> showMainMenu(stage));

        if (learningIterator.hasNext()) {
            currentWord = learningIterator.next();
            displayedWord.setText(currentWord.getTerm());
        }

        submitButton.setOnAction(e -> {
            if (currentWord != null) {
                String answer = answerField.getText().trim();
                totalQuestions++;
                if (answer.equalsIgnoreCase(currentWord.getMeaning())) {
                    correctAnswers++;
                    resultLabel.setText("정답입니다!");
                } else {
                    resultLabel.setText("오답입니다! 정답: " + currentWord.getMeaning());
                    incorrectCount.put(currentWord.getTerm(), incorrectCount.getOrDefault(currentWord.getTerm(), 0) + 1);
                }
                answerField.clear();
            }
        });

        nextButton.setOnAction(e -> {
            if (learningIterator.hasNext()) {
                currentWord = learningIterator.next();
                displayedWord.setText(currentWord.getTerm());
                resultLabel.setText("");
            } else {
                resultLabel.setText("학습 모드 종료!");
                displayedWord.setText("");
                nextButton.setDisable(true); // 더 이상 단어가 없으면 버튼 비활성화
            }
        });

        grid.getChildren().addAll(wordLabel, displayedWord, answerField, submitButton, nextButton, backButton, resultLabel);

        Scene learningScene = new Scene(grid, 400, 300);
        stage.setScene(learningScene);
    }

// 복습 모드 시작
private void startReviewMode(Stage stage) {
    List<VocabularyManager.Word> wordList = new ArrayList<>(vocabularyManager.getWordList());

    // 오답 횟수 내림차순 정렬
    wordList.sort((w1, w2) -> Integer.compare(
        incorrectCount.getOrDefault(w2.getTerm(), 0),
        incorrectCount.getOrDefault(w1.getTerm(), 0)
    ));

    // 단어 순회 Iterator
    Iterator<VocabularyManager.Word> reviewIterator = wordList.iterator();

    GridPane reviewGrid = new GridPane();
    reviewGrid.setPadding(new Insets(10, 10, 10, 10));
    reviewGrid.setVgap(10);
    reviewGrid.setHgap(10);

    Label wordLabel = new Label("단어: ");
    GridPane.setConstraints(wordLabel, 0, 0);

    Label displayedWord = new Label(); // 단어 표시
    GridPane.setConstraints(displayedWord, 1, 0);

    Label resultLabel = new Label(); // 결과 메시지
    GridPane.setConstraints(resultLabel, 0, 2, 2, 1);

    TextField answerField = new TextField();
    GridPane.setConstraints(answerField, 0, 1, 2, 1);

    Button submitButton = new Button("제출");
    GridPane.setConstraints(submitButton, 0, 3);

    Button nextButton = new Button("다음");
    GridPane.setConstraints(nextButton, 1, 3);

    Button backButton = new Button("메인 메뉴로");
    GridPane.setConstraints(backButton, 0, 4, 2, 1);
    backButton.setOnAction(e -> {
        totalQuestions = 0; // 정답률 초기화
        correctAnswers = 0;
        showMainMenu(stage);
    });

    Label accuracyLabel = new Label(); // 정답률 표시용
    GridPane.setConstraints(accuracyLabel, 0, 5, 2, 1);

    // 다음 단어를 표시하는 메서드
    Runnable loadNextWord = () -> {
        if (reviewIterator.hasNext()) {
            currentWord = reviewIterator.next();
            displayedWord.setText(currentWord.getTerm()); // 단어 표시
            resultLabel.setText(""); // 결과 메시지 초기화
            answerField.clear(); // 입력 필드 초기화
        } else {
            resultLabel.setText("복습 모드 종료! 정답률: " + calculateAccuracy() + "%");
            displayedWord.setText("");
            accuracyLabel.setText("정답률: " + calculateAccuracy() + "%"); // 정답률 표시
            submitButton.setDisable(true); // 제출 버튼 비활성화
            nextButton.setDisable(true); // 다음 버튼 비활성화
            answerField.setDisable(true); // 답안 입력 필드 비활성화
        }
    };

    // 첫 단어 표시
    loadNextWord.run();

    // 제출 버튼 동작
    submitButton.setOnAction(e -> {
        if (currentWord != null) {
            String answer = answerField.getText().trim();
            totalQuestions++; // 전체 질문 수 증가
            if (answer.equalsIgnoreCase(currentWord.getMeaning())) {
                correctAnswers++; // 맞은 정답 수 증가
                resultLabel.setText("정답입니다!");
                incorrectCount.put(currentWord.getTerm(), Math.max(incorrectCount.getOrDefault(currentWord.getTerm(), 0) - 1, 0));
            } else {
                resultLabel.setText("오답입니다! 정답은: " + currentWord.getMeaning());
                incorrectCount.put(currentWord.getTerm(), incorrectCount.getOrDefault(currentWord.getTerm(), 0) + 1);
            }
        }
    });

    // 다음 버튼 동작
    nextButton.setOnAction(e -> loadNextWord.run());

    reviewGrid.getChildren().addAll(wordLabel, displayedWord, answerField, submitButton, nextButton, backButton, resultLabel, accuracyLabel);

    Scene reviewScene = new Scene(reviewGrid, 400, 300);
    stage.setScene(reviewScene);
}


    // 단어 추가 화면
    private void showAddWordScreen(Stage stage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(10);

        Label termLabel = new Label("단어:");
        GridPane.setConstraints(termLabel, 0, 0);
        TextField termField = new TextField();
        GridPane.setConstraints(termField, 1, 0);

        Label meaningLabel = new Label("뜻:");
        GridPane.setConstraints(meaningLabel, 0, 1);
        TextField meaningField = new TextField();
        GridPane.setConstraints(meaningField, 1, 1);

        Button addButton = new Button("추가");
        GridPane.setConstraints(addButton, 1, 2);
        addButton.setOnAction(e -> {
            String term = termField.getText().trim();
            String meaning = meaningField.getText().trim();
            if (!term.isEmpty() && !meaning.isEmpty()) {
                vocabularyManager.addWord(term, meaning);
                showAlert(Alert.AlertType.INFORMATION, "단어가 추가되었습니다.");
                termField.clear();
                meaningField.clear();
            } else {
                showAlert(Alert.AlertType.ERROR, "단어와 뜻을 모두 입력해주세요.");
            }
        });

        Button backButton = new Button("메인 메뉴로");
        GridPane.setConstraints(backButton, 0, 2);
        backButton.setOnAction(e -> showMainMenu(stage));

        grid.getChildren().addAll(termLabel, termField, meaningLabel, meaningField, addButton, backButton);

        Scene scene = new Scene(grid, 400, 200);
        stage.setScene(scene);
    }

    // 정답률 계산
    private int calculateAccuracy() {
        return totalQuestions > 0 ? (int) (((double) correctAnswers / totalQuestions) * 100) : 0;
    }

    // 알림 표시
    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
