package test.project1.application;

import java.util.concurrent.CompletableFuture;

public interface AnalyzeService {

    CompletableFuture<Result> analyze(String text);

}
