package test.project1;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import java.util.function.Consumer;

public class Runner {

    public static void runExample(final Class clazz) {
        final Consumer<Vertx> runner = vertx -> vertx.deployVerticle(clazz.getName());
        final var vertx = Vertx.vertx(new VertxOptions());
        runner.accept(vertx);
    }

}