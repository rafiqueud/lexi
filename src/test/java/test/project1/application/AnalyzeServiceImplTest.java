package test.project1.application;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import test.project1.ports.WordRepository;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class AnalyzeServiceImplTest {

    private AnalyzeServiceImpl analyzeService;

    @Mock
    private WordRepository wordRepository;

    private final List<String> words = new ArrayList<>();

    @BeforeEach
    public void setup() {
        Mockito.reset(wordRepository);
        words.clear();
        Mockito.when(wordRepository.getAllWords()).thenReturn(words);
        analyzeService = new AnalyzeServiceImpl(wordRepository);
    }

    @Test
    public void testFindClosestWord() throws ExecutionException, InterruptedException {
        mockSave();
        words.addAll(List.of("apple", "banana", "cherry", "date", "grape", "lemon"));
        analyzeService = new AnalyzeServiceImpl(wordRepository);
        // Test closest word with different total character values
        Assertions.assertEquals("cherry", analyzeService.analyze("berry").get().getValue());
        Assertions.assertEquals("cherry", analyzeService.analyze("apricot").get().getValue());

        // Test next apricot return
        Assertions.assertEquals("apricot", analyzeService.analyze("apricot").get().getValue());
        // Test closest word with equal total character values
        Assertions.assertEquals("lemon", analyzeService.analyze("lemon").get().getValue());


        // Test edge case with empty request word
        Assertions.assertEquals("date", analyzeService.analyze("").get().getValue());

        words.clear();
        analyzeService = new AnalyzeServiceImpl(wordRepository);
        Assertions.assertNull(analyzeService.analyze("apple").get().getValue());

        // Test edge case with empty word list and request word
        words.clear();
        analyzeService = new AnalyzeServiceImpl(wordRepository);
        words.clear();
        Assertions.assertNull(analyzeService.analyze("").get().getValue());
    }

    @Test
    public void testTieBreakWordsLexical() throws ExecutionException, InterruptedException {
        mockSave();
        words.addAll(List.of("apple", "banana", "anan", "cherry", "date", "grape", "lemon"));
        analyzeService = new AnalyzeServiceImpl(wordRepository);

        // If two or more candidates are equally close, choose the candidate that has the largest value.
        // If two or more largest value candidates are equally close, choose the word that sorts highest in ascending lexical order.
        Assertions.assertEquals("apple", analyzeService.analyze("apple").get().getValue());
        // nn = 28, anan = 30, date = 30,
        Assertions.assertEquals("date", analyzeService.analyze("nn").get().getValue());
        // banamb == 33, date = 30, anan = 30, banana = 33
        Assertions.assertEquals("banana", analyzeService.analyze("banamb").get().getValue());

        // pp == 32, date = 30, anan = 30
        Assertions.assertEquals("banana", analyzeService.analyze("pp").get().getValue());
    }

    @Test
    public void testCalculateTotalCharacterValue() throws ExecutionException, InterruptedException {
        // Test total character value calculation
        Assertions.assertEquals(50, AnalyzeServiceImpl.calculateTotalCharacterValue("apple"));
        Assertions.assertEquals(30, AnalyzeServiceImpl.calculateTotalCharacterValue("anan"));
        Assertions.assertEquals(33, AnalyzeServiceImpl.calculateTotalCharacterValue("banana"));
        Assertions.assertEquals(30, AnalyzeServiceImpl.calculateTotalCharacterValue("date"));
        Assertions.assertEquals(47, AnalyzeServiceImpl.calculateTotalCharacterValue("grape"));
        Assertions.assertEquals(59, AnalyzeServiceImpl.calculateTotalCharacterValue("lemon"));

        Assertions.assertEquals(77, AnalyzeServiceImpl.calculateTotalCharacterValue("cherry"));
        Assertions.assertEquals(82, AnalyzeServiceImpl.calculateTotalCharacterValue("apricot"));

        Assertions.assertEquals(28, AnalyzeServiceImpl.calculateTotalCharacterValue("nn"));
        Assertions.assertEquals(33, AnalyzeServiceImpl.calculateTotalCharacterValue("banamb"));
        Assertions.assertEquals(32, AnalyzeServiceImpl.calculateTotalCharacterValue("pp"));
    }


    @Test
    public void testFindClosestWord_DifferentLexicalDistances() throws ExecutionException, InterruptedException {
        mockSave();
        words.addAll(List.of("cherry", "apple", "banana", "date", "grape", "lemon"));
        analyzeService = new AnalyzeServiceImpl(wordRepository);

        // Test closest word with different lexical distances
        Assertions.assertEquals("apple", analyzeService.analyze("berry").get().getLexical());
        Assertions.assertEquals("banana", analyzeService.analyze("apricot").get().getLexical());
    }

    @Test
    public void testFindClosestWord_EqualLexicalDistances() throws ExecutionException, InterruptedException {
        mockSave();
        words.addAll(List.of("apple", "banana", "cherry", "date", "grape", "lemon"));
        analyzeService = new AnalyzeServiceImpl(wordRepository);

        // Test closest word with equal lexical distances
        Assertions.assertEquals("banana", analyzeService.analyze("april").get().getLexical());
        Assertions.assertEquals("banana", analyzeService.analyze("cherries").get().getLexical());
        Assertions.assertEquals("grape", analyzeService.analyze("grapes").get().getLexical());
    }

    @Test
    public void testFindClosestWord_TiebreakBasedOnLexicalOrder() throws ExecutionException, InterruptedException {
        mockSave();
        words.addAll(List.of("apple", "banana", "cherry", "date", "grape", "lemon"));
        analyzeService = new AnalyzeServiceImpl(wordRepository);

        // Test closest word tiebreak based on lexical order
        Assertions.assertEquals("banana", analyzeService.analyze("apricot").get().getLexical());
        Assertions.assertEquals("cherry", analyzeService.analyze("datef").get().getLexical());
    }

    @Test
    public void testFindClosestWord_oneHundredItens() throws ExecutionException, InterruptedException {

        for (int i = 0; i < 100000; i++) {
            words.add(UUID.randomUUID().toString());
            words.add("banana");
            words.add("date");
        }
        analyzeService = new AnalyzeServiceImpl(wordRepository);

        // Test closest word tiebreak based on lexical order
        Assertions.assertEquals("banana", analyzeService.analyze("banana").get().getLexical());
        Assertions.assertEquals("date", analyzeService.analyze("date").get().getLexical());
    }

    @Test
    public void testFindClosestWord_EmptyWordList() throws ExecutionException, InterruptedException {
        mockSave();
        analyzeService = new AnalyzeServiceImpl(wordRepository);

        // Test edge case with empty word list
        Assertions.assertNull(analyzeService.analyze("apple").get().getLexical());
    }

    @Test
    public void testFindClosestWord_EmptyRequestWord() throws ExecutionException, InterruptedException {
        mockSave();
        words.addAll(List.of("apple", "banana", "cherry", "date", "grape", "lemon"));
        analyzeService = new AnalyzeServiceImpl(wordRepository);
        // Test edge case with empty request word
        Assertions.assertEquals("date", analyzeService.analyze("").get().getLexical());
    }

    @Test
    public void testFindClosestWord_EmptyWordListAndRequestWord() throws ExecutionException, InterruptedException {
        mockSave();

        // Test edge case with empty word list and request word
        Assertions.assertNull(analyzeService.analyze("").get().getLexical());
    }

    private void mockSave() {
        Mockito.when(wordRepository.saveWord(any())).then(invocationOnMock -> {
            words.add(invocationOnMock.getArgument(0, String.class));
            final var future = new CompletableFuture<Void>();
            future.complete(null);
            return future;
        });
    }
}