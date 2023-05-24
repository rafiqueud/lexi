package test.project1.adapters.outbound;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import test.project1.ports.WordRepository;

public class FileWordRepositoryImpl implements WordRepository {

    private static final String FILE_PATH = "word_file.txt";

    @Override
    public CompletableFuture<Void> saveWord(final String word) {
        return CompletableFuture.runAsync(() -> {
            try {
                Files.writeString(Paths.get(FILE_PATH), word + System.lineSeparator(),
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save word to file.", e);
            }
        });
    }

    public static void createFileIfNotExists(String filePath) throws IOException {

        final var path = Path.of(filePath);

        if (!Files.exists(path)) {
            Files.createFile(path);
        }
    }

    @Override
    public List<String> getAllWords() {
        try {
            createFileIfNotExists(FILE_PATH);
            return Files.readAllLines(Paths.get(FILE_PATH), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read words from file.", e);
        }
    }
}
