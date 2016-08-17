package wanion.avaritiaddons.block.chest.infinity;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import wanion.avaritiaddons.block.chest.ContainerAvaritiaddonsChest;

import javax.annotation.Nonnull;

public final class ContainerInfinityChest extends ContainerAvaritiaddonsChest
{
	@SuppressWarnings("unchecked")
	public ContainerInfinityChest(@Nonnull final TileEntityInfinityChest tileEntityCompressedChest, final InventoryPlayer inventoryPlayer)
	{
		super(tileEntityCompressedChest, inventoryPlayer);
	}

	@Override
	public boolean mergeItemStack(final ItemStack itemStack, final int start, final int end, final boolean backwards)
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			return false;
		int currentSlot = !backwards ? start : end - 1;
		boolean someThingChanged = false;

		while (itemStack.stackSize > 0 && (!backwards && currentSlot < end || backwards && currentSlot >= start)) {
			final Slot slot = (Slot) inventorySlots.get(currentSlot);
			final ItemStack slotStack = slot.getStack();
			boolean changed = false;

			// do inventário do player para o inventario do baú
			if (!backwards) {
				if (slotStack == null) {
					slot.putStack(itemStack.copy());
					itemStack.stackSize = 0;
					changed = true;
				} else if (slotStack.getItem() == itemStack.getItem() && (!itemStack.getHasSubtypes() || itemStack.getItemDamage() == slotStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(itemStack, slotStack)) {
					slotStack.stackSize += itemStack.stackSize;
					itemStack.stackSize = 0;
					changed = true;
				}
				++currentSlot;
				// do baú para o inventário do player
			} else {
				if (slotStack == null) {
					final int dif = MathHelper.clamp_int(itemStack.getMaxStackSize(), 1, itemStack.stackSize);
					final ItemStack newItemStack = itemStack.copy();
					newItemStack.stackSize = dif;
					itemStack.stackSize -= dif;
					slot.putStack(newItemStack);
					changed = true;
				} else if (slotStack.getItem() == itemStack.getItem() && slotStack.stackSize < slotStack.getMaxStackSize() && (!itemStack.getHasSubtypes() || itemStack.getItemDamage() == slotStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(itemStack, slotStack)) {
					final int dif = MathHelper.clamp_int(itemStack.stackSize, 1, slotStack.getMaxStackSize() - slotStack.stackSize);
					slotStack.stackSize += dif;
					itemStack.stackSize -= dif;
					changed = true;
				}
				--currentSlot;
			}
			if (changed) {
				slot.onSlotChanged();
				someThingChanged = true;
			}
		}
		return someThingChanged;
	}

	@Override
	public boolean canDragIntoSlot(final Slot slot)
	{
		return slot.slotNumber > 242;
	}

	@Override
	public ItemStack slotClick(final int slot, final int mouseButton, final int modifier, final EntityPlayer entityPlayer)
	{
		if (slot >= 0 && slot < 243 && modifier == 0) {
			Slot actualSlot = (Slot) inventorySlots.get(slot);
			final ItemStack slotStack = actualSlot.getStack();
			final ItemStack playerStack = entityPlayer.inventory.getItemStack();
			if (mouseButton == 0) {
				if (slotStack == null && playerStack != null) {
					actualSlot.putStack(playerStack.copy());
					entityPlayer.inventory.setItemStack(null);
				} else if (slotStack != null && playerStack == null) {
					final ItemStack newItemStack = slotStack.copy();
					int took = MathHelper.clamp_int(slotStack.stackSize, 1, newItemStack.getMaxStackSize());
					newItemStack.stackSize = took;
					slotStack.stackSize -= took;
					entityPlayer.inventory.setItemStack(newItemStack);
					if (slotStack.stackSize == 0)
						actualSlot.putStack(null);
					else
						actualSlot.onSlotChanged();
				} else if (slotStack != null && slotStack.getItem() == playerStack.getItem() && (!playerStack.getHasSubtypes() || playerStack.getItemDamage() == slotStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(playerStack, slotStack)) {
					slotStack.stackSize += playerStack.stackSize;
					entityPlayer.inventory.setItemStack(null);
				}
			} else if (mouseButton == 1 && playerStack != null) {
				if (slotStack == null) {
					playerStack.stackSize--;
					final ItemStack newItemStack = playerStack.copy();
					newItemStack.stackSize = 1;
					actualSlot.putStack(newItemStack);
					if (playerStack.stackSize == 0)
						entityPlayer.inventory.setItemStack(null);
				} else if (slotStack.getItem() == playerStack.getItem() && (!playerStack.getHasSubtypes() || playerStack.getItemDamage() == slotStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(playerStack, slotStack)) {
					playerStack.stackSize--;
					slotStack.stackSize++;
					actualSlot.onSlotChanged();
					if (playerStack.stackSize == 0)
						entityPlayer.inventory.setItemStack(null);
				}
			} else if (mouseButton == 1 && slotStack != null) {
				int took = slotStack.stackSize > slotStack.getMaxStackSize() ? slotStack.getMaxStackSize() / 2 : slotStack.stackSize / 2;
				if (took == 0)
					took = 1;
				final ItemStack newItemStack = slotStack.copy();
				newItemStack.stackSize = took;
				slotStack.stackSize -= took;
				entityPlayer.inventory.setItemStack(newItemStack);
				if (slotStack.stackSize == 0)
					actualSlot.putStack(null);
				else
					actualSlot.onSlotChanged();
			}
			return actualSlot.getHasStack() ? actualSlot.getStack().copy() : null;
		} else if (slot >= 0 && slot < 243 && modifier == 2)
			return null;
		else
			return super.slotClick(slot, mouseButton, modifier, entityPlayer);
	}
}