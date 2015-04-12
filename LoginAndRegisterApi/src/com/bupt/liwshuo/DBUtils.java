
package com.bupt.liwshuo;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shuo on 2015/4/7.
 */
public class DBUtils {
    private Connection connection = null;
    private Statement statement = null;

    public DBUtils() {
        
    }
    public DBUtils(String jdbc, String url, String user, String password) {
        try {
            Class.forName(jdbc);
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建表
     * @param createSql
     */
    public void createTable(String createSql) {
        try {
            statement.execute(createSql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 插入数据
     * @param table
     * @param columns
     * @param args
     * @return
     */
    public int insert(String table, String[] columns, String[] args) {
        if(columns != null && columns.length ==  0) {
            return -1;
        }
        if(args != null && columns.length != args.length) {
            return -1;
        }
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ");
        sql.append(table + "(");
        for(int i = 0; i < columns.length - 1;i++) {
            sql.append(columns[i]);
            sql.append(",");
        }
        sql.append(columns[columns.length - 1]);
        sql.append(") VALUES(");
        for (int i = 0; i < args.length - 1; i++) {
            sql.append("?,");
        }
        sql.append("?)");
        ResultSet rs = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql.toString(),Statement.RETURN_GENERATED_KEYS);
            for (int i = 1; i <= args.length; i++) {
                preparedStatement.setString(i, args[i - 1]);
            }
            preparedStatement.executeUpdate();
            rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return -1;
    }

    /**
     * 查询数据
     * @param table
     * @param columns
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @return
     */
    public ResultSet query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        if(columns != null && columns.length != 0) {
            appendColumns(sql, columns);
        }else {
            sql.append("* ");
        }
        sql.append("FROM ");
        sql.append(table);
      //  String where = bindSelection(selection, selectionArgs);
        appendClause(sql, " WHERE ", selection);
        appendClause(sql," GROUP BY ", groupBy);
        appendClause(sql, " HAVING ", having);
        appendClause(sql, " ORDER BY ", orderBy);
        System.out.println(sql.toString());
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql.toString(),ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            for (int i = 1; i <= selectionArgs.length; i++) {
                preparedStatement.setString(i, selectionArgs[i-1]);
            }
            ResultSet rs = preparedStatement.executeQuery();
            return preparedStatement.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 更新数据
     * @param table
     * @param values
     * @param whereClause
     * @param whereArgs
     * @return
     */
    public int update(String table,Map<String,String> values, String whereClause, String[] whereArgs) {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ");
        sql.append(table);
        sql.append(" SET ");

        int setValuesSize = values.size();
        int bindArgsSize = (whereArgs == null) ? setValuesSize : (setValuesSize + whereArgs.length);
        String[] bindArgs = new String[bindArgsSize];
        int i = 0;
        for (String colName : values.keySet()) {
            sql.append((i > 0) ? "," : "");
            sql.append(colName);
            bindArgs[i++] = values.get(colName);
            sql.append("=?");
        }
        if (whereArgs != null) {
            for (i = setValuesSize; i < bindArgsSize; i++) {
                bindArgs[i] = whereArgs[i - setValuesSize];
            }
        }
        if (!whereClause.equals(null)) {
            sql.append(" WHERE ");
            sql.append(whereClause);
        }
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql.toString());
            for (int j = 1; j <= bindArgsSize; j++) {
                preparedStatement.setString(j,bindArgs[j-1]);
            }
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 删除数据
     * @param table
     * @param whereClause
     * @param whereArgs
     * @return
     */
    public int delete(String table, String whereClause, String[] whereArgs) {
        String sql = "DELETE FROM " + table + (whereClause != null ? " WHERE " + whereClause : "");
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for (int j = 1; j <= whereArgs.length; j++) {
                preparedStatement.setString(j,whereArgs[j-1]);
            }
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }


    private void appendClause(StringBuilder s, String name, String clause) {
        if (clause != null) {
            s.append(name);
            s.append(clause);
        }
    }

    private void appendColumns(StringBuilder s, String[] columns) {
        int n = columns.length;

        for (int i = 0; i < n; i++) {
            String column = columns[i];

            if (column != null) {
                if (i > 0) {
                    s.append(", ");
                }
                s.append(column);
            }
        }
        s.append(' ');
    }

    private String bindSelection(String selecton, String[] selectionArgs) {
        String result = null;
        for (String s : selectionArgs) {
            System.out.println(s);
            result = selecton.replaceFirst("\\?", "'" + s + "'");
        }
        return result;
    }

    /**
     * 关闭数据库
     */
    public void closeDB() {
        if(statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        DBUtils dbUtils = new DBUtils();
        Map<String, String> map = new HashMap<String, String>();
        map.put("1", "1");
        map.put("2", "2");
        map.put("3", "3");
        map.put("4", "4");
        String[] arggs = {"1"};
        dbUtils.update("test", map, " 1 = ?", arggs);
    }
}
