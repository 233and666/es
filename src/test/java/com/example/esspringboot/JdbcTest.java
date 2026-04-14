package com.example.esspringboot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcTest {
    public static void main(String[] args) {
        // 直接使用你配置的参数
        String url = "jdbc:mysql://localhost:3306/es?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
        String username = "root";
        String password = "1459929060";
        String driverClass = "com.mysql.cj.jdbc.Driver";

        Connection connection = null;

        try {
            // 1. 加载MySQL8驱动（8.0+版本可省略，自动注册）
            Class.forName(driverClass);
            // 2. 获取数据库连接
            connection = DriverManager.getConnection(url, username, password);
           System.out.println("✅ 原生JDBC数据库连接成功！");
            System.out.println("连接对象：" + connection);
        } catch (ClassNotFoundException e) {
            System.out.println("❌ 驱动加载失败！");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("❌ 数据库连接失败！");
            e.printStackTrace();
        } finally {
            // 关闭连接
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
