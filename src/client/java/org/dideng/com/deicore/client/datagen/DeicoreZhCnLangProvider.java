package org.dideng.com.deicore.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import org.dideng.com.deicore.Deicore;

import java.util.concurrent.CompletableFuture;

public class DeicoreZhCnLangProvider extends FabricLanguageProvider {
    protected DeicoreZhCnLangProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "zh_cn", registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
        // 模组版本校验
        translationBuilder.add("deicore.api.mod_check.command", "Deicore版本 " + Deicore.MOD_VERSION);
        translationBuilder.add("deicore.command.deicore", "§c无法单独使用「deicore」命令，请添加子命令后重试！");
        translationBuilder.add("deicore.command.world_var.not_found", "§c找不到变量：");
        translationBuilder.add("deicore.command.world_var.format_error", "§c变量格式错误！");
        translationBuilder.add("deicore.command.world_var.unknown_error", "§c变量操作出现未知错误！");
        translationBuilder.add("deicore.command.world_var.new_success", "成功创建变量「%s」，初始值为%d");
        translationBuilder.add("deicore.command.world_var.get_success", "变量「%s」的值为%d");
        translationBuilder.add("deicore.command.world_var.add_success", "变量「%s」成功累加%d，当前值为%d");
        translationBuilder.add("deicore.command.world_var.set_success", "成功将变量「%s」设置为%d");
        translationBuilder.add("deicore.command.world_var.remove_success", "成功删除变量「%s」");
        translationBuilder.add("deicore.command.world_var.subtract_success", "变量「%s」成功减去%d，当前值为%d");
    }
}