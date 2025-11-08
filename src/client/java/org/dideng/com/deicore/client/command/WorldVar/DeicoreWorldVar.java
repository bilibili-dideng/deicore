package org.dideng.com.deicore.client.command.WorldVar;

import org.dideng.com.deicore.Deicore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DeicoreWorldVar {
    public static List<Map<String, String>> worldVarList = new ArrayList<>();

    public static List<Map<String, String>> getWorldVarList() {
        return worldVarList;
    }

    public static void addWorldVar(Map<String, String> worldVar) {
        worldVarList.add(worldVar);
        WorldVarConfigManager.saveWorldVars(); // 自动保存
    }

    /**
     * 获取变量值
     * @param varName 变量名
     * @return 成功返回变量值（int）；变量不存在返回"NOT_FOUND"；格式错误返回"FORMAT_ERROR"
     */
    public static Object getWorldVar(String varName) {
        for (Map<String, String> var : worldVarList) {
            if (Objects.equals(var.get("VarName"), varName)) {
                try {
                    return Integer.parseInt(var.get("Var")); // 找到且格式正确，返回值
                } catch (NumberFormatException | NullPointerException e) {
                    Deicore.LOGGER.error("变量{}的值格式错误：{}", varName, e.getMessage());
                    return "FORMAT_ERROR";
                }
            }
        }
        return "NOT_FOUND";
    }

    /**
     * 累加变量值
     * @param varName 变量名
     * @param add 累加值
     * @return 成功返回null；变量不存在返回"NOT_FOUND"；格式错误返回"FORMAT_ERROR"
     */
    public static Object addWorldVar(String varName, int add) {
        for (Map<String, String> var : worldVarList) {
            if (Objects.equals(var.get("VarName"), varName)) {
                String currentVarStr = var.get("Var");
                if (currentVarStr == null) {
                    var.put("Var", String.valueOf(add));
                    WorldVarConfigManager.saveWorldVars(); // 自动保存
                    return null; // 成功（空值默认从0累加）
                }
                try {
                    int currentVar = Integer.parseInt(currentVarStr);
                    var.put("Var", String.valueOf(currentVar + add));
                    WorldVarConfigManager.saveWorldVars(); // 自动保存
                    return null; // 成功
                } catch (NumberFormatException e) {
                    Deicore.LOGGER.error("变量{}的值格式错误，无法累加：{}", varName, currentVarStr);
                    return "FORMAT_ERROR";
                }
            }
        }
        return "NOT_FOUND";
    }

    /**
     * 修改变量值
     * @param varName 变量名
     * @param newValue 新值
     * @return 成功返回null；变量不存在返回"NOT_FOUND"
     */
    public static Object setWorldVar(String varName, int newValue) {
        for (Map<String, String> var : worldVarList) {
            if (Objects.equals(var.get("VarName"), varName)) {
                var.put("Var", String.valueOf(newValue));
                WorldVarConfigManager.saveWorldVars(); // 自动保存
                return null; // 找到并修改成功
            }
        }
        Deicore.LOGGER.warn("未找到变量：{}，修改失败", varName);
        return "NOT_FOUND"; // 变量不存在
    }

    /**
     * 删除变量
     * @param varName 变量名
     * @return 成功返回null；变量不存在返回"NOT_FOUND"
     */
    public static Object removeWorldVar(String varName) {
        Iterator<Map<String, String>> iterator = worldVarList.iterator();
        while (iterator.hasNext()) {
            Map<String, String> var = iterator.next();
            if (Objects.equals(var.get("VarName"), varName)) {
                iterator.remove();
                WorldVarConfigManager.saveWorldVars(); // 自动保存
                return null;
            }
        }
        Deicore.LOGGER.warn("未找到变量：{}，删除失败", varName);
        return "NOT_FOUND";
    }

    /**
     * 减少变量值
     * @param varName 变量名
     * @param subtract 减少值
     * @return 成功返回null；变量不存在返回"NOT_FOUND"；格式错误返回"FORMAT_ERROR"
     */
    public static Object subtractWorldVar(String varName, int subtract) {
        for (Map<String, String> var : worldVarList) {
            if (Objects.equals(var.get("VarName"), varName)) {
                String currentVarStr = var.get("Var");
                if (currentVarStr == null) {
                    var.put("Var", String.valueOf(-subtract));
                    WorldVarConfigManager.saveWorldVars(); // 自动保存
                    return null;
                }
                try {
                    int currentVar = Integer.parseInt(currentVarStr);
                    var.put("Var", String.valueOf(currentVar - subtract));
                    WorldVarConfigManager.saveWorldVars(); // 自动保存
                    return null; // 成功
                } catch (NumberFormatException e) {
                    Deicore.LOGGER.error("变量{}的值格式错误，无法减少：{}", varName, currentVarStr);
                    return "FORMAT_ERROR";
                }
            }
        }
        return "NOT_FOUND";
    }
}