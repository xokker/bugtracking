package ru.hse.esadykov.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.hse.esadykov.ConnectionFactory;
import ru.hse.esadykov.model.Bug;
import ru.hse.esadykov.model.BugStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Ernest Sadykov
 * @since 31.05.2014
 */
@Repository("bugDao")
public class BugDao {

    @Autowired
    private NamedParameterJdbcTemplate template;
    
    private Bug extractBug(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        Date created = rs.getTimestamp("created");
        int priority = rs.getInt("priority");
        String title = rs.getString("title");
        String description = rs.getString("description");
        Integer responsibleId = rs.getInt("responsible_id");
        BugStatus status = BugStatus.valueOf(rs.getString("status"));

        return new Bug(id, created, priority, title, description, responsibleId, status);
    }

    public List<Bug> getBugs() {
        return template.query(
                "(select id, created, priority, title, description, responsible_id, status " +
                        "from bug where status = 'NEW' order by priority desc)" +
                        " union " +
                        "(select id, created, priority, title, description, responsible_id, status " +
                        "from bug where status <> 'NEW' order by priority desc)", new ResultSetExtractor<List<Bug>>() {
                    @Override
                    public List<Bug> extractData(ResultSet rs) throws SQLException, DataAccessException {
                        List<Bug> result = new ArrayList<>();
                        while (rs.next()) {
                            result.add(extractBug(rs));
                        }

                        return result;
                    }
                });
    }

    public Bug getBug(int bugId) throws SQLException {
        try (Connection con = ConnectionFactory.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "select id, created, priority, title, description, responsible_id, status " +
                            "from bug where id = ?");
            ps.setInt(1, bugId);
            ResultSet resultSet = ps.executeQuery();
            if (!resultSet.next()) {
                return null;
            }

            return extractBug(resultSet);
        }
    }

    public boolean addBug(Bug bug) throws SQLException {
        try (Connection con = ConnectionFactory.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "insert into bug (priority, title, description, responsible_id) values (?, ?, ?, ?)");
            ps.setInt(1, bug.getPriority());
            ps.setString(2, bug.getTitle());
            ps.setString(3, bug.getDescription());
            ps.setInt(4, bug.getResponsibleId());

            return ps.executeUpdate() > 0;
        }
    }
}
