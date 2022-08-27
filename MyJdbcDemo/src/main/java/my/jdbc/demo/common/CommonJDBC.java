package my.jdbc.demo.common;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

import my.jdbc.demo.mytools.MyJDBCTools;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * 对JDBC基础知识的一个简单总结和测试，通过这些测试，对基础知识的认识又深了一层。
 *
 * @author Stephen Huge
 * @date 2017年8月3日
 */
public class CommonJDBC {

    /**
     * 对最基础的Connection进行测试，使用4个参数通过反射可以成功创建一个Connection对象。
     */
    @Test
    public void testConnection() throws SQLException, ClassNotFoundException {
        String username = "root";
        String password = "123456";
        String jdbcUrl = "jdbc:mysql://127.0.0.1:3307/myjdbcdemo";
        String driverClass = "com.mysql.jdbc.Driver";

        Connection connection = null;

        try {
            Class.forName(driverClass);

            connection = DriverManager
                    .getConnection(jdbcUrl, username, password);

            System.out.println(connection);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            MyJDBCTools.releaseDB(connection);
        }

    }

    /**
     * 使用外置的properties文件对数据库信息进行配置，通过输入流对其进行读取获取参数，
     * 最终获得Connection。
     * 在测试成功之后，我们将获取Connection抽取为一个方法{@link MyJDBCTools#getConnectionV1()}，
     * 在之后的其它测试中均使用此方法代替初始获取Connection的方法。
     */
    @Test
    public void testConnectionWithProperties() throws SQLException, IOException, ClassNotFoundException {
        String username = null;
        String password = null;
        String jdbcUrl = null;
        String driverClass = null;

        Properties properties = new Properties();

        InputStream inStream = CommonJDBC.class
                .getClassLoader()
                .getResourceAsStream("jdbc.properties");

        Connection connection = null;

        try {
            properties.load(inStream);

            username = properties.getProperty("username");
            password = properties.getProperty("password");
            jdbcUrl = properties.getProperty("jdbcUrl");
            driverClass = properties.getProperty("driverClass");

            Class.forName(driverClass);

            connection = DriverManager
                    .getConnection(jdbcUrl, username, password);

            System.out.println(connection);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            MyJDBCTools.releaseDB(connection);
        }
    }


    /**
     * 测试Statement，Statement的作用是执行SQL语句以及获得查询 & 修改结果，
     * 我们会在下一个例子中提到。
     */
    @Test
    public void testStatement() throws Exception {
        String sql = "SELECT id, name, bestsong"
                + " FROM singer WHERE id = 1";

        Connection connection = null;
        Statement statement = null;

        try {
            connection = MyJDBCTools.getConnection();
            statement = connection.createStatement();

            statement.execute(sql);

            System.out.println(statement);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            }
            MyJDBCTools.releaseDB(connection);
        }
    }

    /**
     * PreparedStatement从字面意思就能看出来，它在执行SQL语句时会先传入SQL语句并预先编译，
     * 跟Statement相比，它除了可以预先编译，在多次执行SQL语句时提高效率，而且它可以有效防止SQL注入，
     * 详细信息可以参见{@code testSQLInjection}。
     * 当PreparedStatement执行修改操作时(调用{@code PreparedStatement #excuteUpdate()})，
     * 其返回值是一个int型变量，此时不会产生结果集。
     */
    @Test
    public void testPreparedStatement() throws Exception {
        String sql = "SELECT id, name, bestsong"
                + " FROM singer WHERE id = ?";

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = MyJDBCTools.getConnection();
            ps = connection.prepareStatement(sql);
            ps.setInt(1, 1);

            ps.executeQuery();

            System.out.println(ps);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            MyJDBCTools.releaseDB(ps, connection);
        }
    }


    /**
     * 测试SQL注入。执行结果是"登陆成功！"。
     * 如果采用简单的拼接方式拼接SQL语句，则可能会导致SQL被注入。
     */
    @Test
    public void testSQLInjection() throws Exception {
        String name = "'suibian' OR password = ' ";
        String sqlPassword = " ' OR '1' = '1' ";

        String sql = "SELECT name, password"
                + " FROM user WHERE name = " + name + " AND password = " + sqlPassword;

        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;

        try {
            connection = MyJDBCTools.getConnection();
            statement = connection.createStatement();

            rs = statement.executeQuery(sql);

            if (rs.next()) {
                System.out.println("登陆成功！");
            } else {
                System.out.println("账号或者密码错误，登陆错误！");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            }
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            }
            MyJDBCTools.releaseDB(connection);
        }
    }

    /**
     * 使用 PreparedStatement 后SQL注入失败，有效的解决了 SQL 注入问题.
     */
    @Test
    public void testSQLInjectionWithPS() throws Exception {
        Assert.assertThrows(SQLSyntaxErrorException.class, () -> {
            String name = "'suibian' OR password = ' ";
            String sqlPassword = " ' OR '1' = '1' ";

            String sql = "SELECT name, password"
                    + " FROM user WHERE name = ? AND password = ?";

            Connection connection = null;
            PreparedStatement ps = null;
            ResultSet rs = null;

            try {
                connection = MyJDBCTools.getConnection();
                ps = connection.prepareStatement(sql);
                ps.setString(1, name);
                ps.setString(2, sqlPassword);

                try {
                    rs = ps.executeQuery(sql);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }

                if (rs.next()) {
                    System.out.println("登陆成功！");
                } else {
                    System.out.println("账号或者密码错误，登陆错误！");
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            } finally {
                MyJDBCTools.releaseDB(rs, ps, connection);
            }
        });
    }

    /**
     * ResultSet返回一个查询结果的结果集，通过读取其每个字段的内容可以将查询结果打印出来。
     * 如果PreparedStatement执行修改操作，则返回值是一个int型变量，此时不会产生结果集。
     */
    @Test
    public void testResultSet() throws Exception {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = "SELECT id, name, bestsong"
                + " FROM singer WHERE name = ?";
        String sqlName = "Jay Chou";
        try {
            connection = MyJDBCTools.getConnection();
            ps = connection.prepareStatement(sql);
            ps.setString(1, sqlName);

            rs = ps.executeQuery();

            if (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                String bestSong = rs.getString(3);

                System.out.println("id: " + id +
                        ", name: " + name +
                        ", bestSong: " + bestSong);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            MyJDBCTools.releaseDB(rs, ps, connection);
        }
    }

    /**
     * ResultSetMetaData 返回查询结果集的元数据，也就是每个字段的名称或者别名，
     * 可以通过相关方法得出每个字段的名称或者别名。
     */
    @Test
    public void testResultSetMetaData() throws Exception {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;

        String sql = "SELECT id ID, name NAME, bestsong BESTSONG"
                + " FROM singer WHERE name = ?";
        String sqlName = "Jay Chou";
        try {
            connection = MyJDBCTools.getConnection();
            ps = connection.prepareStatement(sql);
            ps.setString(1, sqlName);

            rs = ps.executeQuery();
            rsmd = rs.getMetaData();

            if (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                String bestSong = rs.getString(3);

                System.out.println(rsmd.getColumnLabel(1) + ": " + id + ", " +
                        rsmd.getColumnLabel(2) + ": " + name + ", " +
                        rsmd.getColumnLabel(3) + ": " + bestSong);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            MyJDBCTools.releaseDB(rs, ps, connection);
        }
    }

}
