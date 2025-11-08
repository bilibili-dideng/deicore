package org.dideng.com.deicore.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import org.dideng.com.deicore.Deicore;

import java.util.concurrent.CompletableFuture;

public class DeicoreEn_UsLangProvider extends FabricLanguageProvider {
    protected DeicoreEn_UsLangProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add("deicore.api.mod_check.command", "Deicore version " + Deicore.MOD_VERSION);
        translationBuilder.add("deicore.command.deicore", "§cCannot use the command 'deicore' alone, please add a sub - command and try again!");
        translationBuilder.add("deicore.command.world_var.not_found", "§cWorldVar not found: ");
        translationBuilder.add("deicore.command.world_var.format_error", "§cWorldVar format error!");
        translationBuilder.add("deicore.command.world_var.unknown_error", "§cUnknown error occurred with WorldVar!");
        translationBuilder.add("deicore.command.world_var.new_success", "Successfully created WorldVar '%s' with initial value %d");
        translationBuilder.add("deicore.command.world_var.get_success", "The value of WorldVar '%s' is %d");
        translationBuilder.add("deicore.command.world_var.add_success", "Successfully added %d to WorldVar '%s', the new value is %d");
        translationBuilder.add("deicore.command.world_var.set_success", "Successfully set WorldVar '%s' to %d");
        translationBuilder.add("deicore.command.world_var.remove_success", "Successfully deleted WorldVar '%s'");
        translationBuilder.add("deicore.command.world_var.subtract_success", "Successfully subtracted %d from WorldVar '%s', the new value is %d");
    }
}