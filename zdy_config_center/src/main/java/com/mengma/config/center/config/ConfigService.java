package com.mengma.config.center.config;

/**
 * @author fgm
 * @description  配置服务
 * @date 2020-04-25
 ***/
public interface ConfigService {

    /**
     * 获取数据源配置
     * @return
     */
    DataSourceDO getConfig();

}
