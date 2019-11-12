package io.solar.utils.db;

import java.io.Serializable;
import java.sql.*;
import java.util.*;

public class Query {
    /**
     * The statement this object is wrapping.
     */
    private final PreparedStatement statement;

    /**
     * Maps parameter names to arrays of ints which are the parameter indices.
     */
    private final Map<String, List<Integer>> indexMap;


    /**
     * Creates a NamedParameterStatement.  Wraps a call to
     * c.{@link Connection#prepareStatement(java.lang.String)
     * prepareStatement}.
     *
     * @param connection the database connection
     * @param query      the parameterized query
     * @throws RuntimeException if the statement could not be created
     */
    public Query(Connection connection, String query) {
        indexMap = new HashMap<>();
        String parsedQuery = parse(query);
        try {
            statement = connection.prepareStatement(parsedQuery, Statement.RETURN_GENERATED_KEYS);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("invalid query syntax - " + query);
        }
    }


     private String parse(String query) {
        // I was originally using regular expressions, but they didn't work well
        // for ignoring parameter-like strings inside quotes.
        int length = query.length();
        StringBuilder parsedQuery = new StringBuilder(length);
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        int index = 1;

        for (int i = 0; i < length; i++) {
            char c = query.charAt(i);
            if (inSingleQuote) {
                if (c == '\'') {
                    inSingleQuote = false;
                }
            } else if (inDoubleQuote) {
                if (c == '"') {
                    inDoubleQuote = false;
                }
            } else {
                if (c == '\'') {
                    inSingleQuote = true;
                } else if (c == '"') {
                    inDoubleQuote = true;
                } else if (c == ':' && i + 1 < length &&
                        Character.isJavaIdentifierStart(query.charAt(i + 1))) {
                    int j = i + 2;
                    while (j < length && Character.isJavaIdentifierPart(query.charAt(j))) {
                        j++;
                    }
                    String name = query.substring(i + 1, j);
                    c = '?'; // replace the parameter with a question mark
                    i += name.length(); // skip past the end if the parameter

                    List<Integer> indexList = indexMap.computeIfAbsent(name, k -> new ArrayList<>());
                    indexList.add(index);

                    index++;
                }
            }
            parsedQuery.append(c);
        }

        return parsedQuery.toString();
    }


    /**
     * Returns the indexes for a parameter.
     *
     * @param name parameter name
     * @return parameter indexes
     * @throws IllegalArgumentException if the parameter does not exist
     */
    private List<Integer> getIndexes(String name) {
        List<Integer> indexes = indexMap.get(name);
        if (indexes == null) {
            throw new IllegalArgumentException("Parameter not found: " + name);
        }
        return indexes;
    }


    /**
     * Sets a parameter.
     *
     * @param name  parameter name
     * @param value parameter value
     * @throws SQLException             if an error occurred
     * @throws IllegalArgumentException if the parameter does not exist
     * @see PreparedStatement#setObject(int, java.lang.Object)
     */
    public void setObject(String name, Object value) throws SQLException {
        List<Integer> indexes = getIndexes(name);
        for (Integer i : indexes) {
            statement.setObject(i, value);
        }
    }


