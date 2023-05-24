package test.project1.adapters.inbound;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import test.project1.application.AnalyzeService;

public class AnalyzeController implements Handler<RoutingContext> {

    private final AnalyzeService analyzeService;

    public AnalyzeController(final AnalyzeService analyzeService) {
        this.analyzeService = analyzeService;
    }

    @Override
    public void handle(final RoutingContext context) {
        final var text = context.body()
                .asJsonObject()
                .getString("text");

        final var response = context.response();

        analyzeService.analyze(text)
                .whenComplete((result, throwable) -> {
                    response.putHeader("content-type", "text/application/json");
                    response.end(result.toJson().toBuffer());
                });

    }
}
