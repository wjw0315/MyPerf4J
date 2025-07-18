package cn.MyPerf4J.restart.entity;

import cn.MyPerf4J.restart.commons.enums.HealthStatusEnums;

/**
 * 健康检测状态
 */
public class HealthMonitorStatusEntity {
    /**
     * 服务健康状态
     * @see HealthStatusEnums
     */
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

