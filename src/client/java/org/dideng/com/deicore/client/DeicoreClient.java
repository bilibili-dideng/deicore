package org.dideng.com.deicore.client;

import net.fabricmc.api.ClientModInitializer;
import org.dideng.com.deicore.Deicore;
import org.dideng.com.deicore.client.api.mod_check.ModCheck;

public class DeicoreClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // 初始化ModCheck（客户端版本）
        ModCheck deicoreModCheck = new ModCheck("deicore.api.mod_check.command", Deicore.MOD_ID);
        deicoreModCheck.init();
        
        // 注意：WorldVar配置加载和命令注册已移至服务器端入口点
        // 以避免客户端类加载问题
    }

}
