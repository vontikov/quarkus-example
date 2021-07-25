package com.github.vontikov.quarkus.jdbc.example;

import io.agroal.api.AgroalDataSource;
import io.agroal.api.configuration.supplier.AgroalPropertiesReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@Slf4j
public class DataSourceBean {

    private static Set<Connection> connections = ConcurrentHashMap.newKeySet();

    public static void cleanUp() {
        log.info("Releasing connections...");
        connections.forEach(c -> {
            try {
                c.close();
            } catch (SQLException ex) {
                // NOOP
            }
        });
    }

    @ConfigProperty(name = "datasource.url",
            defaultValue = "jdbc:postgresql://localhost:5432/postgres")
    String jdbcUrl;

    @ConfigProperty(name = "datasource.principal", defaultValue = "postgres")
    String jdbcPrincipal;

    @ConfigProperty(name = "datasource.credential", defaultValue = "postgres")
    String jdbcCredential;

    private ThreadLocal<Connection> conn = new ThreadLocal<>();

    private DataSource dataSource;

    @PostConstruct
    void init() throws SQLException {
        val props = new HashMap<String, String>();

        props.put(AgroalPropertiesReader.JDBC_URL, jdbcUrl);
        props.put(AgroalPropertiesReader.PRINCIPAL, jdbcPrincipal);
        props.put(AgroalPropertiesReader.CREDENTIAL, jdbcCredential);
        props.put(AgroalPropertiesReader.MAX_SIZE, "10");
        props.put(AgroalPropertiesReader.MIN_SIZE, "10");
        props.put(AgroalPropertiesReader.INITIAL_SIZE, "10");
        props.put(AgroalPropertiesReader.MAX_LIFETIME_S, "300");
        props.put(AgroalPropertiesReader.ACQUISITION_TIMEOUT_S, "30");

        dataSource = AgroalDataSource
                .from(new AgroalPropertiesReader().readProperties(props).get());
    }

    public Connection getConnection() throws SQLException {
        val c = conn.get();
        if (c != null) {
            return c;
        }

        val nc = dataSource.getConnection();
        connections.add(nc);
        conn.set(nc);
        return nc;
    }
}
