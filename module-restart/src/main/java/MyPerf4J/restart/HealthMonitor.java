package MyPerf4J.restart;

import MyPerf4J.restart.utils.ScriptUtil;
import cn.myperf4j.base.config.HealthMonitorConfig;
import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.http.HttpRequest;
import cn.myperf4j.base.http.HttpResponse;
import cn.myperf4j.base.http.client.HttpClient;
import cn.myperf4j.base.util.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static cn.myperf4j.base.http.HttpStatusClass.SUCCESS;

public class HealthMonitor implements Runnable {

    private final HttpClient httpClient =  new HttpClient.Builder()
            .connectTimeout(3000)
            .readTimeout(5000)
            .build();

    private int failureCount = 0;

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
//                    System.out.println("Health check succeeded.");
                    Logger.info("Health check succeeded.");
                    failureCount = 0; // 成功则重置计数器
                } else {
                    failureCount++;
//                    System.err.println("Health check failed with code: " + responseCode + ", failure count: " + failureCount);
                    Logger.error("Health check failed with code: " + response.getStatus().statusClass() + ", failure count: " + failureCount);
                    maybeTriggerScript();
                }
            } catch (Exception e) {
                failureCount++;
//                System.err.println("Health check failed: " + e.getMessage() + ", failure count: " + failureCount);
                Logger.error("Health check failed: " + e.getMessage() + ", failure count: " + failureCount);
                maybeTriggerScript();
            }
        }, 0, healthMonitorConfig.getHealthCheckInterval(), TimeUnit.MILLISECONDS);
    }
    private void maybeTriggerScript() {
        if (failureCount >= healthMonitorConfig.getHealthCheckFailThreshold()) {
//            System.err.println("Failure threshold reached (" + failureCount + "/" + OOMAgent.failThreshold + "). Executing script...");
            Logger.info("Failure threshold reached (" + failureCount + "/" + healthMonitorConfig.getHealthCheckFailThreshold() + "). Executing script...");
            ScriptUtil.executeScript(healthMonitorConfig.getRestartScriptPath());
            failureCount = 0; // 执行脚本后重置计数器
        }
    }

}