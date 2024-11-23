import java.util.*;

public class LearningProgram {

    private VocabularyManager manager; // VocabularyManager 인스턴스
    private Map<String, Integer> incorrectCount; // 단어별 오답 횟수 기록

    public LearningProgram() {
        manager = new VocabularyManager(); // VocabularyManager 초기화
        incorrectCount = new HashMap<>(); // 오답 기록 초기화
    }

    public static void main(String[] args) {
        LearningProgram program = new LearningProgram();
        Scanner scanner = new Scanner(System.in);

        System.out.println("1: 학습 모드 | 2: 복습 모드");
        int mode = scanner.nextInt();
        scanner.nextLine(); // 버퍼 정리

        if (mode == 1) {
            program.startLearningMode(scanner);
        } else if (mode == 2) {
            program.startReviewMode(scanner);
        } else {
            System.out.println("잘못된 입력입니다.");
        }

        scanner.close();
    }

    // 학습 모드
    private void startLearningMode(Scanner scanner) {
        List<VocabularyManager.Word> wordList = new ArrayList<>(manager.getWordList());
        Collections.shuffle(wordList); // 단어 리스트를 섞음

        System.out.println("학습 모드 시작!");
        for (VocabularyManager.Word word : wordList) {
            System.out.println("단어: " + word.getTerm());
            System.out.print("뜻 입력: ");
            String answer = scanner.nextLine();

            if (answer.equalsIgnoreCase(word.getMeaning())) {
                System.out.println("정답입니다!");
            } else {
                System.out.println("오답입니다! 정답: " + word.getMeaning());
                // 오답 횟수 증가
                incorrectCount.put(word.getTerm(), incorrectCount.getOrDefault(word.getTerm(), 0) + 1);
            }
        }
        System.out.println("학습 모드 종료!");
    }

    // 복습 모드 : 틀린 횟수가 많은 단어를 내림차순으로 학습
    private void startReviewMode(Scanner scanner) {
        List<VocabularyManager.Word> wordList = new ArrayList<>(manager.getWordList());

        // 오답률에 따라 오답횟수가 클 수록 단어를 리스트 앞에 배치
        wordList.sort((w1, w2) -> Integer.compare(
            incorrectCount.getOrDefault(w2.getTerm(), 0),
            incorrectCount.getOrDefault(w1.getTerm(), 0)
        ));

        System.out.println("복습 모드 시작!");
        for (VocabularyManager.Word word : wordList) {
            if (incorrectCount.getOrDefault(word.getTerm(), 0) > 0) { // 오답 기록이 있는 단어만 복습
                System.out.println("단어: " + word.getTerm());
                System.out.print("뜻 입력: ");
                String answer = scanner.nextLine();

                if (answer.equalsIgnoreCase(word.getMeaning())) {
                    System.out.println("정답입니다!");
                    incorrectCount.put(word.getTerm(), incorrectCount.get(word.getTerm()) - 1);
                } else {
                    System.out.println("오답입니다! 정답: " + word.getMeaning());
                    incorrectCount.put(word.getTerm(), incorrectCount.getOrDefault(word.getTerm(), 0) + 1);
                }
            }
        }
        System.out.println("복습 모드 종료!");
    }
}
