package org.dideng.com.deicore.client.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.dideng.com.deicore.client.command.WorldVar.DeicoreWorldVar;
import java.util.HashMap;
import java.util.Map;

public class DeicoreCommands {
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

            dispatcher.register(CommandManager.literal("world_var")
                    .then(CommandManager.literal("new")
                            .then(CommandManager.argument("VarName", StringArgumentType.string())
                                    .then(CommandManager.argument("Value", IntegerArgumentType.integer())
                                            .executes(context -> {
                                                String varName = StringArgumentType.getString(context, "VarName");
                                                int value = IntegerArgumentType.getInteger(context, "Value");

                                                Map<String, String> newVar = new HashMap<>();
                                                newVar.put("VarName", varName);
                                                newVar.put("Var", String.valueOf(value));
                                                DeicoreWorldVar.addWorldVar(newVar);

                                                context.getSource().sendFeedback(
                                                        () -> Text.translatable("deicore.command.world_var.new_success", varName, value),
                                                        false
                                                );
                                                return 1;
                                            })
                                    )
                            )
                    )
                    .then(CommandManager.literal("get")
                            .then(CommandManager.argument("VarName", StringArgumentType.string())
                                    .executes(context -> {
                                        String varName = StringArgumentType.getString(context, "VarName");
                                        Object result = DeicoreWorldVar.getWorldVar(varName);

                                        if ("NOT_FOUND".equals(result)) {
                                            context.getSource().sendFeedback(
                                                    () -> Text.translatable("deicore.command.world_var.not_found").append(varName),
                                                    false
                                            );
                                            return 0;
                                        } else if ("FORMAT_ERROR".equals(result)) {
                                            context.getSource().sendFeedback(
                                                    () -> Text.translatable("deicore.command.world_var.format_error"),
                                                    false
                                            );
                                            return 0;
                                        }

                                        if (result instanceof Integer) {
                                            context.getSource().sendFeedback(
                                                    () -> Text.translatable("deicore.command.world_var.get_success", varName, result),
                                                    false
                                            );
                                            return (Integer) result;
                                        } else {
                                            context.getSource().sendFeedback(
                                                    () -> Text.translatable("deicore.command.world_var.unknown_error"),
                                                    false
                                            );
                                            return 0;
                                        }
                                    })
                            )
                    )
                    .then(CommandManager.literal("add")
                            .then(CommandManager.argument("VarName", StringArgumentType.string())
                                    .then(CommandManager.argument("Value", IntegerArgumentType.integer())
                                            .executes(context -> {
                                                String varName = StringArgumentType.getString(context, "VarName");
                                                int addValue = IntegerArgumentType.getInteger(context, "Value");
                                                Object result = DeicoreWorldVar.addWorldVar(varName, addValue);

                                                if ("NOT_FOUND".equals(result)) {
                                                    context.getSource().sendFeedback(
                                                            () -> Text.translatable("deicore.command.world_var.not_found").append(varName),
                                                            false
                                                    );
                                                    return 0;
                                                } else if ("FORMAT_ERROR".equals(result)) {
                                                    context.getSource().sendFeedback(
                                                            () -> Text.translatable("deicore.command.world_var.format_error"),
                                                            false
                                                    );
                                                    return 0;
                                                }

                                                Object newValue = DeicoreWorldVar.getWorldVar(varName);
                                                if (newValue instanceof Integer) {
                                                    context.getSource().sendFeedback(
                                                            () -> Text.translatable("deicore.command.world_var.add_success", varName, addValue, newValue),
                                                            false
                                                    );
                                                    return (Integer) newValue;
                                                } else {
                                                    return 0;
                                                }
                                            })
                                    )
                            )
                    )
                    .then(CommandManager.literal("set")
                            .then(CommandManager.argument("VarName", StringArgumentType.string())
                                    .then(CommandManager.argument("NewValue", IntegerArgumentType.integer())
                                            .executes(context -> {
                                                String varName = StringArgumentType.getString(context, "VarName");
                                                int newValue = IntegerArgumentType.getInteger(context, "NewValue");
                                                Object result = DeicoreWorldVar.setWorldVar(varName, newValue);

                                                if ("NOT_FOUND".equals(result)) {
                                                    context.getSource().sendFeedback(
                                                            () -> Text.translatable("deicore.command.world_var.not_found").append(varName),
                                                            false
                                                    );
                                                    return 0;
                                                }

                                                context.getSource().sendFeedback(
                                                        () -> Text.translatable("deicore.command.world_var.set_success", varName, newValue),
                                                        false
                                                );
                                                return newValue;
                                            })
                                    )
                            )
                    )
                    .then(CommandManager.literal("remove")
                            .then(CommandManager.argument("VarName", StringArgumentType.string())
                                    .executes(context -> {
                                        String varName = StringArgumentType.getString(context, "VarName");
                                        Object result = DeicoreWorldVar.removeWorldVar(varName);

                                        if ("NOT_FOUND".equals(result)) {
                                            context.getSource().sendFeedback(
                                                    () -> Text.translatable("deicore.command.world_var.not_found").append(varName),
                                                    false
                                            );
                                            return 0;
                                        }

                                        context.getSource().sendFeedback(
                                                () -> Text.translatable("deicore.command.world_var.remove_success", varName),
                                                false
                                        );
                                        return 1;
                                    })
                            )
                    )
                    .then(CommandManager.literal("subtract")
                            .then(CommandManager.argument("VarName", StringArgumentType.string())
                                    .then(CommandManager.argument("SubtractValue", IntegerArgumentType.integer())
                                            .executes(context -> {
                                                String varName = StringArgumentType.getString(context, "VarName");
                                                int subtractValue = IntegerArgumentType.getInteger(context, "SubtractValue");
                                                Object result = DeicoreWorldVar.subtractWorldVar(varName, subtractValue);

                                                if ("NOT_FOUND".equals(result)) {
                                                    context.getSource().sendFeedback(
                                                            () -> Text.translatable("deicore.command.world_var.not_found").append(varName),
                                                            false
                                                    );
                                                    return 0;
                                                } else if ("FORMAT_ERROR".equals(result)) {
                                                    context.getSource().sendFeedback(
                                                            () -> Text.translatable("deicore.command.world_var.format_error"),
                                                            false
                                                    );
                                                    return 0;
                                                }

                                                Object newValue = DeicoreWorldVar.getWorldVar(varName);
                                                if (newValue instanceof Integer) {
                                                    context.getSource().sendFeedback(
                                                            () -> Text.translatable("deicore.command.world_var.subtract_success", varName, subtractValue, newValue),
                                                            false
                                                    );
                                                    return (Integer) newValue;
                                                } else {
                                                    return 0;
                                                }
                                            })
                                    )
                            )
                    )
            );
        });
    }
}