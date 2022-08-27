其中有两个类[CommonUpdateBlob.java](https://github.com/StephenHuge/MyJDBCReWrite/blob/master/src/com/jdbc/extensions/CommonUpdateBlob.java) 和 [CommonUseBatch.java](https://github.com/StephenHuge/MyJDBCReWrite/blob/master/src/com/jdbc/extensions/CommonUseBatch.java)，用来读写数据库（这里使用的是MySQL）中的Blob类型，也就是二进制大对象类型文件（一般存储长文本或者图片）和进行数据库批处理。  
# 在数据库中读写Blob类型的数据
MySQL中，BLOB是一个二进制大型对象，是一个可以存储大量数据的容器，它能容纳不同大小的数据。  
MySQL的四种BLOB类型(除了在存储的最大信息量上不同外，他们是等同的)  
图片1  
实际使用中根据需要存入的数据大小定义不同的BLOB类型。需要注意的是：如果存储的文件过大，数据库的性能会下降。  
插入 BLOB 类型的数据必须使用 PreparedStatement：因为 BLOB 类型的数据时无法使用字符串拼写的。  

# 在数据库中使用批量操作
当需要成批插入或者更新记录时。可以采用Java的批量更新机制，这一机制允许多条语句一次性提交给数据库批量处理。通常情况下比单独提交处理更有效率  
JDBC的批量处理语句包括下面两个方法：  
1. addBatch(String)：添加需要批量处理的SQL语句或是参数；  
2. executeBatch（）；执行批量处理语句；  

通常我们会遇到两种批量执行SQL语句的情况：  
多条SQL语句的批量处理；  
一个SQL语句的批量传参。  

