package ukma.library.server.dao.impl;

import org.springframework.jdbc.core.RowMapper;

import ukma.library.server.dao.SearchDao;
import ukma.library.server.entity.*;
import ukma.library.server.service.LibraryService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcSearchDao implements SearchDao {

    private static Connection connection;
    private static ResultSet resultSet;
    private static PreparedStatement statement;

    private static final String QUEUE_TABLE_NAME = " acsm_b775c39c99325aa.queue ";
    private static final String COPY_TABLE_NAME = " acsm_b775c39c99325aa.instance ";
    private static final String USER_TABLE_NAME = " acsm_b775c39c99325aa.User ";
    private static final String USER_ROLE_TABLE_NAME = " acsm_b775c39c99325aa.User_Role ";

    public JdbcSearchDao() {
    }

    private Connection createConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(LibraryService.MYSQL_URL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean addQueue(Queue queue) {
        String sql = "INSERT INTO " + QUEUE_TABLE_NAME + "(id_book, id_user, date) " +
                "VALUES (?, ?, ?)";
        try {
            connection = createConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, queue.getBookId());
            statement.setInt(2, queue.getUserId());
            statement.setDate(3, new Date(queue.getDate().getTime()));
            statement .executeUpdate();
        } catch (SQLException e) {
            return false;
        } finally {
            closeConnection();
        }
        return true;
    }

    @Override
    public boolean deleteOueue(int id_book, int id_user) {
        String sql = "DELETE FROM " + QUEUE_TABLE_NAME + " WHERE Queue.id_book = " + id_book+" AND Queue.id_user = "+id_user;
        try {
            connection = createConnection();
            statement = connection.prepareStatement(sql);
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeConnection();
        }
        return true;
    }

    @Override
    public Copy getFreeCopy(int id) {
        String sql = "SELECT * FROM" + COPY_TABLE_NAME + " WHERE id_book = " + id;
        List<Copy> copies = selectCopyList(sql);
        return copies.isEmpty() ? null : copies.get(0);
    }

    @Override
    public List<Queue> getActiveQueue() {
        String sql = "SELECT * FROM" + QUEUE_TABLE_NAME;
        return selectQueueList(sql);
    }

    @Override
    public List<User> getQueueForBook(int book) {
        String sql = "SELECT * FROM" + QUEUE_TABLE_NAME + "INNER JOIN"+USER_TABLE_NAME+"on User.id_user=Queue.id_user INNER JOIN"+USER_ROLE_TABLE_NAME+"on User.user_role=User_Role.id_role WHERE Queue.id_book = " + book;
        return selectUserList(sql);
    }

    @Override
    public void addCopy(Copy c) {
        String sql = "INSERT INTO " + COPY_TABLE_NAME + "(id_book, status, ISBN) " +
                "VALUES (?, ?, ?)";
        try {
            connection = createConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, c.getBookId());
            statement.setInt(2, c.getStatus());
            statement.setString(3, String.valueOf(c.getIsbn()));
            statement .executeUpdate();
        } catch (SQLException e) {
            System.out.println("ERROR INSERT COPY : " + c.toString() + " - " + e);
        } finally {
            closeConnection();
        }
    }

    private List<Copy> selectCopyList(String sql) {
        ArrayList<Copy> copies = new ArrayList<Copy>();
        try {
            connection = createConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            int row = 0;
            while (resultSet.next()) {
                copies.add(copyMapper.mapRow(resultSet, row++));
            }

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e);
        } finally {
            closeConnection();
        }
        return copies;
    }

    private List<Queue> selectQueueList(String sql) {
        ArrayList<Queue> queues = new ArrayList<Queue>();
        try {
            connection = createConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            int row = 0;
            while (resultSet.next()) {
                queues.add(queueMapper.mapRow(resultSet, row++));
            }

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e);
        } finally {
            closeConnection();
        }
        return queues;
    }

    private static final RowMapper<Queue> queueMapper = new RowMapper<Queue>() {

        public Queue mapRow(ResultSet rs, int rowNum) throws SQLException {
            Queue queue = new Queue();
            queue.setId(rs.getInt("id_queue"));
            queue.setBookId(rs.getInt("id_book"));
            queue.setUserId(rs.getInt("id_user"));
            queue.setDate(rs.getTimestamp("date"));
            return queue;
        }
    };
    
    private List<User> selectUserList(String sql) {
        ArrayList<User> users = new ArrayList<User>();
        try {
            connection = createConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            int row = 0;
            while (resultSet.next()) {
                users.add(userMapper.mapRow(resultSet, row++));
            }

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e);
        } finally {
            closeConnection();
        }
        return users;
    }

    private static final RowMapper<Copy> copyMapper = new RowMapper<Copy>() {

        public Copy mapRow(ResultSet rs, int rowNum) throws SQLException {
            Copy copy = new Copy();
            copy.setId(rs.getInt("id_inst"));
            copy.setBookId(rs.getInt("id_book"));
            copy.setStatus(rs.getInt("status"));
            copy.setIsbn(Integer.valueOf(rs.getString("ISBN")));
            return copy;
        }
    };
    
    private static final RowMapper<User> userMapper = new RowMapper<User>() {

        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(resultSet.getInt("id_user"));
            user.setName(resultSet.getString("name"));
            user.setPhone(resultSet.getString("phone"));
            user.setPassword(resultSet.getString("password"));
            user.setRole(resultSet.getString("role"));
            return user;
        }
    };
    
}
