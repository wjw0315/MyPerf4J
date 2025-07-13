package MyPerf4J.restart;

import MyPerf4J.restart.utils.ScriptUtil;
import cn.myperf4j.base.util.Logger;

import java.lang.management.ManagementFactory;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OOMMonitor implements Runnable {

    private static final long MEMORY_THRESHOLD_PERCENT = 95; // 百分比
    private static final long CHECK_INTERVAL_MS = 5000;

    @Override
    public void run() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            double usagePercent = ((double) usedMemory / (double) runtime.maxMemory()) * 100;

            Logger.info("Memory usage: " + usagePercent);
//            System.out.printf("Heap memory usage: %.2f%%\n", usagePercent);

            if (usagePercent > MEMORY_THRESHOLD_PERCENT) {
                Logger.error("Memory threshold exceeded! Executing script...");
//                System.err.println("Memory threshold exceeded! Executing script...");
                ScriptUtil.executeScript(OOMAgent.scriptPath);
            }
        }, 0, CHECK_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }


}
