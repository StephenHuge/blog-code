package my.jdbc.demo.common;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import my.jdbc.demo.mytools.MyJDBCTools;

/**
 * 在上面{@code CommonJDBC}中我们学习了JDBC的5个基本属性：
 * Connection、 Statement、PreparedStatement、ResultSet和ResultSetMetaData。
 * 通过操作这5个属性，我们知道了如何对数据库进行基本的CRUD操作，在这个类中我们将对如何对数据库的CRUD操作
 * 进行较为详细的说明。具体的说，CRUD操作在业务代码中一般都会抽象成一个Dao类。
 * <p>
 * 所谓Dao类，即为Data Acess Object，是访问数据信息的类，对数据可以进行CRUD操作。
 * 其中不应包含任何业务逻辑。
 * <p>
 * 在Dao类中有四个方法，分别是：
 * 1. 包含insert update delete 操作的update方法，传入的是SQL语句和跟它对应的占位符参数；
 * 2. 将一个对象写入数据库的write方法，和update方法的区别是它的参数是一个对象；
 * 3. 查询一条记录的get方法，其中有两种方法，在读取数据之后，可以分别使用
 * a. 构造函数创建对象；
 * b. 反射创建对象。
 * 两种方法相比，第二种适用范围更广，所以第一种方法被@Deprecated 修饰，仅供参考。
 * 4. 查询多条记录的getForList方法（是get方法的超集）；
 * 5. 查询一条记录中某个属性的getForField方法。
 *
 * @author Administrator
 * @date 2017年8月3日 下午10:27:47
 */
public class JDBCDao {

