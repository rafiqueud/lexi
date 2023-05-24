package test.project1.ports;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface WordRepository {
    CompletableFuture<Void> saveWord(String word);

    List<String> getAllWords();
}
