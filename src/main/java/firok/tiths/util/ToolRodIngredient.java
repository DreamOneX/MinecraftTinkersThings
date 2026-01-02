package firok.tiths.util;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.tools.TinkerTools;

import java.util.Arrays;

public class ToolRodIngredient extends Ingredient
{
	String material;
	public ToolRodIngredient(String material)
	{
		this.material=material;
	}
	@SuppressWarnings("all")
	@Override
	public boolean apply(ItemStack input) {
		return input!=null &&
				input.getItem() == TinkerTools.toolRod &&
				input.hasTagCompound() &&
				input.getTagCompound().hasKey("Material") &&
				material.equals(input.getTagCompound().getString("Material"));
	}

	ItemStack[] matchingStacks=null;
	@Override
	public ItemStack[] getMatchingStacks() {
		if(matchingStacks==null)
		{
			Material mat= TinkerRegistry.getMaterial(material);
			if(mat==null)
			{
				matchingStacks=new ItemStack[0];
			}
			else
			{
				matchingStacks=new ItemStack[]{ TinkerTools.toolRod.getItemstackWithMaterial(mat) };
			}
		}
		return matchingStacks;
	}

	@Override
	public IntList getValidItemStacksPacked() {
		// Use a safe implementation that doesn't depend on client-side RecipeItemHelper
		return new IntArrayList(Arrays.stream(this.getMatchingStacks())
				.mapToInt(ToolRodIngredient::packItemStack)
				.sorted()
				.toArray());
	}
	
	/**
	 * Server-safe alternative to RecipeItemHelper.pack()
	 * Packs an ItemStack into an int for recipe matching
	 */
	private static int packItemStack(ItemStack stack) {
		if (stack.isEmpty()) {
			return 0;
		}
		Item item = stack.getItem();
		int id = Item.getIdFromItem(item);
		int damage = stack.getMetadata();
		return id << 16 | damage & 0xFFFF;
	}

	@Override
	protected void invalidate() {
		this.matchingStacks=null;
	}
	@Override
	public boolean isSimple() {
		return true;
	}

}