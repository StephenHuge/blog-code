package my.jdbc.demo.dbutilstest;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import my.jdbc.demo.common.Singer;
import org.apache.commons.dbutils.QueryLoader;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.Test;

import my.jdbc.demo.mytools.MyJDBCTools;

/**
 * 我们知道，对数据库的操作一般分为两大类：更新（update）和查询(select)。
 * <p>
 * 更新的一般流程是：先创建一个Connection，然后通过Connection获得PreparedStatement，最后PreparedStatement
 * 执行SQL语句完成更新。这其中PreparedStatement的executeUpdate()方法执行后，一般返回值是一个数字，源代码中的原话是
 * " either (1) the row count for SQL Data Manipulation Language (DML) statements or (2) 0
 * for SQL statements that return nothing"。意思是返回的值要么是SQL数据操作语言（DML）语句的行数，要么因为SQL
 * 声明没有返回任何东西而返回0。
 * <p>
 * 而查询的流程大体上和更新相同，也是需要先创建一个Connection，然后通过Connection获得PreparedStatement，最后
 * PreparedStatement执行SQL语句完成查询。不同的是其中PreparedStatement的executeQuery()方法执行后返回的是一个
 * ResultSet也就是结果集对象。那么这些和DBUtils包又有什么关系呢？？
 * <p>
 * DBUtils是一个工具类，它的作用是为了简化数据库中复杂的操作，其中比较常用的有{@code QueryRunner}类和
 * {@code ResultSetHandle}接口。
 * <p>
 * 上面提到的{@code QueryRunner}类和{@code ResultSetHandle}接口其实就是为了对上述过程进行封装简化而出现的，其实有
 * 了Connection之后，再逐个创建PreparedStatement、ResultSet并且调用它们的各种方法是比较麻烦而且很多时候步骤是完全相同
 * 而没有必要的。
 * <p>
 * 对于更新操作（update），我们在得到Connection之后，其实只需要把它和SQL语句中的占位符传进一个方法，让它执行，如果出错就抛出异
 * 常就可以了，这其实就是{@code QueryRunner}中的
 * {@link QueryRunner #update(Connection conn, String sql, Object... params)}方法；
 * <p>
 * 相对于查询(select)方法，情况会稍微复杂一点，因为查询需要返回查询结果（例如JDBC中是一个ResultSet），但也完全在理解范围之内。
 * 我们会同时用到{@code QueryRunner}类和{@code ResultSetHandle}接口，具体方法是调用
 * {@code link QueryRunner #query(Connection conn, String sql, ResultSetHandler<T> rsh,Object... params)}，
 * 传入的参数就是一个 ResultSetHandle实现类，不同的实现类会对ResutSet对象进行不同方法的"加工"操作。
 * <p>
 * 由于大部分时候，对数据"加工"的方式就那么几种，比较固定，所以DBUtils包中为我们提供了几个默认的的ResultSetHandle的实现类。
 * <p>
 * 我们记得，之前在方法{@code com.jdbc.common.JDBCDao# getByReflection(Class<T>, String, Object...)}
 * 中，我们将ResultSet对象中的值经过反射变成了一个T类型的对象，在
 * {code JDBCDao#getForList(Class<T> clazz, String sql, Object... args)}方法中，将ResutSet对象中的值
 * 经过反射变成了一个成员类型为T的List对象。
 * 事实上，这两个方法有默认的实现，分别对应的是ResultSetHandle接口的实现类{@code BeanHandle}和{@code BeanListHandle}。
 * <p>
 * 其它的实现类还有很多，例如{@code ArrayHandler}、{@code MapHandler}以及它们对应的集合类等。
 * <p>
 * 综上所述，这个类的出现是为了简化JDBC对数据库的操作流程，我们之前已经自己实现过类似的功能，在此知道其常见使用方法即可。
 *
 * @author Administrator
 * @date 2017年8月5日 下午10:58:49
 */
public class CommonDBUtilsTest {

