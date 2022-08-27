package my.jdbc.demo.extensions;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.Test;

import my.jdbc.demo.mytools.MyJDBCTools;


public class CommonUpdateBlob {

    /**
     * 插入 BLOB 类型的数据必须使用 PreparedStatement：因为 BLOB 类型的数据时无法使用字符串拼写的。
     * 在这儿直接使用了QueryRunner，因为QueryRunner封装的就是PreparedStatement。
     */
    @Test
    public void myWriteBlob() throws Exception {
        Connection connection = null;

        String sql = "UPDATE singer SET image = ? WHERE name = ?";
        try {
            connection = MyJDBCTools.getConnection();
            QueryRunner qr = new QueryRunner();

            InputStream inStream = Files.newInputStream(Paths.get("src/main/resources/Jolin Cai.png"));

            qr.update(connection, sql, inStream, "Jolin Cai");

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            MyJDBCTools.releaseDB(connection);
        }
    }

    /**
     * 读取 blob 数据，通过ScalarHandle获得一个byte[]类型的对象，然后通过IO流读取该对象，将其输出为一个
     * 叫做“蔡依林.png”的图片。
     */
    @Test
    public void myReadBlob() throws Exception {
        Connection connection = null;

        String sql = "SELECT image "
                + "	FROM singer WHERE name = ?";

        try {
            connection = MyJDBCTools.getConnection();
            QueryRunner qr = new QueryRunner();
            ResultSetHandler<Object> rsh = new ScalarHandler<>();

            //此处返回的是一个byte[]型的对象，通过使用输出流读取它，将它转化为一个png格式的图片。
            byte[] pic = (byte[]) qr.query(connection, sql, rsh, "Jolin Cai");

            @SuppressWarnings("resource")
            OutputStream out = Files.newOutputStream(Paths.get("src/main/resources/蔡依林.png"));

            System.out.println(pic.length);

            out.write(pic, 0, pic.length);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            MyJDBCTools.releaseDB(connection);
        }
    }
}
