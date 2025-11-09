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
        
        // 多类型命令翻译
        translationBuilder.add("deicore.command.world_var.invalid_type", "§c无效的变量类型：%s");
        translationBuilder.add("deicore.command.world_var.type_mismatch", "§c类型不匹配：期望类型为%s，但提供的值为'%s'");
        translationBuilder.add("deicore.command.world_var.type_mismatch_existing", "§c类型不匹配：期望类型为%s，但现有变量类型为%s");
        translationBuilder.add("deicore.command.world_var.typed_new_success", "成功创建%s类型变量「%s」，值为'%s'");
        translationBuilder.add("deicore.command.world_var.typed_get_success", "变量「%s」（%s类型）的值为：%s");
        translationBuilder.add("deicore.command.world_var.typed_set_success", "成功将%s类型变量「%s」的值设置为'%s'");
        translationBuilder.add("deicore.command.world_var.typed_remove_success", "成功删除%s类型变量「%s」");
        translationBuilder.add("deicore.command.world_var.duplicate_name", "§c变量名「%s」已存在！");
        
        // Hash命令翻译
        translationBuilder.add("deicore.command.hash.hash_value", "哈希值：%d");
        translationBuilder.add("deicore.command.hash.copy_tooltip", "点击复制哈希值");
        translationBuilder.add("deicore.command.hash.result", "字符串「%s」的哈希值为%d");
        translationBuilder.add("deicore.command.hash.decrypt_result", "原始字符串：%s");
        translationBuilder.add("deicore.command.hash.decrypt_success", "找到哈希值%d对应的原始字符串：%s");
        translationBuilder.add("deicore.command.hash.decrypt_not_found", "未找到哈希值%d对应的字符串");
    }
}