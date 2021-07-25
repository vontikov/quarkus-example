package com.github.vontikov.quarkus.jdbc.example;

import io.quarkus.runtime.ShutdownEvent;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class CleanUp {

    void onShutdown(@Observes ShutdownEvent event) {
        log.info("Releasing resources...");
        Fruit.cleanUp();
        DataSourceBean.cleanUp();
    }
}
