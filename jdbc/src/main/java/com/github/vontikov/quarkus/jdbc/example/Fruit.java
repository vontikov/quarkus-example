package com.github.vontikov.quarkus.jdbc.example;

import io.quarkus.runtime.annotations.RegisterForReflection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
@RegisterForReflection
public class Fruit {

    @Inject
    DataSourceBean dsb;

    private Long id;
    private String name;

    private ThreadLocal<PreparedStatement> findAllStmt = new ThreadLocal<>();
    private ThreadLocal<PreparedStatement> findByIdStmt = new ThreadLocal<>();
    private ThreadLocal<PreparedStatement> createStmt = new ThreadLocal<>();
    private ThreadLocal<PreparedStatement> deleteStmt = new ThreadLocal<>();

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

    public List<Fruit> findAll() throws SQLException {
        var stmt = findAllStmt.get();
        if (stmt == null) {
            stmt = prepare("SELECT id, name FROM fruits ORDER BY name ASC", findAllStmt);
        }

        var res = new ArrayList<Fruit>();
        try (var rs = stmt.executeQuery()) {
            while (rs.next()) {
                res.add(new Fruit(rs.getLong(1), rs.getString(2)));
            }
        }
        return res;
    }

    public Optional<Fruit> findById(Long id) throws SQLException {
        var stmt = findByIdStmt.get();
        if (stmt == null) {
            stmt = prepare("SELECT id, name FROM fruits WHERE id = ?", findByIdStmt);
        }

        stmt.setLong(1, id);
        try (var rs = stmt.executeQuery()) {
            if (!rs.next()) {
                return Optional.empty();
            }
            return Optional.of(new Fruit(rs.getLong(1), rs.getString(2)));
        }
    }

    public Long create(Fruit fruit) throws SQLException {
        var stmt = createStmt.get();
        if (stmt == null) {
            stmt = prepare("INSERT INTO fruits (name) VALUES (?) RETURNING id", createStmt);
        }

        stmt.setString(1, fruit.getName());
        stmt.execute();
        try (var rs = stmt.getResultSet()) {
            rs.next();
            var res = rs.getLong(1);
            return res;
        }
    }

    public boolean delete(Long id) throws SQLException {
        var stmt = deleteStmt.get();
        if (stmt == null) {
            stmt = prepare("DELETE FROM fruits WHERE id = ?", deleteStmt);
        }

        stmt.setLong(1, id);
        stmt.execute();
        return stmt.getUpdateCount() == 1;
    }

    private PreparedStatement prepare(String sql, ThreadLocal<PreparedStatement> tl)
            throws SQLException {
        var stmt = dsb.getConnection().prepareStatement(sql);
        dsb.addStatement(stmt);
        tl.set(stmt);
        return stmt;
    }

}