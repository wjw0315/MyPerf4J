package cn.MyPerf4J.restart.utils;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.myperf4j.base.util.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public final class ScriptUtil {
    private static final Log log = LogFactory.get();

    private ScriptUtil() {
    }
    /**
     * 执行外部脚本
     * @param scriptPath
     */
    /**
     * 执行脚本
     * @param scriptPath
     */
    public static void executeScript(String scriptPath) {
        if (scriptPath == null || scriptPath.isEmpty()) {
//            System.err.println("No script path provided.");
            log.error("No script path provided.");
            return;
        }

        try {
            // 使用nohup和&让脚本在后台独立运行
            ProcessBuilder pb = new ProcessBuilder("sh", "-c", "nohup " + scriptPath + " &");
            pb.redirectErrorStream(true);
            // 设置工作目录
            pb.directory(new java.io.File(scriptPath).getParentFile());
            Process process = pb.start();

            // 打印脚本输出
//            BufferedReader reader = new BufferedReader(
//                    new InputStreamReader(process.getInputStream()));
//            String line;
//            while ((line = reader.readLine()) != null) {
////                System.out.println(line);
//                log.info(line);
//            }
//
//            int exitCode = process.waitFor();
//            log.info("Script exited with code: " + exitCode);

            // 不等待脚本完成，直接返回
            log.info("Restart script started in background: " + scriptPath);
        } catch (Exception e) {
            log.error("Error executing script: " + e.getMessage());
        }
    }
}
