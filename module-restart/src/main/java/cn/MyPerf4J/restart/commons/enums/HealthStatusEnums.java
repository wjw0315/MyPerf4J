package cn.MyPerf4J.restart.commons.enums;

/**
 *
 */
public enum HealthStatusEnums {
    UP("UP", "在线"),
    DOWN("DOWN", "下线"),
    Null;
    private String status;
    private String desc;

    HealthStatusEnums() {
    }

    HealthStatusEnums(String status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static HealthStatusEnums valueOfStatus(String status) {
        for (HealthStatusEnums obj : HealthStatusEnums.values()) {
            if (java.util.Objects.equals(obj.status, status)) {
                return obj;
            }
        }
        return Null;
    }

    public static HealthStatusEnums valueOfDesc(String desc) {
        for (HealthStatusEnums obj : HealthStatusEnums.values()) {
            if (java.util.Objects.equals(obj.desc, desc)) {
                return obj;
            }
        }
        return Null;
    }

    public String getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
