package org.dideng.com.deicore.client;

import net.fabricmc.api.ClientModInitializer;
import org.dideng.com.deicore.Deicore;
import org.dideng.com.deicore.client.api.mod_check.ModCheck;
import org.dideng.com.deicore.client.command.WorldVar.WorldVarConfigManager;

import static org.dideng.com.deicore.client.command.DeicoreCommands.registry_command;

public class DeicoreClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModCheck Deicore_ModCheck = new ModCheck("deicore.api.mod_check.command", Deicore.MOD_ID);
        Deicore_ModCheck.init();
        
        // 加载WorldVar配置
        WorldVarConfigManager.loadWorldVars();
        
        registry_command();
    }

}