    /**
     * 使用可变参数对SQL进行更新。
     *
     * @param sql  一般是带占位符的SQL语句
     * @param args 可变参数的数组，用来填充SQL语句中的占位符
     */
    public static void update(String sql, Object... args) throws Exception {

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = MyJDBCTools.getConnection();
            ps = connection.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {
                //遍历args占位符数组，为每个占位符赋值，占位符位置初始值为1
                ps.setObject(i + 1, args[i]);
            }

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            MyJDBCTools.releaseDB(ps, connection);
        }
    }

    /**
     * 向数据库中写入对象，其内部实现是调用singer的get方法获取到其属性，之后调用update方法将属性放入SQL语句中
     * 并最终将该对象存放入数据库。
     *
     * @param singer 传入的Singer对象
     */
    public static void write(Singer singer) throws Exception {
        if (singer == null || !(singer instanceof Singer)) {
            throw new NullPointerException("Singer对象输入有误！");
        }
        String sql = "INSERT INTO singer(name, bestsong) "
                + "VALUES(?, ?)";
        update(sql, singer.getName(), singer.getBestSong());
    }

    /**
     * 从数据库读取数据并创建对象，有两种方法
     * 1） 构造函数创建
     * 2）反射创建：原因是为了抽取共性，代码能够复用
     *
     * @param sql  一般是带占位符的SQL语句
     * @param args 可变参数的数组，用来填充SQL语句中的占位符
     * @return 返回获取的对象，在此处为Singer对象
     * @deprecated 由于使用反射适用范围更广，此方法弃用，参见{@link #getByReflection}
     */
    @Deprecated
    public static Object getByConstructor(String sql, Object... args) throws Exception {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = MyJDBCTools.getConnection();
            ps = connection.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            rs = ps.executeQuery();

            if (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                String bestSong = rs.getString(3);

                return new Singer(id, name, bestSong);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            MyJDBCTools.releaseDB(rs, ps, connection);
        }
        return null;
    }

    /**
     * 通过反射从数据库中获取对象属性，具体实现是：
     * （1） 读取rsmd，通过rsmd中的值获取rs中相对应的值，将键值对存入一个HashMap；
     * （2）遍历HshMap，通过反射将读取的值赋值到对象中，其中使用了工具类{@code BeanUtils}。
     * <p>
     * 被@Deprecated修饰是因为我们在后面会对它进行重构，重构中有更好的实现。
     *
     * @param clazz 需要创建类的类型
     * @param sql   一般是带占位符的SQL语句
     * @param args  可变参数的数组，用来填充SQL语句中的占位符
     * @return 一个传入类型的实例
     * @deprecated 直接使用反射创建对象，由于代码重构，我们直接调用了{@link #getForList}
     * 方法实现该方法 ，参见{@link #getByReflection}
     */
    @Deprecated
    public static <T> T getByReflectionWithoutList(Class<T> clazz, String sql, Object... args) throws Exception {

        T entity = null;

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = MyJDBCTools.getConnection();
            ps = connection.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();

            Map<String, Object> map = new HashMap<>();    //使用HashMap装取属性和值对应的键值对

            if (rs.next()) {
                for (int j = 0; j < rsmd.getColumnCount(); j++) {
                    String label = rsmd.getColumnLabel(j + 1);
                    Object value = rs.getObject(label);

                    map.put(label, value);
                }
            }

            if (map.size() > 0) {
                entity = clazz.newInstance();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    String key = entry.getKey();
                    Object val = entry.getValue();

                    BeanUtils.setProperty(entity, key, val);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            MyJDBCTools.releaseDB(rs, ps, connection);
        }
        return entity;
    }

    /**
     * 使用getByForList()实现。
     *
     * @param clazz 需要创建类的类型
     * @param sql   一般是带占位符的SQL语句
     * @param args  可变参数的数组，用来填充SQL语句中的占位符
     * @return 一个装着多个传入类的不同实例的List
     */
    public static <T> T getByReflection(Class<T> clazz, String sql, Object... args) throws Exception {

        List<T> list = getForList(clazz, sql, args);

        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 将数据库中的多条记录转为多个对象，并使用一个装着多个不同类实例的List返回。其实现是复杂版的
     * {@link #getByReflectionWithoutList(Class, String, Object...)} ()}。
     * 在代码重构中，将 把结果集转化为List这一逻辑抽取成了一个私有方法
     * {@link #transferResultSetToBeanList}。
     *
     * @param clazz 需要创建类的类型
     * @param sql   一般是带占位符的SQL语句
     * @param args  可变参数的数组，用来填充SQL语句中的占位符
     * @return 一个装着多个传入类的不同实例的List
     */
    public static <T> List<T> getForList(Class<T> clazz, String sql, Object... args) throws Exception {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = MyJDBCTools.getConnection();
            ps = connection.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            rs = ps.executeQuery();
            if (rs.next()) {
                return transferResultSetToBeanList(clazz, rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            MyJDBCTools.releaseDB(rs, ps, connection);
        }
        return null;
    }

    /**
     * 从getForList()方法中抽取出来的方法，传入一个Class<T>类型的参数和一个ResultSet类型的参数，通过反射生成
     * 一个装着多个不同类实例的List。
     *
     * @param clazz 需要创建类的类型
     * @param rs    一个结果集
     * @return 一个装着多个传入类的不同实例的List
     * @throws Exception 当方法执行出错时，会抛出异常
     */
    private static <T> List<T> transferResultSetToBeanList(Class<T> clazz, ResultSet rs)
            throws Exception {

        ResultSetMetaData rsmd = rs.getMetaData();    //获取表中列的名字
        List<T> list = new ArrayList<>();

        do {
            Map<String, Object> map = new HashMap<>();

            // 获取查询结果
            for (int j = 0; j < rsmd.getColumnCount(); j++) {    // 表的列数
                String label = rsmd.getColumnLabel(j + 1);    // 表每一列的名字
                Object value = rs.getObject(label);

                map.put(label, value);
            }

            T instance = null;
            // 生成并添加元素
            if (map.size() > 0) {
                instance = clazz.newInstance();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    String key = entry.getKey();
                    Object val = entry.getValue();

                    BeanUtils.setProperty(instance, key, val);
                }

                list.add(instance);
            }
        } while (rs.next());
        return list;
    }

    /**
     * 返回某条记录的某一个字段的值 或 一个统计的值(一共有多少条记录等)，
     * 一般得到的结果集应该只有一行, 且只有一列
     *
     * @param sql  一般是带占位符的SQL语句
     * @param args 可变参数的数组，用来填充SQL语句中的占位符
     * @return 获取到的某条记录的某个具体的属性
     */
    @SuppressWarnings("unchecked")
    public static <E> E getForField(String sql, Object... args) throws Exception {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = MyJDBCTools.getConnection();
            ps = connection.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            rs = ps.executeQuery();

            if (rs.next()) {
                return (E) rs.getObject(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            MyJDBCTools.releaseDB(rs, ps, connection);
        }
        return null;
    }
}
