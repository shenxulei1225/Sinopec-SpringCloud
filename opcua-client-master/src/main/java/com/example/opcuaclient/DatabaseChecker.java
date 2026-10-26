package com.example.opcuaclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Component
public class DatabaseChecker implements CommandLineRunner {
    
    @Autowired
    private DataSource dataSource;
    
    @Override
    public void run(String... args) throws Exception {
        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            
            // Check if tables exist
            ResultSet resultSet = statement.executeQuery("SHOW TABLES");
            List<String> tables = new ArrayList<>();
            
            while (resultSet.next()) {
                tables.add(resultSet.getString(1));
            }
            
            System.out.println("数据库中的表:");
            for (String table : tables) {
                System.out.println(table);
            }
            
            // Close connections
            resultSet.close();
            statement.close();
            connection.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}