package MyPerf4J.restart;

import MyPerf4J.restart.utils.ScriptUtil;
import cn.myperf4j.base.util.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HealthMonitor implements Runnable {

    private int failureCount = 0;

    /**
     * 健康检测
     */
    @Override
    public void run() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                URL url = new URL(OOMAgent.healthCheckUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    System.out.println("Health check succeeded.");
                    failureCount = 0; // 成功则重置计数器
                } else {
                    failureCount++;
//                    System.err.println("Health check failed with code: " + responseCode + ", failure count: " + failureCount);
                    Logger.error("Health check failed with code: " + responseCode + ", failure count: " + failureCount);
                    maybeTriggerScript();
                }
            } catch (Exception e) {
                failureCount++;
//                System.err.println("Health check failed: " + e.getMessage() + ", failure count: " + failureCount);
                Logger.error("Health check failed: " + e.getMessage() + ", failure count: " + failureCount);
                maybeTriggerScript();
            }
        }, 0, OOMAgent.interval, TimeUnit.MILLISECONDS);
    }
    private void maybeTriggerScript() {
        if (failureCount >= OOMAgent.failThreshold) {
//            System.err.println("Failure threshold reached (" + failureCount + "/" + OOMAgent.failThreshold + "). Executing script...");
            Logger.info("Failure threshold reached (" + failureCount + "/" + OOMAgent.failThreshold + "). Executing script...");
            ScriptUtil.executeScript(OOMAgent.scriptPath);
            failureCount = 0; // 执行脚本后重置计数器
        }
    }

}