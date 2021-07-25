package com.github.vontikov.quarkus.reactive.example;

import io.quarkus.runtime.StartupEvent;
import io.vertx.mutiny.pgclient.PgPool;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class InitDb {

    private final PgPool client;
    private final boolean createSchema;

    public InitDb(PgPool client,
            @ConfigProperty(name = "app.db.schema.create",
            defaultValue = "true") boolean createSchema) {
        this.client = client;
        this.createSchema = createSchema;
    }

    void onStart(@Observes StartupEvent event) {
        if (!createSchema) {
            return;
        }
        client.query("DROP TABLE IF EXISTS fruits").execute()
                .flatMap(r -> client
                        .query("CREATE TABLE fruits (id SERIAL PRIMARY KEY, name TEXT NOT NULL)")
                        .execute())
                .flatMap(r -> client.query("INSERT INTO fruits (name) VALUES ('Kiwi')").execute())
                .flatMap(r -> client.query("INSERT INTO fruits (name) VALUES ('Durian')").execute())
                .flatMap(r -> client.query("INSERT INTO fruits (name) VALUES ('Pomelo')").execute())
                .flatMap(r -> client.query("INSERT INTO fruits (name) VALUES ('Lychee')").execute())
                .await().indefinitely();
    }
}