    /**
     * Sets a parameter.
     *
     * @param name  parameter name
     * @param value parameter value
     * @throws IllegalArgumentException if the parameter does not exist
     * @see PreparedStatement#setString(int, java.lang.String)
     */
    public void setString(String name, String value) {
        List<Integer> indexes = getIndexes(name);
        for (Integer i : indexes) {
            try {
                statement.setString(i, value);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


    /**
     * Sets a parameter.
     *
     * @param name  parameter name
     * @param value parameter value
     * @throws IllegalArgumentException if the parameter does not exist
     * @see PreparedStatement#setInt(int, int)
     */
    public void setInt(String name, Integer value) {
        List<Integer> indexes = getIndexes(name);
        for (Integer i : indexes) {
            try {
                if(value == null) {
                    statement.setNull(i, Types.INTEGER);
                } else {
                    statement.setInt(i, value);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


    /**
     * Sets a parameter.
     *
     * @param name  parameter name
     * @param value parameter value
     * @throws IllegalArgumentException if the parameter does not exist
     * @see PreparedStatement#setInt(int, int)
     */
    public void setLong(String name, Long value) {
        List<Integer> indexes = getIndexes(name);
        for (Integer i : indexes) {
            try {
                if(value == null) {
                    statement.setNull(i, Types.INTEGER);
                } else {
                    statement.setLong(i, value);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void setLong(int index, Long value) {
        try {
            if(value == null) {
                statement.setNull(index, Types.INTEGER);
            } else {
                statement.setLong(index, value);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Sets a parameter.
     *
     * @param name  parameter name
     * @param value parameter value
     * @throws IllegalArgumentException if the parameter does not exist
     * @see PreparedStatement#setInt(int, int)
     */
    public void setFloat(String name, Float value) {

        List<Integer> indexes = getIndexes(name);
        for (Integer i : indexes) {
            try {
                if(value == null) {
                    statement.setNull(i, Types.FLOAT);
                } else {
                    statement.setFloat(i, value);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


    /**
     * Sets a parameter.
     *
     * @param name  parameter name
     * @param value parameter value
     * @throws SQLException             if an error occurred
     * @throws IllegalArgumentException if the parameter does not exist
     * @see PreparedStatement#setTimestamp(int, java.sql.Timestamp)
     */
    public void setTimestamp(String name, Timestamp value) throws SQLException {
        List<Integer> indexes = getIndexes(name);
        for (Integer i : indexes) {
            statement.setTimestamp(i, value);
        }
    }


    /**
     * Returns the underlying statement.
     *
     * @return the statement
     */
    public PreparedStatement getStatement() {
        return statement;
    }


    /**
     * Executes the statement.
     *
     * @return true if the first result is a {@link ResultSet}
     * @see PreparedStatement#execute()
     */
    public boolean execute() {
        try {
            return statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }


    /**
     * Executes the statement, which must be a query.
     *
     * @return the query results
     * @see PreparedStatement#executeQuery()
     */
    public SafeResultSet executeQuery() {
        try {
            return new SafeResultSet(statement.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * Executes the statement, which must be a query.
     *
     * @return the query results
     * @see PreparedStatement#executeQuery()
     */
    public <T>List<T> executeQuery(DbMapper<T> mapper) {
        try {
            List<T> out = new ArrayList<>();
            SafeResultSet rs =  new SafeResultSet(statement.executeQuery());
            while(rs.next()) {
                out.add(mapper.map(rs));
            }
            return out;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }


    /**
     * Executes the statement, which must be an SQL INSERT, UPDATE or DELETE
     * statement;
     * or an SQL statement that returns nothing, such as a DDL statement.
     *
     * @return number of rows affected
     * @throws SQLException if an error occurred
     * @see PreparedStatement#executeUpdate()
     */
    public int executeUpdate() throws SQLException {
        return statement.executeUpdate();
    }


    /**
     * Closes the statement.
     *
     * @throws SQLException if an error occurred
     * @see Statement#close()
     */
    public void close() throws SQLException {
        statement.close();
    }


    /**
     * Adds the current set of parameters as a batch entry.
     *
     */
    public void addBatch() {
        try {
            statement.addBatch();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Executes all of the batched statements.
     * <p>
     * See {@link Statement#executeBatch()} for details.
     *
     * @return update counts for each statement
     */
    public int[] executeBatch() {
        try {
            return statement.executeBatch();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet getGeneratedKeys() {
        try {
            return statement.getGeneratedKeys();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public <T> T getLastGeneratedKey(Class<T> clazz) {
        try {
            ResultSet resultSet = statement.getGeneratedKeys();
            while(resultSet.next()) {
                if(resultSet.isLast()) {
                    if(clazz.equals(Long.class)) {
                        return clazz.cast(resultSet.getLong(1));
                    } else if(clazz.equals(String.class)) {
                        return clazz.cast(resultSet.getString(1));
                    } else if(clazz.equals(Integer.class)) {
                        return clazz.cast(resultSet.getInt(1));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
