package suike.suikecherry.config;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class TreasureList {
    public static void config() {
        File configFile = new File(Config.config, "config/sui_ke/cherry/treasure.cfg");

        // 确保目标目录存在
        configFile.getParentFile().mkdirs();

        boolean upConfig = false;

        // 检查配置文件是否存在
        if (!configFile.exists()) {
            copyConfig(configFile);
        }
    }

    private static void copyConfig(File configFile) {
        // 从资源复制默认配置
        try (InputStream input = TreasureList.class.getResourceAsStream("/assets/suikecherry/treasure.cfg")) {
            Files.copy(input, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }
}