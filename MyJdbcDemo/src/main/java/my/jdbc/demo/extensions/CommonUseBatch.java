package my.jdbc.demo.extensions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import my.jdbc.demo.mytools.MyJDBCTools;
import org.junit.Test;

/**
 * 这是一个使用JDBC 批处理功能的类
 *
 * @author Stephen Huge
 * @date 2017年8月8日
 */
public class CommonUseBatch {

    private final static boolean USEBATCH = true;

    private final static boolean NOTUSEBATCH = false;


    /**
     * 测试类，分别对使用batch功能和不使用batch功能的时间进行打印对比
     */
    @Test
    public void testMyBatch() throws Exception {
        myBatch(USEBATCH);
        System.out.println("--------------");
        myBatch(NOTUSEBATCH);
    }

    /**
     * @param USEBATCH 是否需要使用batch功能
     */
    public void myBatch(boolean USEBATCH) throws Exception {
        if (USEBATCH) {
            System.out.println("--使用batch--");
            myUseBatch();
        } else {
            System.out.println("--不使用batch--");
            myNotUseBatch();
        }
    }

    /**
     * 使用for循环对数据库进行大量数据的插入操作，尝试使用batch。
     */
    public void myUseBatch() throws Exception {
        Connection connection = null;
        PreparedStatement ps = null;

        String sql = "INSERT INTO batch(name, password)"
                + " VALUES(?, ?)";
        try {
            connection = MyJDBCTools.getConnection();
            ps = connection.prepareStatement(sql);

            MyJDBCTools.beginTransaction(connection);

            long startTime = System.currentTimeMillis();

            for (int i = 0; i < 100000; i++) {
                String user = "user_" + i;
                String password = "password_" + i;

                ps.setString(1, user);
                ps.setString(2, password);
                ps.addBatch();
            }
            ps.executeBatch();

            connection.commit();

            long stopTime = System.currentTimeMillis();

            System.out.println("耗费时间为：" + (stopTime - startTime) / 1000 + " 秒");
        } catch (Exception e) {
            e.printStackTrace();
            try {
                assert connection != null;
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            MyJDBCTools.releaseDB(ps, connection);
        }
    }

    /**
     * 使用for循环对数据库进行大量数据的插入操作，不使用batch。
     */
    public void myNotUseBatch() throws Exception {
        Connection connection = null;
        PreparedStatement ps = null;

        String sql = "INSERT INTO batch(name, password)"
                + " VALUES(?, ?)";
        try {
            connection = MyJDBCTools.getConnection();
            ps = connection.prepareStatement(sql);

            MyJDBCTools.beginTransaction(connection);

            long startTime = System.currentTimeMillis();

            for (int i = 0; i < 100000; i++) {
                String user = "user_" + i;
                String password = "password_" + i;

                ps.setString(1, user);
                ps.setString(2, password);
                ps.execute();
            }

            connection.commit();

            long stopTime = System.currentTimeMillis();

            System.out.println("耗费时间为：" + (stopTime - startTime) / 1000 + " 秒");
        } catch (Exception e) {
            e.printStackTrace();
            try {
                assert connection != null;
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            MyJDBCTools.releaseDB(ps, connection);
        }
    }
}
