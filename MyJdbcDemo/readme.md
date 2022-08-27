这是一个学习JDBC过程中做的一些基础知识的总结，总体上有很多不足，但抱着记录所学的想法，我还是决定将它认真的做一下。  
其中分为6个包：  
**[com.jdbc.common](https://github.com/StephenHuge/MyJDBCReWrite/tree/master/src/com/jdbc/common)：存储一些基础知识**  

**[com.jdbc.extensions](https://github.com/StephenHuge/MyJDBCReWrite/tree/master/src/com/jdbc/extensions)： 存储一些初基础之外需要掌握的知识**  

**[com.jdbc.transaction](https://github.com/StephenHuge/MyJDBCReWrite/tree/master/src/com/jdbc/transaction)： 存储JDBC中关于数据库事务的知识**  

**[com.jdbc.connectionpool](https://github.com/StephenHuge/MyJDBCReWrite/tree/master/src/com/jdbc/connectionpool)： 存储JDBC中关于数据库连接池的知识**  

**[com.jdbc.dbutilstest](https://github.com/StephenHuge/MyJDBCReWrite/tree/master/src/com/jdbc/dbutilstest)： 存储对Apache开发的一个工具类DBUtils的简单测试**  

**[com.jdbc.mytools](https://github.com/StephenHuge/MyJDBCReWrite/tree/master/src/com/jdbc/mytools)： 工具包，存储自己抽取出来的一些工具代码**  


顺序是按照学习先后顺序，这只是一个简陋的不成形的代码的集合，仅作为自己知识的记录。  

在数据库中创建的`schema` 为：`myjdbcdemo` ，其中的`table` ：`user` ，`singer`，`batch`  。   
table `user` ：  

<table>
        <tr>
            <th>id</th>
            <th>name</th>
            <th>password</th>
        </tr>
        <tr>
            <th>1</th>
            <th>李雷</th>
            <th>hanmeimei</th>
        </tr>
        <tr>
            <th>2</th>
            <th>韩梅梅</th>
            <th>lihua</th>
        </tr>
        <tr>
            <th>　</th>
            <th>　</th>
            <th>　</th>
        </tr>
</table>

table `singer` ：  


<table>
        <tr>
            <th>id</th>
            <th>name</th>
            <th>bestsong</th>
			<th>image</th>
        </tr>
        <tr>
            <th>1</th>
            <th>Jay Chou</th>
            <th>七里香</th>
            <th>　</th>
        </tr>
        <tr>
            <th>2</th>
            <th>林俊杰</th>
            <th>美人鱼</th>
            <th>　</th>
        </tr>
        <tr>
            <th>　</th>
            <th>　</th>
            <th>　</th>
            <th>　</th>
        </tr>
    </table>

table `batch`：  

| id  | name   | password   |
|-----|--------|------------|
| 1   | user_1 | password_1 |
| ... | ...    | ...        |
