package com.mengma.config.center.controller;

import com.mengma.config.center.datasource.MyDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author fgm
 * @description
 * @date 2020-04-25
 ***/
@Controller
@RequestMapping
public class HelloController {

    @Autowired
    private DataSource dataSource;

    @RequestMapping(value = {"/","/dataSource"})
    @ResponseBody
    public String  dataSource(){
        try {

            MyDataSource myDataSource=(MyDataSource)dataSource;
            HikariDataSource dataSource =  (HikariDataSource)myDataSource.getDataSource();
            Connection connection=dataSource.getConnection();

            System.out.println("数据源信息:"+myDataSource.toString());


            //测试连接和查询
            PreparedStatement preparedStatement= connection.prepareStatement("select 1 from dual");
            ResultSet resultSet=preparedStatement.executeQuery();
            while (resultSet.next()){
                 int num= resultSet.getInt(1);
                 System.out.println("查询结果:"+num);
             }

        } catch (SQLException e) {
            System.out.println("数据源连接异常");
            e.printStackTrace();
        }
        return "OK!";

    }

}
