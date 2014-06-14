package ru.hse.esadykov.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.hse.esadykov.model.Bug;
import ru.hse.esadykov.model.BugStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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
        Date closed = rs.getTimestamp("closed");
        int priority = rs.getInt("priority");
        String title = rs.getString("title");
        String description = rs.getString("description");
        Integer responsibleId = rs.getInt("responsible_id");
        Integer creatorId = rs.getInt("creator_id");
        BugStatus status = BugStatus.valueOf(rs.getString("status"));

        return new Bug(id, created, closed, priority, title, description, responsibleId, creatorId, status);
    }

    public List<Bug> getBugs() {
        // TODO: [FIXME] rewrite - http://stackoverflow.com/a/12268963/1970544
        return template.query("(select id, created, closed, priority, title, description, responsible_id, creator_id, status " +
                "from bug where status = 'NEW' order by priority desc)" +
                " union " +
                "(select id, created, closed, priority, title, description, responsible_id, creator_id, status " +
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
        return template.query("select id, created, closed, priority, title, description, responsible_id, creator_id, status " +
                        "from bug where id = :bugId",
                Collections.singletonMap("bugId", bugId),
                new ResultSetExtractor<Bug>() {
                    @Override
                    public Bug extractData(ResultSet rs) throws SQLException, DataAccessException {
                        if (!rs.next()) {
                            return null;
                        }
                        return extractBug(rs);
                    }
                });
    }

    public boolean addBug(Bug bug) throws SQLException {
        Map<String, Object> params = new HashMap<>();
        params.put("priority", bug.getPriority());
        params.put("title", bug.getTitle());
        params.put("description", bug.getDescription());
        params.put("responsibleId", bug.getResponsibleId());
        params.put("creatorId", bug.getCreatorId());

        return template.update("insert into bug (priority, title, description, responsible_id, creator_id) values " +
                "(:priority, :title, :description, :responsibleId, :creatorId)", params) > 0;
    }

    public boolean setStatus(int bugId, BugStatus status) {
        Map<String, Object> params = new HashMap<>();
        params.put("bugId", bugId);
        params.put("statusId", status.name());
        params.put("closedDate", new Date());

        return template.update("update bug set status = :statusId, closed = :closedDate where id = :bugId ", params) > 0;
    }
}
