package suike.suikecherry.expand.exnihilocreatio;

import java.io.*;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import suike.suikecherry.config.Config;

public class ExNihiloCreatioExpand {
    public static void expand() {
        File cfgFile = new File(Config.无中生有configFile, "ExNihiloCreatio.cfg");

        if (cfgFile.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(cfgFile));
                StringBuilder content = new StringBuilder();
                String line;
                boolean modified = false;

                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("        B:enableJSONLoading=false")) {
                        line = "        B:enableJSONLoading=true";
                        modified = true;
                    }
                    content.append(line).append(System.lineSeparator());
                }
                reader.close();

                if (modified) {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(cfgFile));
                    writer.write(content.toString());
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //不存在-复制新文件
            try (InputStream input = ExNihiloCreatioExpand.class.getResourceAsStream("/assets/suikecherry/expand/exnihilocreatio/ExNihiloCreatio.cfg")) {
                Files.copy(input, cfgFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File sieveRegistryFile = new File(Config.无中生有configFile, "SieveRegistry.json");

        if (sieveRegistryFile.exists()) {
            try {
                // 读取文件内容
                String content = new String(Files.readAllBytes(sieveRegistryFile.toPath()));

                // 找到 "ore:dirt": [ 的位置
                String insertLine = "    { \"drop\": \"suikecherry:cherry_seed\", \"chance\": 0.05, \"meshLevel\": 1 },";
                String target = "\"ore:dirt\": [";

                // 检查是否存在
                if (!content.contains(insertLine)) {
                    // 插入新行
                    int index = content.indexOf(target);
                    if (index != -1) {
                        index += target.length();
                        String newContent = content.substring(0, index) + "\n" + insertLine + content.substring(index);

                        // 写回文件
                        Files.write(sieveRegistryFile.toPath(), newContent.getBytes());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //不存在-复制新文件
            try (InputStream input = ExNihiloCreatioExpand.class.getResourceAsStream("/assets/suikecherry/expand/exnihilocreatio/SieveRegistry.json")) {
                Files.copy(input, sieveRegistryFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}