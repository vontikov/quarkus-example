package com.github.vontikov.quarkus.jdbc.example;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@Slf4j
@RegisterForReflection(targets = { org.postgresql.Driver.class })
public class DataSourceBean {

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (Exception e) {
            log.error("PostgreSQL driver", e);
            Quarkus.asyncExit(100);
        }
    }

    @ConfigProperty(name = "datasource.url",
            defaultValue = "jdbc:postgresql://localhost:5432/postgres")
    String jdbcUrl;

    @ConfigProperty(name = "datasource.principal", defaultValue = "postgres")
    String jdbcPrincipal;

    @ConfigProperty(name = "datasource.credential", defaultValue = "postgres")
    String jdbcCredential;

    private final Set<Connection> connections = ConcurrentHashMap.newKeySet();
    private final Set<PreparedStatement> statements = ConcurrentHashMap.newKeySet();
    private final ThreadLocal<Connection> conn = new ThreadLocal<>();

    public void destroy(@Observes ShutdownEvent event) {
        log.info("Releasing prepared statements...");
        statements.forEach(c -> {
            try {
                c.close();
            } catch (SQLException ex) {
                // NOOP
            }
        });

        log.info("Releasing connections...");
        connections.forEach(c -> {
            try {
                c.close();
            } catch (SQLException ex) {
                // NOOP
            }
        });
    }

    public Connection getConnection() throws SQLException {
        var c = conn.get();
        if (c != null) {
            return c;
        }

        var props = new Properties();
        props.setProperty("user", jdbcPrincipal);
        props.setProperty("password", jdbcCredential);
        var nc = DriverManager.getConnection(jdbcUrl, props);

        connections.add(nc);
        conn.set(nc);
        return nc;
    }

    public void addStatement(PreparedStatement stmt) {
        statements.add(stmt);
    }
}
