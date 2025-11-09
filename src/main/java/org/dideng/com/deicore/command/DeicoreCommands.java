package org.dideng.com.deicore.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.dideng.com.deicore.command.WorldVar.DeicoreWorldVar;
import org.dideng.com.deicore.command.WorldVar.WorldVarType;
import org.dideng.com.deicore.command.WorldVar.WorldVariable;

import java.util.HashMap;
import java.util.Map;

public class DeicoreCommands {
    // 存储字符串和哈希值的映射，用于解密功能
    private static final Map<Integer, String> hashToStringMap = new HashMap<>();
    
    public static void registry_command() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("deicore")
                    .executes(context -> {
                        context.getSource().sendFeedback(
                                () -> Text.translatable("deicore.command.deicore"),
                                false
                        );
                        return 1;
                    })
            );



            // 新版本多类型命令：/world_var <operation>
            dispatcher.register(CommandManager.literal("world_var")
                    .then(CommandManager.literal("new")
                            .then(CommandManager.argument("type", StringArgumentType.string())
                                    .suggests((context, builder) -> {
                                        // 提供类型建议
                                        builder.suggest("int");
                                        builder.suggest("double");
                                        builder.suggest("string");
                                        builder.suggest("boolean");
                                        return builder.buildFuture();
                                    })
                                    .then(CommandManager.argument("name", StringArgumentType.string())
                                            .then(CommandManager.argument("value", StringArgumentType.string())
                                                    .suggests((context, builder) -> {
                                                        // 根据类型提供建议值
                                                        String typeStr = context.getInput().split("\\s+")[context.getInput().split("\\s+").length - 3]; // 获取type参数值
                                                        if ("boolean".equalsIgnoreCase(typeStr)) {
                                                            // 布尔值类型建议true和false
                                                            builder.suggest("true");
                                                            builder.suggest("false");
                                                        }
                                                        return builder.buildFuture();
                                                    })
                                                    .executes(context -> {
                                                        String typeStr = StringArgumentType.getString(context, "type");
                                                        String name = StringArgumentType.getString(context, "name");
                                                        String value = StringArgumentType.getString(context, "value");

                                                        try {
                                                            WorldVarType type = WorldVarType.valueOf(typeStr.toUpperCase());
                                                            
                                                            if (!type.isValidValue(value)) {
                                                                context.getSource().sendFeedback(
                                                                        () -> Text.translatable("deicore.command.world_var.type_mismatch", typeStr, value),
                                                                        false
                                                                );
                                                                return 0;
                                                            }

                                                            WorldVariable worldVar = new WorldVariable(name, type, value);
                                                            DeicoreWorldVar.addTypedWorldVar(worldVar);

                                                            context.getSource().sendFeedback(
                                                                    () -> Text.translatable("deicore.command.world_var.typed_new_success", name, typeStr, value),
                                                                    false
                                                            );
                                                            return 1;
                                                        } catch (IllegalArgumentException e) {
                                                            // 检查是否是重复变量名的异常
                                                            if (e.getMessage() != null && e.getMessage().contains("变量名") && e.getMessage().contains("已存在")) {
                                                                context.getSource().sendFeedback(
                                                                        () -> Text.translatable("deicore.command.world_var.duplicate_name", name),
                                                                        false
                                                                );
                                                            } else {
                                                                context.getSource().sendFeedback(
                                                                        () -> Text.translatable("deicore.command.world_var.invalid_type", typeStr),
                                                                        false
                                                                );
                                                            }
                                                            return 0;
                                                        }
                                                    })
                                            )
                                    )
                            )
                    )
                    .then(CommandManager.literal("get")
                            .then(CommandManager.argument("name", StringArgumentType.string())
                                    .suggests((context, builder) -> {
                                        // 提供已有的世界变量名称建议
                                        for (WorldVariable var : DeicoreWorldVar.getTypedWorldVars()) {
                                            builder.suggest(var.getName());
                                        }
                                        return builder.buildFuture();
                                    })
                                    .executes(context -> {
                                        String name = StringArgumentType.getString(context, "name");

                                        WorldVariable var = DeicoreWorldVar.getTypedWorldVar(name);
                                        if (var == null) {
                                            context.getSource().sendFeedback(
                                                    () -> Text.translatable("deicore.command.world_var.not_found").append(name),
                                                    false
                                            );
                                            return -1;
                                        }

                                        context.getSource().sendFeedback(
                                                () -> Text.translatable("deicore.command.world_var.typed_get_success", name, var.getType().name(), var.getValue()),
                                                false
                                        );
                                        
                                        // 根据变量类型返回相应的值
                                        switch (var.getType()) {
                                            case INT:
                                                return Integer.parseInt(var.getValue());
                                            case DOUBLE:
                                                // 对于DOUBLE类型，返回乘以100后的整数值（保留两位小数精度）
                                                return (int) (Double.parseDouble(var.getValue()) * 100);
                                            case BOOLEAN:
                                                // 对于BOOLEAN类型，返回1表示true，0表示false，适合计分板使用
                                                return Boolean.parseBoolean(var.getValue()) ? 1 : 0;
                                            case STRING:
                                                // 对于字符串类型，返回字符串的哈希值，适合计分板区分不同字符串
                                                return var.getValue().hashCode();
                                            default:
                                                // 对于其他类型，返回1表示成功
                                                return 1;
                                        }
                                    })
                            )
                    )
                    .then(CommandManager.literal("set")
                            .then(CommandManager.argument("name", StringArgumentType.string())
                                    .suggests((context, builder) -> {
                                        // 提供已有的世界变量名称建议
                                        for (WorldVariable var : DeicoreWorldVar.getTypedWorldVars()) {
                                            builder.suggest(var.getName());
                                        }
                                        return builder.buildFuture();
                                    })
                                    .then(CommandManager.argument("value", StringArgumentType.string())
                                            .executes(context -> {
                                                String name = StringArgumentType.getString(context, "name");
                                                String value = StringArgumentType.getString(context, "value");

                                                WorldVariable var = DeicoreWorldVar.getTypedWorldVar(name);
                                                if (var == null) {
                                                    context.getSource().sendFeedback(
                                                            () -> Text.translatable("deicore.command.world_var.not_found").append(name),
                                                            false
                                                    );
                                                    return -1;
                                                }

                                                if (!var.getType().isValidValue(value)) {
                                                    context.getSource().sendFeedback(
                                                            () -> Text.translatable("deicore.command.world_var.type_mismatch", var.getType().name(), value),
                                                            false
                                                    );
                                                    return 0;
                                                }

                                                Object result = DeicoreWorldVar.setTypedWorldVar(name, value);
                                                if ("NOT_FOUND".equals(result)) {
                                                    context.getSource().sendFeedback(
                                                            () -> Text.translatable("deicore.command.world_var.not_found").append(name),
                                                            false
                                                    );
                                                    return -1;
                                                } else if ("FORMAT_ERROR".equals(result)) {
                                                    context.getSource().sendFeedback(
                                                            () -> Text.translatable("deicore.command.world_var.format_error"),
                                                            false
                                                    );
                                                    return -1;
                                                }

                                                // 获取更新后的变量值并返回
                                                WorldVariable updatedVar = DeicoreWorldVar.getTypedWorldVar(name);
                                                if (updatedVar != null) {
                                                    context.getSource().sendFeedback(
                                                            () -> Text.translatable("deicore.command.world_var.typed_set_success", name, var.getType().name(), value),
                                                            false
                                                    );
                                                    
                                                    // 根据变量类型返回相应的值
                                                    switch (updatedVar.getType()) {
                                                        case INT:
                                                            return Integer.parseInt(updatedVar.getValue());
                                                        case DOUBLE:
                                                            // 对于DOUBLE类型，返回乘以100后的整数值（保留两位小数精度）
                                                            return (int) (Double.parseDouble(updatedVar.getValue()) * 100);
                                                        case BOOLEAN:
                                                            // 对于BOOLEAN类型，返回1表示true，0表示false，适合计分板使用
                                                            return Boolean.parseBoolean(updatedVar.getValue()) ? 1 : 0;
                                                        case STRING:
                                                            // 对于字符串类型，返回字符串的哈希值，适合计分板区分不同字符串
                                                            return updatedVar.getValue().hashCode();
                                                        default:
                                                            return 1;
                                                    }
                                                }
                                                return 1;
                                            })
                                    )
                            )
                    )
                    .then(CommandManager.literal("remove")
                            .then(CommandManager.argument("name", StringArgumentType.string())
                                    .suggests((context, builder) -> {
                                        // 提供已有的世界变量名称建议
                                        for (WorldVariable var : DeicoreWorldVar.getTypedWorldVars()) {
                                            builder.suggest(var.getName());
                                        }
                                        return builder.buildFuture();
                                    })
                                    .executes(context -> {
                                        String name = StringArgumentType.getString(context, "name");

                                        WorldVariable var = DeicoreWorldVar.getTypedWorldVar(name);
                                        if (var == null) {
                                            context.getSource().sendFeedback(
                                                    () -> Text.translatable("deicore.command.world_var.not_found").append(name),
                                                    false
                                            );
                                            return 0;
                                        }

                                        Object result = DeicoreWorldVar.removeWorldVar(name);
                                        if ("NOT_FOUND".equals(result)) {
                                            context.getSource().sendFeedback(
                                                    () -> Text.translatable("deicore.command.world_var.not_found").append(name),
                                                    false
                                            );
                                            return -1;
                                        }

                                        context.getSource().sendFeedback(
                                                () -> Text.translatable("deicore.command.world_var.typed_remove_success", name, var.getType().name()),
                                                false
                                        );
                                        return 404;
                                    })
                            )
                    )
                    .then(CommandManager.literal("add")
                            .then(CommandManager.argument("name", StringArgumentType.string())
                                    .suggests((context, builder) -> {
                                        // 提供已有的世界变量名称建议
                                        for (WorldVariable var : DeicoreWorldVar.getTypedWorldVars()) {
                                            builder.suggest(var.getName());
                                        }
                                        return builder.buildFuture();
                                    })
                                    .then(CommandManager.argument("value", IntegerArgumentType.integer())
                                            .executes(context -> {
                                                String name = StringArgumentType.getString(context, "name");
                                                int value = IntegerArgumentType.getInteger(context, "value");

                                                WorldVariable var = DeicoreWorldVar.getTypedWorldVar(name);
                                                if (var == null) {
                                                    context.getSource().sendFeedback(
                                                            () -> Text.translatable("deicore.command.world_var.not_found").append(name),
                                                            false
                                                    );
                                                    return -1;
                                                }

                                                Object result = DeicoreWorldVar.addWorldVar(name, value);
                                                if ("NOT_FOUND".equals(result)) {
                                                    context.getSource().sendFeedback(
                                                            () -> Text.translatable("deicore.command.world_var.not_found").append(name),
                                                            false
                                                    );
                                                    return -1;
                                                } else if ("FORMAT_ERROR".equals(result)) {
                                                    context.getSource().sendFeedback(
                                                            () -> Text.translatable("deicore.command.world_var.format_error"),
                                                            false
                                                    );
                                                    return -1;
                                                }

                                                // 获取更新后的变量值并返回
                                                WorldVariable updatedVar = DeicoreWorldVar.getTypedWorldVar(name);
                                                if (updatedVar != null) {
                                                    context.getSource().sendFeedback(
                                                            () -> Text.translatable("deicore.command.world_var.add_success", value, name, updatedVar.getValue()),
                                                            false
                                                    );
                                                    
                                                    // 根据变量类型返回相应的值
                                                    switch (updatedVar.getType()) {
                                                        case INT:
                                                            return Integer.parseInt(updatedVar.getValue());
                                                        case DOUBLE:
                                                            // 对于DOUBLE类型，返回乘以100后的整数值（保留两位小数精度）
                                                            return (int) (Double.parseDouble(updatedVar.getValue()) * 100);
                                                        case BOOLEAN:
                                                            // 对于BOOLEAN类型，返回1表示true，0表示false，适合计分板使用
                                                            return Boolean.parseBoolean(updatedVar.getValue()) ? 1 : 0;
                                                        case STRING:
                                                            // 对于字符串类型，返回字符串的哈希值，适合计分板区分不同字符串
                                                            return updatedVar.getValue().hashCode();
                                                        default:
                                                            return 1;
                                                    }
                                                }
                                                return 1;
                                            })
                                    )
                            )
                    )
                    .then(CommandManager.literal("subtract")
                            .then(CommandManager.argument("name", StringArgumentType.string())
                                    .suggests((context, builder) -> {
                                        // 提供已有的世界变量名称建议
                                        for (WorldVariable var : DeicoreWorldVar.getTypedWorldVars()) {
                                            builder.suggest(var.getName());
                                        }
                                        return builder.buildFuture();
                                    })
                                    .then(CommandManager.argument("value", IntegerArgumentType.integer())
                                            .executes(context -> {
                                                String name = StringArgumentType.getString(context, "name");
                                                int value = IntegerArgumentType.getInteger(context, "value");

                                                WorldVariable var = DeicoreWorldVar.getTypedWorldVar(name);
                                                if (var == null) {
                                                    context.getSource().sendFeedback(
                                                            () -> Text.translatable("deicore.command.world_var.not_found").append(name),
                                                            false
                                                    );
                                                    return 0;
                                                }

                                                Object result = DeicoreWorldVar.subtractWorldVar(name, value);
                                                if ("NOT_FOUND".equals(result)) {
                                                    context.getSource().sendFeedback(
                                                            () -> Text.translatable("deicore.command.world_var.not_found").append(name),
                                                            false
                                                    );
                                                    return -1;
                                                } else if ("FORMAT_ERROR".equals(result)) {
                                                    context.getSource().sendFeedback(
                                                            () -> Text.translatable("deicore.command.world_var.format_error"),
                                                            false
                                                    );
                                                    return -1;
                                                }

                                                // 获取更新后的变量值并返回
                                                WorldVariable updatedVar = DeicoreWorldVar.getTypedWorldVar(name);
                                                if (updatedVar != null) {
                                                    context.getSource().sendFeedback(
                                                            () -> Text.translatable("deicore.command.world_var.subtract_success", value, name, updatedVar.getValue()),
                                                            false
                                                    );
                                                    
                                                    // 根据变量类型返回相应的值
                                                    switch (updatedVar.getType()) {
                                                        case INT:
                                                            return Integer.parseInt(updatedVar.getValue());
                                                        case DOUBLE:
                                                            // 对于DOUBLE类型，返回乘以100后的整数值（保留两位小数精度）
                                                            return (int) (Double.parseDouble(updatedVar.getValue()) * 100);
                                                        case BOOLEAN:
                                                            // 对于BOOLEAN类型，返回1表示true，0表示false，适合计分板使用
                                                            return Boolean.parseBoolean(updatedVar.getValue()) ? 1 : 0;
                                                        case STRING:
                                                            // 对于字符串类型，返回字符串的哈希值，适合计分板区分不同字符串
                                                            return updatedVar.getValue().hashCode();
                                                        default:
                                                            return 1;
                                                    }
                                                }
                                                return 1;
                                            })
                                    )
                            )
                    )
            );
            
            // 哈希转换命令：/hash <string>
            dispatcher.register(CommandManager.literal("hash")
                    .then(CommandManager.literal("encrypt").then(CommandManager.argument("string", StringArgumentType.string())
                            .executes(context -> {
                                String inputString = StringArgumentType.getString(context, "string");
                                int hashCode = inputString.hashCode();
                                
                                // 记录字符串和哈希值的映射
                                hashToStringMap.put(hashCode, inputString);
                                
                                // 创建可点击复制的文本
                                Text clickableText = Text.translatable("deicore.command.hash.hash_value", hashCode)
                                        .styled(style -> style
                                                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(hashCode)))
                                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("deicore.command.hash.copy_tooltip")))
                                                .withColor(Formatting.GREEN)
                                        );
                                
                                return 1;
                            })
                    ))
                    // 解密命令：/hash decrypt <hash_value>
                    .then(CommandManager.literal("decrypt")
                            .then(CommandManager.argument("hash_value", IntegerArgumentType.integer())
                                    .executes(context -> {
                                        int hashCode = IntegerArgumentType.getInteger(context, "hash_value");
                                        
                                        // 查找对应的字符串
                                String originalString = hashToStringMap.get(hashCode);
                                
                                if (originalString != null) {
                                    // 找到对应的字符串，创建可点击复制的文本
                                    Text clickableText = Text.translatable("deicore.command.hash.decrypt_result", originalString)
                                            .styled(style -> style
                                                    .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, originalString))
                                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("deicore.command.hash.copy_tooltip")))
                                                    .withColor(Formatting.GREEN)
                                            );
                                    
                                    context.getSource().sendFeedback(
                                            () -> Text.translatable("deicore.command.hash.decrypt_success", hashCode, originalString),
                                            false
                                    );
                                } else {
                                    // 没有找到对应的字符串
                                    context.getSource().sendFeedback(
                                            () -> Text.translatable("deicore.command.hash.decrypt_not_found", hashCode),
                                            false
                                    );
                                }
                                
                                return 1;
                            })
                    ))
            );
        });
    }
}