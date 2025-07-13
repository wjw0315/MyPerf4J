package MyPerf4J.restart.utils;

import cn.myperf4j.base.util.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ScriptUtil {
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
            Logger.error("No script path provided.");
            return;
        }

        try {
            ProcessBuilder pb = new ProcessBuilder(scriptPath);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // 打印脚本输出
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
                Logger.info(line);
            }

            int exitCode = process.waitFor();
//            System.out.println("Script exited with code: " + exitCode);
            Logger.info("Script exited with code: " + exitCode);
        } catch (Exception e) {
//            e.printStackTrace();
            Logger.error("Error executing script: " + e.getMessage());
        }
    }
}
