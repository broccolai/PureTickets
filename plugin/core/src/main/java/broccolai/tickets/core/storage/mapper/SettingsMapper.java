package broccolai.tickets.core.storage.mapper;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class SettingsMapper implements RowMapper<SettingsMapper> {

    @Override
    public SettingsMapper map(final ResultSet rs, final StatementContext ctx) throws SQLException {
//        boolean announcements = rs.getBoolean("announcements");
//        return new UserSettings(announcements);
        return null;
    }

}
