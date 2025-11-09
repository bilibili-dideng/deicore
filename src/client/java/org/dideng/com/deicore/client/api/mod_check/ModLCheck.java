package org.dideng.com.deicore.client.api.mod_check;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ModLCheck {
    private String Mod_ID;
    private String SayText;

    public ModLCheck(String Mod_ID, String SayText) {
        this.Mod_ID = Mod_ID;
        this.SayText = SayText;
    }

    public void init() {
        // 在客户端环境中，我们使用客户端事件来显示信息
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
