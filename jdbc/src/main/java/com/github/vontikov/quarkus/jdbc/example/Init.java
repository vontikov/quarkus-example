package com.github.vontikov.quarkus.jdbc.example;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.StartupEvent;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@Slf4j
public class Init {

    private final DataSourceBean dsb;
    private final boolean createSchema;

    public Init(DataSourceBean dsb,
            @ConfigProperty(name = "app.db.schema.create", defaultValue = "true")
            boolean createSchema) {
        this.dsb = dsb;
        this.createSchema = createSchema;
    }

    void onStart(@Observes StartupEvent event) {
        if (!createSchema) {
            return;
        }

        val executor = Executors.newSingleThreadExecutor();

        val fut = executor.submit(() -> {
            log.info("Populating database...");
            while (!Thread.interrupted()) {
                try (val conn = dsb.getConnection();
                        val stmt = conn.prepareStatement("DROP TABLE IF EXISTS fruits;"
                                + "CREATE TABLE fruits (id SERIAL PRIMARY KEY, name TEXT NOT NULL);"
                                + "INSERT INTO fruits (name) VALUES ('Kiwi');"
                                + "INSERT INTO fruits (name) VALUES ('Durian');"
                                + "INSERT INTO fruits (name) VALUES ('Pomelo');"
                                + "INSERT INTO fruits (name) VALUES ('Lychee');");) {

                    val res = stmt.execute();
                    log.info("Database is ready: {}", res);
                    return;
                } catch (SQLException ex) {
                    // NOOP
                }
            }
        });

        try {
            fut.get(10, TimeUnit.SECONDS);
        } catch (TimeoutException ex) {
            log.error("Initialization timeout");
            Quarkus.asyncExit(100);
            fut.cancel(true);
        } catch (InterruptedException | ExecutionException e) {
            // NOOP
        }

        executor.shutdownNow();
    }
}
