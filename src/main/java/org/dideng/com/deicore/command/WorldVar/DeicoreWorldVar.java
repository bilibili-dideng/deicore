package org.dideng.com.deicore.command.WorldVar;

import org.dideng.com.deicore.Deicore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DeicoreWorldVar {
    // 多类型变量列表
    private static List<WorldVariable> typedWorldVars = new ArrayList<>();

    public static List<WorldVariable> getTypedWorldVars() {
        return typedWorldVars;
    }
    
    /**
     * 添加多类型变量
     */
    public static void addTypedWorldVar(WorldVariable worldVar) {
        // 检查变量名是否已存在
        WorldVariable existing = getTypedWorldVar(worldVar.getName());
        if (existing != null) {
            throw new IllegalArgumentException("变量名 '" + worldVar.getName() + "' 已存在");
        }
        
        typedWorldVars.add(worldVar);
        WorldVarConfigManager.saveWorldVars(); // 自动保存
    }


    
    /**
     * 获取多类型变量
     */
    public static WorldVariable getTypedWorldVar(String varName) {
        for (WorldVariable var : typedWorldVars) {
            if (Objects.equals(var.getName(), varName)) {
                return var;
            }
        }
        return null;
    }




    
    /**
     * 设置多类型变量值
     */
    public static Object setTypedWorldVar(String varName, String newValue) {
        WorldVariable typedVar = getTypedWorldVar(varName);
        if (typedVar != null) {
            if (typedVar.getType().isValidValue(newValue)) {
                typedVar.setValue(newValue);
                WorldVarConfigManager.saveWorldVars();
                return null;
            } else {
                return "FORMAT_ERROR";
            }
        }
        return "NOT_FOUND";
    }

    /**
     * 删除多类型变量
     */
    public static Object removeWorldVar(String varName) {
        WorldVariable typedVar = getTypedWorldVar(varName);
        if (typedVar != null) {
            typedWorldVars.remove(typedVar);
            WorldVarConfigManager.saveWorldVars();
            return null;
        }
        return "NOT_FOUND";
    }

    /**
     * 增加多类型变量值（仅适用于数值类型）
     */
    public static Object addWorldVar(String varName, int add) {
        WorldVariable typedVar = getTypedWorldVar(varName);
        if (typedVar != null) {
            if (typedVar.getType() == WorldVarType.INT) {
                try {
                    int currentValue = Integer.parseInt(typedVar.getValue());
                    int newValue = currentValue + add;
                    typedVar.setValue(String.valueOf(newValue));
                    WorldVarConfigManager.saveWorldVars();
                    return null;
                } catch (NumberFormatException e) {
                    return "FORMAT_ERROR";
                }
            } else if (typedVar.getType() == WorldVarType.DOUBLE) {
                try {
                    double currentValue = Double.parseDouble(typedVar.getValue());
                    double newValue = currentValue + add;
                    typedVar.setValue(String.valueOf(newValue));
                    WorldVarConfigManager.saveWorldVars();
                    return null;
                } catch (NumberFormatException e) {
                    return "FORMAT_ERROR";
                }
            } else {
                return "FORMAT_ERROR";
            }
        }
        return "NOT_FOUND";
    }

    /**
     * 减少多类型变量值（仅适用于数值类型）
     */
    public static Object subtractWorldVar(String varName, int subtract) {
        WorldVariable typedVar = getTypedWorldVar(varName);
        if (typedVar != null) {
            if (typedVar.getType() == WorldVarType.INT) {
                try {
                    int currentValue = Integer.parseInt(typedVar.getValue());
                    int newValue = currentValue - subtract;
                    typedVar.setValue(String.valueOf(newValue));
                    WorldVarConfigManager.saveWorldVars();
                    return null;
                } catch (NumberFormatException e) {
                    return "FORMAT_ERROR";
                }
            } else if (typedVar.getType() == WorldVarType.DOUBLE) {
                try {
                    double currentValue = Double.parseDouble(typedVar.getValue());
                    double newValue = currentValue - subtract;
                    typedVar.setValue(String.valueOf(newValue));
                    WorldVarConfigManager.saveWorldVars();
                    return null;
                } catch (NumberFormatException e) {
                    return "FORMAT_ERROR";
                }
            } else {
                return "FORMAT_ERROR";
            }
        }
        return "NOT_FOUND";
    }

}