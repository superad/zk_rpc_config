package com.mengma.config.center.datasource;

import com.mengma.config.center.config.ConfigService;
import com.mengma.config.center.config.DataSourceDO;
import com.mengma.config.center.constants.ConfigConstants;
import com.zaxxer.hikari.HikariConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author fgm
 * @description  数据源配置
 * @date 2020-04-25
 ***/
@Configuration
public class DataSourceConfig {


    @Autowired
    private ConfigService configService;


    /**
     * 配置数据源
     * @return
     */
    @Bean(name = ConfigConstants.DATA_SOURCE_BEAN_NAME)
    public DataSource dataSource(){
        //获取数据源配置
        DataSourceDO dataSourceDO=configService.getConfig();
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dataSourceDO.getUrl());
        config.setUsername(dataSourceDO.getUsername());
        config.setPassword(dataSourceDO.getPassword());
        config.setDriverClassName(dataSourceDO.getDriverClassName());
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        //设置自定义数据源
        MyDataSource myDataSource=new MyDataSource(config);
        return myDataSource;
    }



}