    /**
     * 使用QueryRunner测试数据库更新（update）操作，我们发现较以前的操作，简洁了非常多。
     */
    @Test
    public void testQueryRunnerUpdate() throws Exception {
        Connection connection = null;

        String sql = "DELETE FROM singer WHERE id >= ?";
        try {
            connection = MyJDBCTools.getConnection();

            QueryRunner qr = new QueryRunner();

            qr.update(connection, sql, 9);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            MyJDBCTools.releaseDB(connection);
        }
    }

    /**
     * 使用QueryRunner和BeanHandle测试数据库查询（select）操作。
     */
    @Test
    public void testQueryRunnerSelectWithBeanHandle() throws Exception {
        Connection connection = null;

        String sql = "SELECT id, name, bestsong "
                + "	FROM singer WHERE id = ?";
        try {
            connection = MyJDBCTools.getConnection();

            QueryRunner qr = new QueryRunner();
            ResultSetHandler<Singer> beanHandle = new BeanHandler<>(Singer.class);

            Singer singer = qr.query(connection, sql, beanHandle, 2);

            System.out.println(singer);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            MyJDBCTools.releaseDB(connection);
        }
    }

    /**
     * 使用QueryRunner和BeanListHandle测试数据库查询（select）操作。
     */
    @Test
    public void testQueryRunnerSelectWithBeanListHandle() throws Exception {
        Connection connection = null;

        String sql = "SELECT id, name, bestsong "
                + "	FROM singer ";
        try {
            connection = MyJDBCTools.getConnection();

            QueryRunner qr = new QueryRunner();
            ResultSetHandler<List<Singer>> beanListHandle = new BeanListHandler<>(Singer.class);

            List<Singer> singers = qr.query(connection, sql, beanListHandle);

            System.out.println(singers);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            MyJDBCTools.releaseDB(connection);
        }
    }

    /**
     * 使用QueryRunner和MapHandle测试数据库查询（select）操作。
     */
    @Test
    public void testQueryRunnerSelectWithMapHandle() throws Exception {
        Connection connection = null;

        String sql = "SELECT id, name, bestsong "
                + "	FROM singer WHERE id = ?";
        try {
            connection = MyJDBCTools.getConnection();

            QueryRunner qr = new QueryRunner();
            ResultSetHandler<Map<String, Object>> beanHandle = new MapHandler();

            Map<String, Object> map = qr.query(connection, sql, beanHandle, 2);

            System.out.println(map);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            MyJDBCTools.releaseDB(connection);
        }

    }


    /**
     * 使用QueryRunner和MapListHandle测试数据库查询（select）操作。
     */
    @Test
    public void testQueryRunnerSelectWithMapListHandle() throws Exception {
        Connection connection = null;

        String sql = "SELECT id, name, bestsong "
                + "	FROM singer";
        try {
            connection = MyJDBCTools.getConnection();

            QueryRunner qr = new QueryRunner();
            ResultSetHandler<List<Map<String, Object>>> beanHandle = new MapListHandler();

            List<Map<String, Object>> maps = qr.query(connection, sql, beanHandle);

            System.out.println(maps);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            MyJDBCTools.releaseDB(connection);
        }

    }

    /**
     * 使用QueryRunner和ScalarHandle测试数据库查询（select）操作。
     * ScalarHandler: 可以返回指定列的一个值或返回一个统计函数的值。
     */
    @Test
    public void testQueryRunnerSelectWithScalarHandle() throws Exception {
        Connection connection = null;

        String sql = "SELECT name"
                + "	FROM singer WHERE id = ?";
        try {
            connection = MyJDBCTools.getConnection();

            QueryRunner qr = new QueryRunner();
            ResultSetHandler<Object> scalarHandler = new ScalarHandler();

            Object name = qr.query(connection, sql, scalarHandler, 3);

            System.out.println(name);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            MyJDBCTools.releaseDB(connection);
        }
    }

    /**
     * QueryLoader: 可以用来加载存放着 SQL 语句的资源文件.
     * 使用该类可以把 SQL 语句外置化到一个资源文件中. 以提供更好的解耦
     */
    @Test
    public void testQueryLoader() throws IOException {
        try {
            Map<String, String> sqls =
                    QueryLoader.instance().load("/sql.properties");

            String updateSql = sqls.get("UPDATE_CUSTOMER");
            System.out.println(updateSql);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
