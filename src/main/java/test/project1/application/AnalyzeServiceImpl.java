package test.project1.application;

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import test.project1.ports.WordRepository;

public class AnalyzeServiceImpl implements AnalyzeService {

    private final WordRepository wordRepository;

    private Set<String> words = new TreeSet<>();

    public AnalyzeServiceImpl(final WordRepository wordRepository) {
        this.wordRepository = wordRepository;
        final var allWords = wordRepository.getAllWords();
        words.addAll(allWords);
    }

    @Override
    public CompletableFuture<Result> analyze(final String text) {
        final var lowercaseText = text.toLowerCase();
        final var resultCompletableFuture = new CompletableFuture<Result>();

        if (!words.contains(lowercaseText)) {
            wordRepository.saveWord(lowercaseText)
                    .thenApplyAsync((Void) -> analyzeWord(lowercaseText))
                    .thenApplyAsync(resultCompletableFuture::completeAsync);
            return resultCompletableFuture;
        }

        return resultCompletableFuture
                .completeAsync(analyzeWord(lowercaseText));
    }

    private Supplier<Result> analyzeWord(final String request) {
        return () -> {
            final var result = compareText(request);
            words.add(request);
            return result;
        };
    }

    public Result compareText(final String requestWord) {
        int requestValue = calculateTotalCharacterValue(requestWord);

        // value impl
        String closestWord = null;
        int closestDifference = Integer.MAX_VALUE;
        int largestValue = Integer.MIN_VALUE;


        // lexical impl
        String closestLexicalWord = null;
        int closestLexicalDistance = Integer.MAX_VALUE;

        for (final String word : words) {
            if (closestWord == null) {
                closestWord = word;
            }
            if (closestLexicalWord == null) {
                closestLexicalWord = word;
            }

            // calc process
            int wordValue = calculateTotalCharacterValue(word);
            int difference = Math.abs(requestValue - wordValue);
            if (difference < closestDifference // closest
                    || (difference == closestDifference && wordValue > largestValue) // tieBreak largest value
                    || (difference == closestDifference && wordValue == largestValue && word.compareTo(closestWord) >= 0) // tieBreak lexical sort desc
            ) {
                closestWord = word;
                closestDifference = difference;
                largestValue = wordValue;
            }

            // lexical process
            int lexicalDistance = calculateLexicalDistance(word, requestWord);
            if (lexicalDistance < closestLexicalDistance // closest
                    || (lexicalDistance == closestLexicalDistance && word.compareTo(closestLexicalWord) < 0) // tieBreak lexical sort asc
            ) {
                closestLexicalWord = word;
                closestLexicalDistance = lexicalDistance;
            }
        }

        return new Result(closestWord, closestLexicalWord);
    }

    public static int calculateLexicalDistance(String word1, String word2) {
        return Math.abs(word1.compareTo(word2));
    }

    //  it assumes that the character 'a' represents the value 1, 'b' represents 2, 'c' represents 3, and so on.
    //  To achieve this mapping, 'a' is subtracted by 1 so that 'a' - 'a' + 1 equals 1, 'b' - 'a' + 1 equals 2, and so on.
    public static int calculateTotalCharacterValue(String word) {
        int value = 0;

        for (char c : word.toLowerCase().toCharArray()) {
            value += c - 'a' + 1;
        }

        return value;
    }

}