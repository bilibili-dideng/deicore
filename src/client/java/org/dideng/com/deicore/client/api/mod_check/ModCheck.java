package org.dideng.com.deicore.client.api.mod_check;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ModCheck {
    private String translatable_key;
    private String Mod_ID;

    public ModCheck(String translatable_key, String Mod_ID) {
        this.translatable_key = translatable_key;
        this.Mod_ID = Mod_ID;
    }

    public void init() {
        // 在客户端环境中，使用客户端事件来显示信息
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
        });
    }
    
    // 客户端版本的方法，用于在客户端显示信息
    public static void showClientMessage(String message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(Text.literal(message), false);
        }
    }
}
