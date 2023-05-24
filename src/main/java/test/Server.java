package test;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import test.project1.adapters.inbound.AnalyzeController;
import test.project1.adapters.outbound.FileWordRepositoryImpl;
import test.project1.application.AnalyzeServiceImpl;

public class Server extends AbstractVerticle {
    private Router router;
    private HttpServer server;

    @Override
    public void start(Promise<Void> start) throws Exception {
        router = Router.router(vertx);

        final var wordRepository = new FileWordRepositoryImpl();
        final var analyzeService = new AnalyzeServiceImpl(wordRepository);

        router.route().handler(BodyHandler.create());

        router.post("/analyze").handler(new AnalyzeController(analyzeService));
        vertx.createHttpServer().requestHandler(router)
                .listen(config().getInteger("http.port", 8080))
                .onSuccess(server -> {
                    this.server = server;
                    start.complete();
                })
                .onFailure(start::fail);
    }
}

