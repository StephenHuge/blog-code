package my.jdbc.demo.transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.junit.Test;

import my.jdbc.demo.mytools.MyJDBCTools;

/**
 * 关于事务:
 * 1. 如果多个操作中每个操作使用的是自己的单独的连接, 则无法保证事务，即必须使用同一个connection；
 * 2. 具体步骤:
 * 1). 事务操作开始前, 开始事务:取消 Connection 的默认提交行为；
 * 2). 如果事务的操作都成功,则提交事务；
 * 3). 回滚事务: 若出现异常, 则在 catch 块中回滚事务。
 * 3. 测试事务的隔离级别 在 JDBC 程序中可以通过 Connection 的 setTransactionIsolation 来设置事务的隔离级别。
 *
 * @author Administrator
 * @date 2017年8月5日 下午5:44:29
 */
public class CommonTransaction {

    /**
     * 关于事务的一个基础测试方法。
     */
    @Test
    public void myTransaction() throws Exception {
        Connection connection = null;
        PreparedStatement ps = null;

        String sql = "INSERT singer(name, bestsong) "
                + "VALUES(?, ?)";
        try {
            connection = MyJDBCTools.getConnection();

            MyJDBCTools.beginTransaction(connection);    // 开启事务

            ps = connection.prepareStatement(sql);

            ps.setObject(1, "王力宏");
            ps.setObject(2, "我们的歌");
            ps.execute();

            ps.setObject(1, "五月天");
            ps.setObject(2, "如烟");
            ps.execute();

            MyJDBCTools.commit(connection);// 提交事务

        } catch (Exception e) {
            e.printStackTrace();
            MyJDBCTools.rollback(connection);    // 发生异常则回滚事务
            throw e;

        } finally {
            MyJDBCTools.releaseDB(ps, connection);
        }
    }
}
