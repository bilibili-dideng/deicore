package org.dideng.com.deicore.command.WorldVar;

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
 * WorldVar配置管理器 - 支持多类型变量的JSON存储
 */
public class WorldVarConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_FILE = Paths.get("config/deicore/world_vars.json");
    
    /**
     * 保存WorldVar列表到文件（简化格式）
     */
    public static void saveWorldVars() {
        try {
            // 确保目录存在
            Files.createDirectories(CONFIG_FILE.getParent());
            
            // 保存多类型变量（简化格式，直接保存数组）
            List<WorldVariable> typedVars = DeicoreWorldVar.getTypedWorldVars();
            List<Map<String, Object>> typedVarsData = new ArrayList<>();
            
            for (WorldVariable var : typedVars) {
                Map<String, Object> varData = new HashMap<>();
                varData.put("name", var.getName());
                varData.put("type", var.getType().name());
                varData.put("value", var.getValue());
                typedVarsData.add(varData);
            }
            
            // 保存到文件（直接保存数组格式）
            String json = GSON.toJson(typedVarsData);
            Files.writeString(CONFIG_FILE, json);
            
            Deicore.LOGGER.info("WorldVar配置已保存到: {} (包含{}个变量)", 
                CONFIG_FILE, typedVars.size());
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
            
            // 解析为多类型变量数组格式
            Type typedListType = new TypeToken<List<Map<String, Object>>>(){}.getType();
            List<Map<String, Object>> typedVarsData = GSON.fromJson(json, typedListType);
            
            if (typedVarsData != null) {
                DeicoreWorldVar.getTypedWorldVars().clear();
                
                for (Map<String, Object> varData : typedVarsData) {
                    try {
                        String name = (String) varData.get("name");
                        String typeStr = (String) varData.get("type");
                        String value = (String) varData.get("value");
                        
                        WorldVarType type = WorldVarType.valueOf(typeStr);
                        WorldVariable worldVar = new WorldVariable(name, type, value);
                        DeicoreWorldVar.getTypedWorldVars().add(worldVar);
                    } catch (Exception e) {
                        Deicore.LOGGER.warn("加载变量失败: {}", e.getMessage());
                    }
                }
                
                Deicore.LOGGER.info("已加载 {} 个WorldVar", 
                    DeicoreWorldVar.getTypedWorldVars().size());
            } else {
                Deicore.LOGGER.info("WorldVar配置文件为空，使用默认空列表");
            }
        } catch (Exception e) {
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