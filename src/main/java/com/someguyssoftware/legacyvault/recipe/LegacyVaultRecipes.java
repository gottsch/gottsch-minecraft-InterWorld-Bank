/**
 * 
 */
package com.someguyssoftware.legacyvault.recipe;

import com.someguyssoftware.legacyvault.recipe.condition.VaultEasyDifficultyCondition;
import com.someguyssoftware.legacyvault.recipe.condition.VaultHardDifficultyCondition;
import com.someguyssoftware.legacyvault.recipe.condition.VaultNormalDifficultyCondition;

import mod.gottsch.forge.legacyvault.LegacyVault;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

/**
 * @author Mark Gottschling on May 26, 2021
 *
 */
@Mod.EventBusSubscriber(modid = LegacyVault.MODID, bus = EventBusSubscriber.Bus.MOD)
public class LegacyVaultRecipes {

	 // ModBus, can't use addListener due to nested genetics.
    @SubscribeEvent
    public static void registerRecipeSerialziers(RegistryEvent.Register<RecipeSerializer<?>> event) {
    	LegacyVault.LOGGER.info("in recipe subscribe event");
        CraftingHelper.register(VaultEasyDifficultyCondition.Serializer.INSTANCE);
        CraftingHelper.register(VaultNormalDifficultyCondition.Serializer.INSTANCE);
        CraftingHelper.register(VaultHardDifficultyCondition.Serializer.INSTANCE);
    }
}
