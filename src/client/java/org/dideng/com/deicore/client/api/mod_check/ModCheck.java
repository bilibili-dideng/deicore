package org.dideng.com.deicore.client.api.mod_check;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class ModCheck {
    private String translatable_key;
    private String Mod_ID;

    public ModCheck(String translatable_key, String Mod_ID) {
        this.translatable_key = translatable_key;
        this.Mod_ID = Mod_ID;
    }

    public void init() {
        registry_check_command(this.translatable_key, this.Mod_ID);
    }
    public static void registry_check_command(String translatable_key, String Mod_ID) {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal(Mod_ID)
                    .then(CommandManager.literal("check").executes(context -> {
                        context.getSource().sendFeedback(() -> Text.translatable(translatable_key), false);
                        return 10;
                    }))
            );
        });
    }
}
