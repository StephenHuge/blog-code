package my.jdbc.demo.connectionpool;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;

/**
 * JDBC中的两种数据库连接池实现：DBCP和C3P0 的简单测试。
 *
 * @author Administrator
 * @date 2017年8月5日 下午6:22:33
 */
public class CommonConnectionPool {


    public CommonConnectionPool() {
    }

    /**
     * DBCP的实现类是{@code BasicDataSource}。
     *
     * @return 由DBCP数据库连接池获取得到的Connection
     */
    public static Connection myDBCP() throws SQLException {
        final BasicDataSource dataSource = new BasicDataSource();

        //为数据源实例指定必须的属性
        dataSource.setUsername("root");
        dataSource.setPassword("1234");
        dataSource.setUrl("jdbc:mysql:///myjdbcdemo");
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");

        //指定数据源的一些可选的属性.
        dataSource.setInitialSize(5);    //指定数据库连接池中初始化连接数的个数

        dataSource.setMaxIdle(5);        //指定最大的连接数: 同一时刻可以同时向数据库申请的连接数

        dataSource.setMinIdle(2);        //指定小连接数: 在数据库连接池中保存的最少的空闲连接的数量

        dataSource.setMaxWaitMillis(1000 * 5);//等待数据库连接池分配连接的最长时间. 单位为毫秒. 超出该时间将抛出异常.

        //从数据源中获取数据库连接
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 通过properties文件和{@code BasicDataSourceFactory} 产生dataSource。
     *
     * @return 由DBCP数据库连接池获取得到的Connection
     */
    public static Connection myDBCPWithDataSourceFactory() throws Exception {
        DataSource dataSource = null;

        Properties properties = new Properties();
        InputStream inStream = CommonConnectionPool.class
                .getClassLoader()
                .getResourceAsStream("dbcp.properties");

        try {
            properties.load(inStream);

            dataSource = (DataSource) BasicDataSourceFactory.createDataSource(properties);
            return dataSource.getConnection();

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * C3P0的实现类是{@code ComboPooledDataSource}， 我们在{@code MyJDBCTools}中获取Connection的方法
     * {@code MyJDBCTools#getConnectionV1()}之后由此方式重写并代替。
     *
     * @return 由C3P0数据库连接池获取得到的Connection
     */
    public static Connection myC3P0() throws PropertyVetoException, SQLException {

        ComboPooledDataSource cpds = new ComboPooledDataSource();

        try {
            cpds.setDriverClass("com.mysql.jdbc.Driver");
            cpds.setJdbcUrl("jdbc:mysql:///myjdbcdemo");
            cpds.setUser("root");
            cpds.setPassword("1234");

            return cpds.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 通过xml文件配置C3P0数据库连接池获取Connection。
     *
     * @return 由C3P0数据库连接池获取得到的Connection
     */
    public static Connection myC3P0WithXML() throws SQLException {
        ComboPooledDataSource cpds = new ComboPooledDataSource("c3p0_prop");

        try {
            return cpds.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
