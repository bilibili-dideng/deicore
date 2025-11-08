package org.dideng.com.deicore.client.api.mod_check;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class ModLCheck {
    private String Mod_ID;
    private String SayText;

    public ModLCheck(String Mod_ID, String SayText) {
        this.Mod_ID = Mod_ID;
        this.SayText = SayText;
    }

    public void init() {
        registry_check_command(this.Mod_ID, this.SayText);
    }

    public static void registry_check_command(String Mod_ID, String SayText) {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal(Mod_ID)
                    .then(CommandManager.literal("check").executes(context -> {
                        context.getSource().sendFeedback(() -> Text.literal(SayText), false);
                        return 10;
                    }))
            );
        });
    }
}
