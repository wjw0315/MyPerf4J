package cn.MyPerf4J.restart;

import cn.myperf4j.base.config.HealthMonitorConfig;
import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.util.Logger;

import java.lang.instrument.Instrumentation;

public final class OOMAgent {
    private OOMAgent() {
    }

    //    public static String scriptPath = "";
//    public static String healthCheckUrl = "";
//    //ms
//    public static long interval = 5000;
//    public static int failThreshold = 3; // 新增：失败几次后触发脚本
    private static final HealthMonitorConfig healthMonitorConfig = ProfilingConfig.healthMonitorConfig();

    public static void initial(String args, Instrumentation inst) {
//        if (args != null && !args.isEmpty()) {
//            scriptPath = args;
//        }

        // 设置默认的未捕获异常处理器
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            Logger.error("Uncaught exception in thread '" + thread.getName() + "': " + throwable);
//            System.err.println("Uncaught exception in thread '" + thread.getName() + "': " + throwable);
            if (throwable instanceof OutOfMemoryError) {
                Logger.error("JVM OutOfMemoryError detected!");
//                System.err.println("JVM OutOfMemoryError detected!");
//                ScriptUtil.executeScript(scriptPath);
            }
        });

        // 启动内存监控线程
//        new Thread(new OOMMonitor()).start();
        // 启动健康检查线程
        String healthCheckUrl = healthMonitorConfig.getHealthCheckUrl();
        if (healthCheckUrl != null && !healthCheckUrl.isEmpty()) {
            new Thread(new HealthMonitor()).start();
        }
    }
}
