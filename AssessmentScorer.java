import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Scores supported multiple-choice assessments without trusting UI totals. */
final class AssessmentScorer {
    private AssessmentScorer() { }

    record Result(int attempted, int correct, int incorrect, int unanswered,
                  int marksObtained, int totalMarks, double percentage, double accuracy,
                  Map<String, int[]> topicResults, Map<String, int[]> levelResults) { }

    static Result calculate(List<AssessmentQuestionBank.Question> questions, int[] answers) {
        if (questions == null) throw new IllegalArgumentException("Question list is required.");
        int attempted = 0;
        int correct = 0;
        int marksObtained = 0;
        int totalMarks = 0;
        Map<String, int[]> topics = new LinkedHashMap<>();
        Map<String, int[]> levels = new LinkedHashMap<>();

        for (int i = 0; i < questions.size(); i++) {
            AssessmentQuestionBank.Question question = questions.get(i);
            if (question.marks() < 0 || question.correctOption() < 0
                    || question.correctOption() >= question.options().size()) {
                throw new IllegalArgumentException("Question has invalid scoring data.");
            }
            totalMarks += question.marks();
            int answer = answers != null && i < answers.length ? answers[i] : -1;
            if (answer >= question.options().size()) {
                throw new IllegalArgumentException("Answer choice is out of range.");
            }
            boolean isAttempted = answer >= 0;
            boolean isCorrect = isAttempted && answer == question.correctOption();
            if (isAttempted) attempted++;
            if (isCorrect) {
                correct++;
                marksObtained += question.marks();
            }
            addTopicScore(topics, question.topic(), isCorrect);
            addTopicScore(levels, question.difficulty(), isCorrect);
        }

        int incorrect = attempted - correct;
        int unanswered = questions.size() - attempted;
        double percentage = totalMarks == 0 ? 0 : marksObtained * 100.0 / totalMarks;
        double accuracy = attempted == 0 ? 0 : correct * 100.0 / attempted;
        return new Result(attempted, correct, incorrect, unanswered, marksObtained,
                totalMarks, percentage, accuracy, topics, levels);
    }

    private static void addTopicScore(Map<String, int[]> scores, String key, boolean correct) {
        int[] counts = scores.computeIfAbsent(key, ignored -> new int[2]);
        counts[1]++;
        if (correct) counts[0]++;
    }
}
