package com.github.vontikov.quarkus.jdbc.example;

import io.quarkus.runtime.ApplicationLifecycleManager;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import lombok.extern.slf4j.Slf4j;

@QuarkusMain
@Slf4j
public class Main {

    public static class App implements QuarkusApplication {

        @Override
        public int run(String... args) throws Exception {
            Quarkus.waitForExit();
            return ApplicationLifecycleManager.getExitCode();
        }
    }

    public static void main(String... args) {
        Quarkus.run(App.class, (ret, e) -> {
            log.info("Exit: {}", ret);
            if (e != null) {
                log.error("Error", e);
            }
            Quarkus.asyncExit(ret);
        }, args);
    }
}
