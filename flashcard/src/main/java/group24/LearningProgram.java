package group24;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// 복습 모드 시작 (확률 기반 우선순위)
private void startReviewMode(Stage stage) {
    List<VocabularyManager.Word> wordList = new ArrayList<>(vocabularyManager.getWordList());

    // 확률 기반 정렬: 오답 횟수가 많을수록 우선순위가 높은 확률을 부여
    wordList.sort((w1, w2) -> Integer.compare(
        incorrectCount.getOrDefault(w2.getTerm(), 0),
        incorrectCount.getOrDefault(w1.getTerm(), 0)
    ));

    // 확률 기반 단어 선택
    Iterator<VocabularyManager.Word> reviewIterator = getShuffledWordIterator(wordList);

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

// 단어 리스트를 확률 기반으로 섞는 Iterator 생성
private Iterator<VocabularyManager.Word> getShuffledWordIterator(List<VocabularyManager.Word> wordList) {
    List<VocabularyManager.Word> weightedWords = new ArrayList<>();

    for (VocabularyManager.Word word : wordList) {
        int weight = incorrectCount.getOrDefault(word.getTerm(), 1);
        for (int i = 0; i < weight; i++) {
            weightedWords.add(word);
        }
    }

    Collections.shuffle(weightedWords);
    return weightedWords.iterator();
}
