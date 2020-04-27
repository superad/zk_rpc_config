package com.mengma.config.center.config;

/**
 * @author fgm
 * @description  数据源
 * @date 2020-04-25
 ***/
public class DataSourceDO {

    private String url;
    private String password;
    private String username;
    private String driverClassName;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public static DataSourceDO builder(){
        return new DataSourceDO();
    }

    public DataSourceDO url(String url) {
        this.url = url;
        return this;
    }

    public DataSourceDO password(String password) {
        this.password = password;
        return this;
    }

    public DataSourceDO username(String username) {
        this.username = username;
        return this;
    }

    public DataSourceDO driverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
        return this;
    }

}
