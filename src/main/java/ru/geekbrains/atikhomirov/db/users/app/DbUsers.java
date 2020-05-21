package ru.geekbrains.atikhomirov.db.users.app;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DbUsers {
    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement psInsert;
    private static PreparedStatement psDelete;

    private static void prepareInsert() throws SQLException {
        psInsert = connection.prepareStatement("INSERT INTO users (name, age, email) VALUES (?, ?, ?);");
    }

    private static void prepareDelete() throws SQLException {
        psDelete = connection.prepareStatement("DELETE FROM users WHERE name = ?");
    }

    private static void closePreparedStatement(PreparedStatement ps) {
        try {
            if (ps != null) {
                ps.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String getAllFromUsers(String condition) {
        StringBuilder sb = new StringBuilder();
        if (connection != null) {
            try (ResultSet rs = statement.executeQuery(String.format("SELECT * FROM users %s;", condition))) {
                while (rs.next()) {
                    sb.append(rs.getInt("id")).append("\t");
                    sb.append(rs.getString("name")).append("\t");
                    sb.append(rs.getInt("age")).append("\t");
                    sb.append(rs.getString("email")).append("\n");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static String getAllUsers() {
        return getAllFromUsers("");
    }

    public static String getUsersByAge(int min, int max) {
        if (min >= max) {
            throw  new RuntimeException("Min value must be less then max value");
        }
        return getAllFromUsers(String.format("WHERE (age > %d) and (age < %d)", min, max));
    }

    public static void delUserByName(String name) {
        delUserByName(new ArrayList<>(Arrays.asList(name)));
    }

    public static void delUserByName(List<String> names) {
        if (names.isEmpty() || connection == null) {
            return;
        }
        try {
            prepareDelete();
            connection.setAutoCommit(false);
            for (String name:names) {
                psDelete.setString(1, name);
                psDelete.executeUpdate();
            }
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closePreparedStatement(psDelete);
        }
    }

    public static void add(String userData) {
        add(new ArrayList<>(Arrays.asList(userData)));
    }

    public static void add(List<String> usersData) {
        if (usersData.isEmpty() || connection == null) {
            return;
        }
        try {
            prepareInsert();
            connection.setAutoCommit(false);
            for (String userData:usersData) {
                String[] data = userData.split("\\s");
                psInsert.setString(1, data[0]);
                psInsert.setInt(2, Integer.parseInt(data[1]));
                psInsert.setString(3, data[2]);
                psInsert.executeUpdate();
            }
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closePreparedStatement(psInsert);
        }
    }

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:sqlite-db.db");
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to connect to db");
        }
    }

    public static void disconnect() {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
