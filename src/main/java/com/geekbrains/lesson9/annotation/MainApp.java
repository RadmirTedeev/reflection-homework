package com.geekbrains.lesson9.annotation;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainApp {
    private static final String url = "jdbc:mysql://localhost:3306/test_anno";
    private static final String username = "root";
    private static final String password = "root";
    private static final String driver = "com.mysql.jdbc.Driver";
    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement preparedStatement;

    public static void main(String[] args) {
        Student student1 = new Student("Jack", 20, 250);
        Student student2 = new Student("James", 17, 120);
        NewStudent newStudent1 = new NewStudent("Mary", "Johnson", 19, 140);
        NewStudent newStudent2 = new NewStudent("Vin", "Diesel", 27, 120);
        NewStudent newStudent3 = new NewStudent("Steven", "Smith", 22, 190);
        StudentWithoutAnnotations studentsWithoutAnnotations = new StudentWithoutAnnotations("Jack", 22, 150);

        try {
            connect();

            insertTableByAnnotation(newStudent3);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    public static void insertTableByAnnotation(Object obj) throws SQLException {
        Class<?> theClass = obj.getClass();

        if (theClass.isAnnotationPresent(Table.class)) {
            String title = theClass.getAnnotation(Table.class).title();
            Field[] fields = theClass.getDeclaredFields();
            List<Object> fieldList = new ArrayList<>();
            List<String> columnsList = new ArrayList<>();

            for (Field field : fields) {
                if (field.isAnnotationPresent(Column.class)) {
                    String column = field.getAnnotation(Column.class).name();
                    Object fieldValue = null;

                    try {
                        field.setAccessible(true);
                        fieldValue = field.get(obj);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                    columnsList.add(column);
                    fieldList.add(fieldValue);
                }
            }

            if (!fieldList.isEmpty()) {
                StringBuilder sql = new StringBuilder();
                sql.append("INSERT INTO " + title + " (");

                for (String column : columnsList) {
                    sql.append(column + ", ");
                }

                sql.setLength(sql.length() - 2);
                sql.append(") VALUES (");

                for (int i = 0; i < columnsList.size(); i++) {
                    sql.append("?, ");
                }

                sql.setLength(sql.length() - 2);
                sql.append(")");

                try {
                    preparedStatement = connection.prepareStatement(sql.toString());

                    for (int i = 0; i < columnsList.size(); i++) {
                        preparedStatement.setObject(i + 1, fieldList.get(i));
                    }

                    preparedStatement.executeUpdate();
                    System.out.println(preparedStatement);
                } finally {
                    preparedStatement.close();
                }

                System.out.println("===> New record in table \"" + title + "\" has been successfully added");
            } else {
                System.out.println("This class is not annotated 'Column'");
            }
        } else {
            System.out.println("This class is not annotated 'Table'");
        }
    }

    public static void createTableByAnnotation(Object obj) throws SQLException {
        Class<?> theClass = obj.getClass();

        if (theClass.isAnnotationPresent(Table.class)) {
            String title = theClass.getAnnotation(Table.class).title();
            Field[] fields = theClass.getDeclaredFields();
            Map<String, String> columns = new LinkedHashMap<>();

            for (Field field : fields) {
                if (field.isAnnotationPresent(Column.class)) {
                    String columnName = field.getAnnotation(Column.class).name();
                    Class<?> type = field.getType();
                    String fieldType = type.toString();

                    if (fieldType.equals("class java.lang.String")) {
                        fieldType = "VARCHAR(45)";
                    }

                    columns.put(columnName, fieldType);
                }
            }

            if (!columns.isEmpty()) {
                StringBuilder sql = new StringBuilder();
                sql.append("CREATE TABLE " + title + " (id int NOT NULL PRIMARY KEY AUTO_INCREMENT, ");

                for (Map.Entry<String, String> entry : columns.entrySet()) {
                    sql.append(entry.getKey() + " " + entry.getValue() + " NOT NULL, ");
                }

                sql.setLength(sql.length() - 2);
                sql.append(");");

                System.out.println("===> " + sql);
                statement.executeUpdate(sql.toString());
                System.out.println("===> " + title + " table has been successfully created");
            } else {
                System.out.println("This class is not annotated 'Column'");
            }

        } else {
            System.out.println("This class is not annotated 'Table'");
        }
    }

    public static void createTable(String table, String field1, String field2) throws SQLException {
        String sql = "CREATE TABLE " + table + " (id int NOT NULL PRIMARY KEY AUTO_INCREMENT, " + field1 + " VARCHAR(45) NOT NULL, " + field2 + " int NOT NULL);";
        statement.executeUpdate(sql);
    }

    public static void insertTable(String table, String name, int score) throws SQLException {
        String sql = "INSERT INTO " + table + " (name, score) VALUES (?, ?)";

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, score);
            preparedStatement.executeUpdate();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }

    public static void selectRowsFromTable(String table) throws SQLException {
        String sql = "SELECT * FROM " + table;
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            System.out.println("id: " + rs.getInt(1) + ", name: " + rs.getString(2) + ", score: " + rs.getInt(3));
        }
    }

    public static void connect() throws SQLException {
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            throw new SQLException("Unable to connect");
        }
    }

    public static void disconnect() {
        try {
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
