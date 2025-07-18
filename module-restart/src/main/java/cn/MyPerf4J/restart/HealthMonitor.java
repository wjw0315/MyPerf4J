package cn.MyPerf4J.restart;

import cn.MyPerf4J.restart.commons.enums.HealthStatusEnums;
import cn.MyPerf4J.restart.entity.HealthMonitorStatusEntity;
import cn.MyPerf4J.restart.utils.ScriptUtil;
import cn.hutool.json.JSONUtil;
import cn.myperf4j.base.config.HealthMonitorConfig;
import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.http.HttpRequest;
import cn.myperf4j.base.http.HttpResponse;
import cn.myperf4j.base.http.client.HttpClient;
import cn.myperf4j.base.util.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static cn.myperf4j.base.http.HttpStatusClass.SUCCESS;

public class HealthMonitor implements Runnable {
    private int failureCount;
    private final HttpClient httpClient =  new HttpClient.Builder().connectTimeout(3000).readTimeout(5000).build();
    private static final HealthMonitorConfig healthMonitorConfig = ProfilingConfig.healthMonitorConfig();
    /**
     * 健康检测
     */
    @Override
    public void run() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                String healthCheckUrl = healthMonitorConfig.getHealthCheckUrl();
//                URL url = new URL(healthCheckUrl);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setRequestMethod("GET");
//                connection.setConnectTimeout(5000);
//                connection.setReadTimeout(5000);
//
//                int responseCode = connection.getResponseCode();

                final HttpRequest req = new HttpRequest.Builder()
                        .url(healthCheckUrl)
                        .get()
                        .build();
                final HttpResponse response = httpClient.execute(req);

                if (response.getStatus().statusClass() == SUCCESS) {
                    //校验状态
                    HealthMonitorStatusEntity healthMonitorStatusEntity = JSONUtil.toBean(response.getBodyString()
                            , HealthMonitorStatusEntity.class);
                    if (HealthStatusEnums.UP.getStatus().equals(healthMonitorStatusEntity.getStatus())) {
                        Logger.info("Health check succeeded.");
                        failureCount = 0; // 成功则重置计数器
                    } else {
                        Logger.error("Health check failed. status: "
                                + healthMonitorStatusEntity.getStatus());
                        failureCount++;
                        maybeTriggerScript();
                    }
                } else {
                    Logger.error("Health check failed with code: "
                            + response.getStatus().statusClass()
                            + ", failure count: " + failureCount);
                }
            } catch (Exception e) {
                Logger.error("Health check failed: "
                        + e.getMessage() + ", failure count: "
                        + failureCount);
//                maybeTriggerScript();
            }
        }, 0, healthMonitorConfig.getHealthCheckInterval(), TimeUnit.MILLISECONDS);
    }

    /**
     * 执行脚本
     */
    private void maybeTriggerScript() {
        if (failureCount >= healthMonitorConfig.getHealthCheckFailThreshold()) {
            Logger.info("Failure threshold reached (" + failureCount + "/"
                    + healthMonitorConfig.getHealthCheckFailThreshold() + "). Executing script...");
            ScriptUtil.executeScript(healthMonitorConfig.getRestartScriptPath());
            failureCount = 0; // 执行脚本后重置计数器
        }
    }

}
