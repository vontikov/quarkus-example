package com.github.vontikov.quarkus.reactive.example;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@QuarkusMain
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static class App implements QuarkusApplication {

        @Override
        public int run(String... args) throws Exception {
            LOGGER.info("Waiting...");
            Quarkus.waitForExit();
            return 0;
        }
    }

    public static void main(String... args) {
        Quarkus.run(App.class, (ret, e) -> {
            LOGGER.info("Exit: {}", ret);
            if (e != null) {
                LOGGER.error("Error", e);
            } 
            Quarkus.asyncExit(ret);
        }, args);
    }
}
