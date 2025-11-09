package org.dideng.com.deicore.command.WorldVar;

import java.util.Objects;

/**
 * 增强的世界变量类，支持多种数据类型
 */
public class WorldVariable {
    private String name;
    private WorldVarType type;
    private String value;
    private String description;
    
    public WorldVariable(String name, WorldVarType type, String value) {
        this(name, type, value, "");
    }
    
    public WorldVariable(String name, WorldVarType type, String value, String description) {
        this.name = name;
        this.type = type;
        this.value = type.formatValue(value);
        this.description = description;
    }
    
    // Getters
    public String getName() {
        return name;
    }
    
    public WorldVarType getType() {
        return type;
    }
    
    public String getValue() {
        return value;
    }
    
    public String getDescription() {
        return description;
    }
    
    // Setters
    public void setValue(String value) {
        this.value = type.formatValue(value);
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * 获取整数值
     */
    public int getIntValue() {
        if (type == WorldVarType.INT) {
            return Integer.parseInt(value);
        } else if (type == WorldVarType.DOUBLE) {
            return (int) Double.parseDouble(value);
        } else if (type == WorldVarType.BOOLEAN) {
            return Boolean.parseBoolean(value) ? 1 : 0;
        }
        throw new IllegalStateException("变量类型 " + type + " 不支持转换为整数");
    }
    
    /**
     * 获取浮点数值
     */
    public double getDoubleValue() {
        if (type == WorldVarType.DOUBLE) {
            return Double.parseDouble(value);
        } else if (type == WorldVarType.INT) {
            return Integer.parseInt(value);
        } else if (type == WorldVarType.BOOLEAN) {
            return Boolean.parseBoolean(value) ? 1.0 : 0.0;
        }
        throw new IllegalStateException("变量类型 " + type + " 不支持转换为浮点数");
    }
    
    /**
     * 获取布尔值
     */
    public boolean getBooleanValue() {
        if (type == WorldVarType.BOOLEAN) {
            return Boolean.parseBoolean(value);
        } else if (type == WorldVarType.INT) {
            return Integer.parseInt(value) != 0;
        } else if (type == WorldVarType.DOUBLE) {
            return Double.parseDouble(value) != 0.0;
        } else if (type == WorldVarType.STRING) {
            String lowerValue = value.toLowerCase();
            return lowerValue.equals("true") || lowerValue.equals("1");
        }
        throw new IllegalStateException("变量类型 " + type + " 不支持转换为布尔值");
    }
    
    /**
     * 设置整数值
     */
    public void setIntValue(int newValue) {
        if (type == WorldVarType.INT || type == WorldVarType.DOUBLE) {
            this.value = String.valueOf(newValue);
        } else {
            throw new IllegalStateException("变量类型 " + type + " 不支持设置整数值");
        }
    }
    
    /**
     * 设置浮点数值
     */
    public void setDoubleValue(double newValue) {
        if (type == WorldVarType.DOUBLE) {
            this.value = String.valueOf(newValue);
        } else if (type == WorldVarType.INT) {
            this.value = String.valueOf((int) newValue);
        } else {
            throw new IllegalStateException("变量类型 " + type + " 不支持设置浮点数值");
        }
    }
    
    /**
     * 设置布尔值
     */
    public void setBooleanValue(boolean newValue) {
        if (type == WorldVarType.BOOLEAN) {
            this.value = String.valueOf(newValue);
        } else {
            throw new IllegalStateException("变量类型 " + type + " 不支持设置布尔值");
        }
    }
    
    /**
     * 累加整数值
     */
    public void addIntValue(int addValue) {
        if (type == WorldVarType.INT) {
            int current = getIntValue();
            setIntValue(current + addValue);
        } else if (type == WorldVarType.DOUBLE) {
            double current = getDoubleValue();
            setDoubleValue(current + addValue);
        } else {
            throw new IllegalStateException("变量类型 " + type + " 不支持累加操作");
        }
    }
    
    /**
     * 减少整数值
     */
    public void subtractIntValue(int subtractValue) {
        addIntValue(-subtractValue);
    }
    
    /**
     * 转换为Map格式（用于兼容旧系统）
     */
    public java.util.Map<String, String> toMap() {
        java.util.Map<String, String> map = new java.util.HashMap<>();
        map.put("VarName", name);
        map.put("Var", value);
        map.put("Type", type.getTypeName());
        if (description != null && !description.isEmpty()) {
            map.put("Description", description);
        }
        return map;
    }
    
    /**
     * 从Map创建WorldVariable
     */
    public static WorldVariable fromMap(java.util.Map<String, String> map) {
        String name = map.get("VarName");
        String value = map.get("Var");
        String typeStr = map.get("Type");
        String description = map.get("Description");
        
        WorldVarType type;
        if (typeStr != null) {
            type = WorldVarType.fromString(typeStr);
        } else {
            // 兼容旧版本：默认为整数类型
            type = WorldVarType.INT;
        }
        
        if (type == null) {
            type = WorldVarType.INT; // 默认类型
        }
        
        return new WorldVariable(name, type, value, description);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        WorldVariable that = (WorldVariable) obj;
        return Objects.equals(name, that.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
    
    @Override
    public String toString() {
        return String.format("WorldVariable{name='%s', type=%s, value='%s', description='%s'}",
                name, type, value, description);
    }
}