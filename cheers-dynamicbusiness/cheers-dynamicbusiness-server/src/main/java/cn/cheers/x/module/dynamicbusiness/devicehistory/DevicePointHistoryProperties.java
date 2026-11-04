package cn.cheers.x.module.dynamicbusiness.devicehistory;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 历史库连接。采样只走这里。
 * 不管业务库。不把采样写进设备行。
 */
@ConfigurationProperties(prefix = "device-history")
public class DevicePointHistoryProperties {

    /** 没配连接时为 false。查询会说明历史库未启用，不返回空列表假装没有读数。 */
    private boolean enabled;

    private String jdbcUrl = "";

    private String username = "";

    private String password = "";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isReady() {
        return enabled
                && jdbcUrl != null && !jdbcUrl.isBlank()
                && username != null && !username.isBlank()
                && password != null && !password.isBlank();
    }
}
