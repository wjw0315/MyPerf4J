package cn.myperf4j.base.config;

import static cn.myperf4j.base.config.MyProperties.getStr;
import static cn.myperf4j.base.constant.PropertyKeys.HealthMonitor.*;

public class HealthMonitorConfig {
    /**
     * 重启脚本路径
     */
    private String restartScriptPath ;
    /**
     * health.check.url
     */
    private String healthCheckUrl;
    /**
     * health.check.fail.threshold
     */
    private Integer healthCheckFailThreshold ;

    private Long healthCheckInterval;

    public static HealthMonitorConfig loadHealthMonitorConfig() {
        HealthMonitorConfig config = new HealthMonitorConfig();
        config.restartScriptPath = MyProperties.getStr(RESTART_SCRIPT_PATH, "./restart.sh");
        config.healthCheckUrl = MyProperties.getStr(HEALTH_CHECK_URL, "http://127.0.0.1:8080/actuator/health");
        config.healthCheckFailThreshold = MyProperties.getInt(HEALTH_CHECK_FAIL_THRESHOLD, 3);
        config.healthCheckInterval = MyProperties.getLong(HEALTH_CHECK_INTERVAL, 5000);
        return config;
    }

    public String getRestartScriptPath() {
        return restartScriptPath;
    }

    public void setRestartScriptPath(String restartScriptPath) {
        this.restartScriptPath = restartScriptPath;
    }

    public String getHealthCheckUrl() {
        return healthCheckUrl;
    }

    public void setHealthCheckUrl(String healthCheckUrl) {
        this.healthCheckUrl = healthCheckUrl;
    }

    public int getHealthCheckFailThreshold() {
        return healthCheckFailThreshold;
    }

    public void setHealthCheckFailThreshold(int healthCheckFailThreshold) {
        this.healthCheckFailThreshold = healthCheckFailThreshold;
    }

    public Long getHealthCheckInterval() {
        return healthCheckInterval;
    }

    public void setHealthCheckInterval(Long healthCheckInterval) {
        this.healthCheckInterval = healthCheckInterval;
    }
}
