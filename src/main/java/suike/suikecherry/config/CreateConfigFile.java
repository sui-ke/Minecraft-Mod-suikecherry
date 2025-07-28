package suike.suikecherry.config;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.charset.StandardCharsets;

import suike.suikecherry.SuiKe;

import net.minecraftforge.common.config.Configuration;

public class CreateConfigFile {

    public static int oldConfigVersion = -1;

    public static void config() {
        File configFile = Config.configFile;
        File configFileCopy = Config.configFileCopy;

        boolean haveConfig = configFile.exists();
        boolean needUpdate = false;

        Configuration config = new Configuration(configFile);

        if (haveConfig) {
            config.load();
            //获取配置文件版本
            int configVersion = (int) config.get("Cherry", "configVersion", 0).getDouble();

            if (configVersion != ConfigValue.modsConfigVersion) {
                oldConfigVersion = configVersion;
                config.save();//加载配置
                needUpdate = true;
                //备份旧配置文件
                try {
                    Files.copy(configFile.toPath(), configFileCopy.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //删除旧配置文件
                if (haveConfig) {
                    configFile.delete();
                }
            }
        }

        if (!haveConfig || needUpdate) {
            // 配置文件名称
            String configName = "sui_ke_zh.cfg";
            if (!SuiKe.isZhCn) {
                configName = "sui_ke_en.cfg";
            }

            //从资源路径中复制默认配置文件
            try (InputStream input = CreateConfigFile.class.getResourceAsStream("/assets/suikecherry/" + configName)) {
                Files.copy(input, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (needUpdate) {
            //读取备份文件内容
            String backupConfigContent;
            try {
                backupConfigContent = new String(Files.readAllBytes(configFileCopy.toPath()), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            //更新新配置文件中的值
            try {
                String newConfigContent = new String(Files.readAllBytes(configFile.toPath()), StandardCharsets.UTF_8);
                List<String> configKeys = getConfigKeys(newConfigContent);

                for (String key : configKeys) {
                    if (key.equals("D:configVersion")) {
                        continue;
                    }

                    newConfigContent = updateConfigValue(newConfigContent, backupConfigContent, key);
                }

                Files.write(configFile.toPath(), newConfigContent.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }

            //删除备份的配置文件
            configFileCopy.delete();
        }
    }

    public static List<String> getConfigKeys(String configContent) {
        List<String> keys = new ArrayList<>();
        String[] lines = configContent.split("\n");
        for (String line : lines) {
            if (line.contains("=")) {
                String key = line.split("=")[0].trim();
                keys.add(key);
            }
        }
        return keys;
    }

    public static String updateConfigValue(String newConfigContent, String backupConfigContent, String key) {
        String backupValue = getValueFromBackup(backupConfigContent, key);
        if (backupValue.isEmpty()) {
            //保持设置默认值
            return newConfigContent;
        } else {
            //使用固定字符串替换
            String oldConfigLine = getConfigLine(newConfigContent, key);
            String newConfigLine = key + "=" + backupValue;
            return newConfigContent.replace(oldConfigLine, newConfigLine);
        }
    }

    private static String getValueFromBackup(String content, String key) {
        int startIndex = content.indexOf(key + "=");
        if (startIndex == -1) {
            return "";
        }
        startIndex += key.length() + 1;
        int endIndex = content.indexOf("\n", startIndex);
        if (endIndex == -1) endIndex = content.length();
        return content.substring(startIndex, endIndex).trim();
    }

    private static String getConfigLine(String content, String key) {
        int startIndex = content.indexOf(key + "=");
        if (startIndex == -1) return "";
        int endIndex = content.indexOf("\n", startIndex);
        if (endIndex == -1) endIndex = content.length();
        return content.substring(startIndex, endIndex).trim();
    }

    public static void upConfigValue(String key, String value) {
        Path configFile = Paths.get(Config.configFile.toURI()); // 配置文件路径
        String newValue = "    " + key + "=" + value;           // 新的值

        try {
            // 读取文件内容
            List<String> lines = Files.readAllLines(configFile);

            // 修改指定键的值
            List<String> updatedLines = new ArrayList<>();
            boolean keyFound = false;
            for (String line : lines) {
                if (line.trim().startsWith(key)) {
                    updatedLines.add(newValue); // 替换为新值
                    keyFound = true;
                } else {
                    updatedLines.add(line); // 保留其他行
                }
            }

            // 写回文件
            Files.write(configFile, updatedLines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}