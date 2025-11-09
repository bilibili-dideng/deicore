package org.dideng.com.deicore.command.WorldVar;

/**
 * 世界变量类型枚举
 * 支持多种数据类型：整数、浮点数、字符串、布尔值
 */
public enum WorldVarType {
    INT("int", "整数类型"),
    DOUBLE("double", "浮点数类型"),
    STRING("string", "字符串类型"),
    BOOLEAN("boolean", "布尔值类型");
    
    private final String typeName;
    private final String description;
    
    WorldVarType(String typeName, String description) {
        this.typeName = typeName;
        this.description = description;
    }
    
    public String getTypeName() {
        return typeName;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据类型名称获取枚举
     */
    public static WorldVarType fromString(String typeName) {
        for (WorldVarType type : values()) {
            if (type.typeName.equalsIgnoreCase(typeName)) {
                return type;
            }
        }
        return null;
    }
    
    /**
     * 验证值是否符合类型要求
     */
    public boolean isValidValue(String value) {
        if (value == null) return false;
        
        try {
            switch (this) {
                case INT:
                    Integer.parseInt(value);
                    return true;
                case DOUBLE:
                    Double.parseDouble(value);
                    return true;
                case BOOLEAN:
                    String lowerValue = value.toLowerCase();
                    return lowerValue.equals("true") || lowerValue.equals("false") || 
                           lowerValue.equals("1") || lowerValue.equals("0");
                case STRING:
                    return true; // 字符串类型接受任何值
                default:
                    return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * 格式化值为标准格式
     */
    public String formatValue(String value) {
        if (value == null) return null;
        
        switch (this) {
            case BOOLEAN:
                String lowerValue = value.toLowerCase();
                if (lowerValue.equals("true") || lowerValue.equals("1")) {
                    return "true";
                } else if (lowerValue.equals("false") || lowerValue.equals("0")) {
                    return "false";
                }
                return value; // 保持原值
            default:
                return value;
        }
    }
    
    /**
     * 获取所有支持的类型名称
     */
    public static String[] getAllTypeNames() {
        WorldVarType[] types = values();
        String[] names = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            names[i] = types[i].getTypeName();
        }
        return names;
    }
}