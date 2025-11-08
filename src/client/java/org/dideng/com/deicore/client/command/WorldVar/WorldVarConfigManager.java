package org.dideng.com.deicore.client.command.WorldVar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.dideng.com.deicore.Deicore;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WorldVar配置管理器 - 简单实用的JSON存储
 */
public class WorldVarConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_FILE = Paths.get("config/deicore/world_vars.json");
    
    /**
     * 保存WorldVar列表到文件
     */
    public static void saveWorldVars() {
        try {
            // 确保目录存在
            Files.createDirectories(CONFIG_FILE.getParent());
            
            // 转换为简单的数据结构
            List<Map<String, String>> worldVars = DeicoreWorldVar.getWorldVarList();
            
            // 保存到文件
            String json = GSON.toJson(worldVars);
            Files.writeString(CONFIG_FILE, json);
            
            Deicore.LOGGER.info("WorldVar配置已保存到: {}", CONFIG_FILE);
        } catch (IOException e) {
            Deicore.LOGGER.error("保存WorldVar配置失败: {}", e.getMessage());
        }
    }
    
    /**
     * 从文件加载WorldVar列表
     */
    public static void loadWorldVars() {
        try {
            if (!Files.exists(CONFIG_FILE)) {
                Deicore.LOGGER.info("WorldVar配置文件不存在，使用默认空列表");
                return;
            }
            
            String json = Files.readString(CONFIG_FILE);
            Type listType = new TypeToken<List<Map<String, String>>>(){}.getType();
            List<Map<String, String>> loadedVars = GSON.fromJson(json, listType);
            
            if (loadedVars != null) {
                // 清空现有列表并添加加载的数据
                DeicoreWorldVar.getWorldVarList().clear();
                DeicoreWorldVar.getWorldVarList().addAll(loadedVars);
                Deicore.LOGGER.info("已加载 {} 个WorldVar", loadedVars.size());
            }
        } catch (IOException e) {
            Deicore.LOGGER.error("加载WorldVar配置失败: {}", e.getMessage());
        }
    }
    
    /**
     * 获取配置文件路径
     */
    public static String getConfigFilePath() {
        return CONFIG_FILE.toString();
    }
}