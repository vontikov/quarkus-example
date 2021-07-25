package com.github.vontikov.quarkus.jdbc.example;

import io.quarkus.runtime.annotations.RegisterForReflection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@ApplicationScoped
@RegisterForReflection
@Slf4j
public class Fruit {

    private static Set<PreparedStatement> statements = ConcurrentHashMap.newKeySet();

    public static void cleanUp() {
        log.info("Releasing prepared statements...");
        statements.forEach(c -> {
            try {
                c.close();
            } catch (SQLException ex) {
                // NOOP
            }
        });
    }

    private Long id;
    private String name;

    private static ThreadLocal<PreparedStatement> findAllStmt = new ThreadLocal<>();
    private static ThreadLocal<PreparedStatement> findByIdStmt = new ThreadLocal<>();
    private static ThreadLocal<PreparedStatement> createStmt = new ThreadLocal<>();
    private static ThreadLocal<PreparedStatement> deleteStmt = new ThreadLocal<>();

    private static PreparedStatement prepare(Connection conn, String sql,
            ThreadLocal<PreparedStatement> tl) throws SQLException {
        val stmt = conn.prepareStatement(sql);
        statements.add(stmt);
        tl.set(stmt);
        return stmt;
    }

    public Fruit() {
    }

    public Fruit(String name) {
        this.name = name;
    }

    public Fruit(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static List<Fruit> findAll(Connection conn) throws SQLException {
        PreparedStatement stmt = findAllStmt.get();
        if (stmt == null) {
            stmt = prepare(conn, "SELECT id, name FROM fruits ORDER BY name ASC", findAllStmt);
        }

        val res = new ArrayList<Fruit>();
        try (val rs = stmt.executeQuery()) {
            while (rs.next()) {
                res.add(new Fruit(rs.getLong(1), rs.getString(2)));
            }
        }
        return res;
    }

    public static Optional<Fruit> findById(Connection conn, Long id) throws SQLException {
        PreparedStatement stmt = findByIdStmt.get();
        if (stmt == null) {
            stmt = prepare(conn, "SELECT id, name FROM fruits WHERE id = ?", findByIdStmt);
        }

        stmt.setLong(1, id);
        try (val rs = stmt.executeQuery()) {
            if (!rs.next()) {
                return Optional.empty();
            }
            return Optional.of(new Fruit(rs.getLong(1), rs.getString(2)));
        }
    }

    public Long create(Connection conn, Fruit fruit) throws SQLException {
        PreparedStatement stmt = createStmt.get();
        if (stmt == null) {
            stmt = prepare(conn, "INSERT INTO fruits (name) VALUES (?) RETURNING id", createStmt);
        }

        stmt.setString(1, fruit.getName());
        stmt.execute();
        try (val rs = stmt.getResultSet()) {
            rs.next();
            val res = rs.getLong(1);
            return res;
        }
    }

    public static boolean delete(Connection conn, Long id) throws SQLException {
        PreparedStatement stmt = deleteStmt.get();
        if (stmt == null) {
            stmt = prepare(conn, "DELETE FROM fruits WHERE id = ?", deleteStmt);
        }

        stmt.setLong(1, id);
        stmt.execute();
        return stmt.getUpdateCount() == 1;
    }
}