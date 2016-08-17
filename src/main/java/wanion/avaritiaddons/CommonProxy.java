package wanion.avaritiaddons;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import fox.spiteful.avaritia.crafting.ExtremeCraftingManager;
import fox.spiteful.avaritia.crafting.Grinder;
import fox.spiteful.avaritia.items.LudicrousItems;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import wanion.avaritiaddons.block.chest.compressed.BlockCompressedChest;
import wanion.avaritiaddons.block.chest.compressed.TileEntityCompressedChest;
import wanion.avaritiaddons.block.chest.infinity.BlockInfinityChest;
import wanion.avaritiaddons.block.chest.infinity.TileEntityInfinityChest;
import wanion.avaritiaddons.core.GuiHandler;

import java.util.Iterator;
import java.util.List;

public class CommonProxy
{
	private final TIntSet oresToRemove = new TIntHashSet();
	private final TIntSet stacksToRemove = new TIntHashSet();

	public void preInit()
	{
		GameRegistry.registerBlock(BlockCompressedChest.instance, "CompressedChest");
		GameRegistry.registerBlock(BlockInfinityChest.instance, "InfinityChest");
		GameRegistry.registerTileEntity(TileEntityCompressedChest.class, "compressedChest");
		GameRegistry.registerTileEntity(TileEntityInfinityChest.class, "infinityChest");
		NetworkRegistry.INSTANCE.registerGuiHandler(Avaritiaddons.instance, GuiHandler.instance);
	}

	public final void init()
	{
		if (Config.hardCompressedChestRecipe)
			ExtremeCraftingManager.getInstance().addExtremeShapedOreRecipe(new ItemStack(BlockCompressedChest.instance), "CCCCCCCCC", "CCCCCCCCC", "CCCCCCCCC", "CCCCCCCCC", "CCCCCCCCC", "CCCCCCCCC", "CCCCCCCCC", "CCCCCCCCC", "CCCCCCCCC", 'C', Blocks.chest);
		else
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockCompressedChest.instance), "CCC", "CCC", "CCC", 'C', Blocks.chest));
		if (Config.hardInfinityChestRecipe)
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockInfinityChest.instance), "INI", "BCB", "IBI", 'I', "ingotInfinity", 'N', "blockCosmicNeutronium", 'B', "blockInfinity", 'C', BlockCompressedChest.instance));
		else
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockInfinityChest.instance), "TNT", "ICI", "TIT", 'T', new ItemStack(LudicrousItems.resource, 1, 5), 'N', "blockCosmicNeutronium", 'I', "ingotInfinity", 'C', BlockCompressedChest.instance));
	}

	public final void postInit()
	{
		populateRemovalSets();
		for (final Iterator<Object> catalystRecipeIterator = Grinder.catalyst.getInput().iterator(); catalystRecipeIterator.hasNext(); )
		{
			final Object catalystRecipeInput = catalystRecipeIterator.next();
			final int hash = catalystRecipeInput instanceof List ? System.identityHashCode(catalystRecipeInput) : catalystRecipeInput instanceof ItemStack ? BasicMetaItem.get((ItemStack)catalystRecipeInput) : 0;
			if (hash == 0)
				continue;
			if ((catalystRecipeInput instanceof List && oresToRemove.contains(hash)) || (catalystRecipeInput instanceof ItemStack && stacksToRemove.contains(hash)))
				catalystRecipeIterator.remove();
		}
	}

	private void populateRemovalSets()
	{
		Config.thingsToRemoveFromInfinityCatalystRecipe.forEach(toRemove -> {
			if (toRemove.indexOf(':') == -1){
				final List<ItemStack> ores = OreDictionary.getOres(toRemove, false);
				if (ores != null && !ores.isEmpty())
					oresToRemove.add(System.identityHashCode(ores));
			} else {
				final int separatorChar = toRemove.indexOf('#');
				final Item item = BasicMetaItem.itemRegistry.getRaw(separatorChar == -1 ? toRemove : toRemove.substring(0, separatorChar));
				if (item != null)
				{
					final int metadata = separatorChar == -1 ? 0 : Integer.parseInt(toRemove.substring(separatorChar+1, toRemove.length()));
					final int hash = BasicMetaItem.get(new ItemStack(item, 1, metadata));
					if (hash > 0)
						stacksToRemove.add(hash);
				}
			}
		});
	}
}