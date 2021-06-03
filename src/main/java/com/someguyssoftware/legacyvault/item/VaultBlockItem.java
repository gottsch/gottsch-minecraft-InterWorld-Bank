/*
 * This file is part of Legacy Vault.
 * Copyright (c) 2021, Mark Gottschling (gottsch)
 * 
 * All rights reserved.
 *
 * Legacy Vault is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Legacy Vault is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Legacy Vault.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package com.someguyssoftware.legacyvault.item;

import java.util.List;

import com.someguyssoftware.gottschcore.world.WorldInfo;
import com.someguyssoftware.legacyvault.LegacyVault;
import com.someguyssoftware.legacyvault.capability.IVaultCountHandler;
import com.someguyssoftware.legacyvault.capability.LegacyVaultCapabilities;
import com.someguyssoftware.legacyvault.config.Config;
import com.someguyssoftware.legacyvault.init.LegacyVaultSetup;
import com.someguyssoftware.legacyvault.network.LegacyVaultNetworking;
import com.someguyssoftware.legacyvault.network.VaultCountMessageToClient;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * @author Mark Gottschling on May 25, 2021
 *
 */
public class VaultBlockItem extends BlockItem {

	/**
	 * 
	 * @param block
	 * @param properties
	 */
	public VaultBlockItem(Block block, Properties properties) {
		super(block, properties);
	}
	
	/**
	 * 
	 */
	@Override
	protected boolean placeBlock(BlockItemUseContext context, BlockState state) {
		if (WorldInfo.isServerSide(context.getLevel())) {
			
			/*
			 *  Q: why add this creative check ? what is the reasoning?
			 *  A: the Block.playerDestroy() is only called in survival mode, thus the decrement vault count calculation is not executed, 
			 *  which will throw off the vault count. so just making Creative actions the same for placement and destroy.
			 */
			
			if (context.getPlayer().isCreative()) {
				return context.getLevel().setBlock(context.getClickedPos(), state, 26);
			}			
			
			if (Config.GENERAL.enablePublicVault.get()) {
				// TODO only admin UUIDs can place
				return context.getLevel().setBlock(context.getClickedPos(), state, 26);
			}
			else {
				if (Config.GENERAL.enableLimitedVaults.get()) {
					// get  player capabilities
					IVaultCountHandler cap = context.getPlayer().getCapability(LegacyVaultCapabilities.VAULT_BRANCH).orElseThrow(() -> {
						return new RuntimeException("player does not have VaultCountHandler capability.'");
					});
					LegacyVault.LOGGER.debug("player branch count -> {}", cap.getCount());
	
					if (cap != null && cap.getCount() < Config.GENERAL.vaultsPerPlayer.get()) {
						LegacyVault.LOGGER.debug("player branch count less than config -> {}", Config.GENERAL.vaultsPerPlayer.get());
						
						// TODO does the increment portion belong here or in VaultBlock ? 
						// increment capability size
						int count = cap.getCount() + 1;
						count = count > Config.GENERAL.vaultsPerPlayer.get() ? Config.GENERAL.vaultsPerPlayer.get() : count;
						cap.setCount(count);
						// send state message to client
						VaultCountMessageToClient message = new VaultCountMessageToClient(context.getPlayer().getStringUUID(), count);
						LegacyVaultNetworking.simpleChannel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)context.getPlayer()),message);
						return context.getLevel().setBlock(context.getClickedPos(), state, 26);
					}
					else {
						LegacyVault.LOGGER.debug("player branch count greater than config-> {}",  Config.GENERAL.vaultsPerPlayer.get());
					}
				}
				else {
					return context.getLevel().setBlock(context.getClickedPos(), state, 26);
				}
			}
		}
		else {
			LegacyVault.LOGGER.debug("no can do, you're on client side");
		}
		return false;
	}
}
