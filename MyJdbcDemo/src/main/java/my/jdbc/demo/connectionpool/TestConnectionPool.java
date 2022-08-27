package my.jdbc.demo.connectionpool;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestConnectionPool {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testMyDBCP() throws SQLException {
        Connection connection = CommonConnectionPool.myDBCP();
        assert connection != null;
        System.out.println(connection);
    }

    @Test
    public void testMyDBCPWithDataSourceFactory() throws Exception {
        Connection connection = CommonConnectionPool.myDBCPWithDataSourceFactory();
        assert connection != null;
        System.out.println(connection);
    }

    @Test
    public void testMyC3P0() throws PropertyVetoException, SQLException {
        Connection connection = CommonConnectionPool.myC3P0();
        assert connection != null;
        System.out.println(connection);
    }

    @Test
    public void testMyC3P0WithXML() throws SQLException {
        Connection connection = CommonConnectionPool.myC3P0WithXML();
        assert connection != null;
        System.out.println(connection);
    }
}
