package com.mengma.config.center.datasource;

import com.mengma.config.center.config.DataSourceDO;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * @author fgm
 * @description  自定义数据源
 * @date 2020-04-27
 ***/
public class MyDataSource implements  DataSource, Closeable {



    //被代理的dataSource
    private  DataSource dataSource;

    public MyDataSource(HikariConfig hikariConfig){
        HikariDataSource dataSource=new HikariDataSource(hikariConfig);
        this.dataSource =dataSource;
    }

    public DataSource getDataSource(){
        return this.dataSource;
    }


    /**
     * 刷新服务
     * @param dataSourceDO
     */
    public void refresh(DataSourceDO dataSourceDO){
        synchronized (this){
            //历史数据源关闭
            close();
            //重新配置新数据源
            HikariConfig hikariConfig=initConfig(dataSourceDO);
            //刷新历史连接
            HikariDataSource dataSource=new HikariDataSource(hikariConfig);
            //重新设置数据源
            this.dataSource =dataSource;
            System.out.println("刷新数据源成功!");
        }
    }

    private HikariConfig initConfig(DataSourceDO dataSourceDO) {
        HikariConfig hikariConfig=new HikariConfig();
        hikariConfig.setJdbcUrl(dataSourceDO.getUrl());
        hikariConfig.setUsername(dataSourceDO.getUsername());
        hikariConfig.setPassword(dataSourceDO.getPassword());
        hikariConfig.setDriverClassName(dataSourceDO.getDriverClassName());
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        return hikariConfig;
    }

    @Override
    public void close() {
        ((HikariDataSource)this.dataSource).close();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return  this.dataSource.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return this.dataSource.getConnection(username,password);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return this.dataSource.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return this.dataSource.isWrapperFor(iface);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return  this.dataSource.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        this.dataSource.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        this.dataSource.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return this.dataSource.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return this.dataSource.getParentLogger();
    }

    @Override
    public String toString() {
        HikariDataSource dataSource= ((HikariDataSource)this.dataSource);
        return "username:"+dataSource.getUsername()+"\n"+
            "password:"+dataSource.getPassword()+"\n"+
            "jdbcUrl:"+dataSource.getJdbcUrl()+"\n"+
            "driverClassName:"+dataSource.getDriverClassName()+"\n";
    }
}
