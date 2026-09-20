package io.github.floatingpointmc.banmanager.core.repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.floatingpointmc.banmanager.api.punishment.Punishment;
import io.github.floatingpointmc.banmanager.api.punishment.Type;
import io.github.floatingpointmc.banmanager.core.config.DatabaseConfig;
import io.github.floatingpointmc.banmanager.core.model.PunishmentRecord;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class HikariPunishmentRepository implements PunishmentRepository, AutoCloseable {
    private final HikariDataSource dataSource;

    private static final String CREATE_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS punishment (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "target_uuid VARCHAR(36) NOT NULL, " +
                    "executor_uuid VARCHAR(36), " +
                    "executing_time TIMESTAMP NOT NULL, " +
                    "expiry_time TIMESTAMP, " +
                    "overridden BOOLEAN NOT NULL DEFAULT FALSE, " +
                    "overridden_by_id INT, " +
                    "overriding BOOLEAN NOT NULL DEFAULT FALSE, " +
                    "overridden_id INT, " +
                    "withdrawn BOOLEAN NOT NULL DEFAULT FALSE, " +
                    "withdrawn_by_uuid VARCHAR(36), " +
                    "type TINYINT);";

    private static final String SELECT_BY_ID =
            "SELECT * FROM punishment WHERE id = ?;";

    private static final String SELECT_BY_TARGET =
            "SELECT * FROM punishment WHERE target_uuid = ?;";

    private static final String SELECT_ACTIVE_BY_TARGET =
            "SELECT * FROM punishment WHERE target_uuid = ? " +
                    "AND overridden = FALSE AND withdrawn = FALSE " +
                    "AND (expiry_time IS NULL OR expiry_time > CURRENT_TIMESTAMP);";

    private static final String INSERT_SQL =
            "INSERT INTO punishment (target_uuid, executor_uuid, executing_time, expiry_time, " +
                    "overridden, overridden_by_id, overriding, overridden_id, withdrawn, withdrawn_by_uuid, type) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private static final String UPDATE_SQL =
            "UPDATE punishment SET target_uuid = ?, executor_uuid = ?, executing_time = ?, expiry_time = ?, " +
                    "overridden = ?, overridden_by_id = ?, overriding = ?, overridden_id = ?, " +
                    "withdrawn = ?, withdrawn_by_uuid = ?, type = ? WHERE id = ?;";

    private static final String DELETE_SQL =
            "DELETE FROM punishment WHERE id = ?;";

    public HikariPunishmentRepository(@NotNull DatabaseConfig config) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(config.getDriver());
        hikariConfig.setJdbcUrl(config.getJdbcUrl());
        hikariConfig.setUsername(config.getUser());
        hikariConfig.setPassword(config.getPassword());
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setPoolName("BanManager-HikariPool");
        this.dataSource = new HikariDataSource(hikariConfig);
        initTable();
    }

    private void initTable() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(CREATE_TABLE_SQL);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize punishment table", e);
        }
    }

    @Override
    public @Nullable Punishment findById(int id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query punishment by id: " + id, e);
        }
        return null;
    }

    @Override
    public @NotNull Collection<Punishment> findByTarget(@NotNull UUID target) {
        Collection<Punishment> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_TARGET)) {
            ps.setString(1, target.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query punishments by target: " + target, e);
        }
        return result;
    }

    @Override
    public @NotNull Collection<Punishment> findActiveByTarget(@NotNull UUID target) {
        Collection<Punishment> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ACTIVE_BY_TARGET)) {
            ps.setString(1, target.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query active punishments by target: " + target, e);
        }
        return result;
    }

    @Override
    public void save(@NotNull Punishment punishment) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            setInsertParams(ps, punishment);
            ps.executeUpdate();
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next() && punishment instanceof PunishmentRecord) {
                    ((PunishmentRecord) punishment).setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save punishment", e);
        }
    }

    @Override
    public void update(@NotNull Punishment punishment) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
            setInsertParams(ps, punishment);
            ps.setInt(12, punishment.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update punishment: " + punishment.getId(), e);
        }
    }

    @Override
    public void delete(int id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete punishment: " + id, e);
        }
    }

    @Override
    public void close() {
        if (!dataSource.isClosed()) {
            dataSource.close();
        }
    }

    private @NotNull Punishment mapRow(@NotNull ResultSet rs) throws SQLException {
        String executorUuidStr = rs.getString("executor_uuid");
        Timestamp expiryTimestamp = rs.getTimestamp("expiry_time");
        String withdrawnByUuidStr = rs.getString("withdrawn_by_uuid");
        return new PunishmentRecord(
                rs.getInt("id"),
                UUID.fromString(rs.getString("target_uuid")),
                executorUuidStr != null ? UUID.fromString(executorUuidStr) : null,
                rs.getTimestamp("executing_time").toLocalDateTime(),
                expiryTimestamp != null ? expiryTimestamp.toLocalDateTime() : null,
                rs.getBoolean("overridden"),
                null,
                rs.getBoolean("overriding"),
                null,
                rs.getBoolean("withdrawn"),
                withdrawnByUuidStr != null ? UUID.fromString(withdrawnByUuidStr) : null,
                Type.values()[rs.getByte("type")]
        );
    }

    private void setInsertParams(@NotNull PreparedStatement ps, @NotNull Punishment p) throws SQLException {
        ps.setString(1, p.getTarget().toString());
        ps.setString(2, p.getExecutor() != null ? p.getExecutor().toString() : null);
        ps.setTimestamp(3, Timestamp.valueOf(p.getExecutingTime()));
        ps.setTimestamp(4, p.getExpiryTime() != null ? Timestamp.valueOf(p.getExpiryTime()) : null);
        ps.setBoolean(5, p.isOverridden());
        ps.setObject(6, p.getOverriddenBy() != null ? p.getOverriddenBy().getId() : null, Types.INTEGER);
        ps.setBoolean(7, p.isOverriding());
        ps.setObject(8, p.getOverriddenPunishment() != null ? p.getOverriddenPunishment().getId() : null, Types.INTEGER);
        ps.setBoolean(9, p.isWithdrawn());
        ps.setString(10, p.getWithdrawnBy() != null ? p.getWithdrawnBy().toString() : null);
        ps.setByte(11, (byte) p.getType().ordinal());
    }
}