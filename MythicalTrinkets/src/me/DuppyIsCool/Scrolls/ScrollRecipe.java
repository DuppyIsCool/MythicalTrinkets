package me.DuppyIsCool.Scrolls;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.meta.ItemMeta;

import me.DuppyIsCool.Main.Plugin;
import net.md_5.bungee.api.ChatColor;
@SuppressWarnings("deprecation")
public class ScrollRecipe {
	private static ScrollManager sm = new ScrollManager();
public static void createRecipe() {
		
		//Prevent double creation of recipes
		try {
		
			//Create scroll Itemstack
			ItemStack rescroll = new ItemStack(Material.PAPER,1);
			
			ItemMeta meta = rescroll.getItemMeta();
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', ScrollManager.scrollName));
	
			rescroll.setItemMeta(meta);
			
			//Create shard Itemstack
			ItemStack cscroll = sm.createScroll("CHEAP");
			
			//Recipe Choice
			ExactChoice cheapscroll = new RecipeChoice.ExactChoice(cscroll);
			
			//Create key
			NamespacedKey scrollkey = new NamespacedKey(Plugin.plugin, "me.duppyiscool.scrollkey");
			
			//Form shapeless recipe
			ShapedRecipe diamondrecipe = new ShapedRecipe(scrollkey, rescroll);
			
			//Add "diamond shards" (9) to recipe
			diamondrecipe.shape("ECE","CNC","ECE");
			diamondrecipe.setIngredient('C', cheapscroll);
			diamondrecipe.setIngredient('E', Material.ENDER_EYE);
			diamondrecipe.setIngredient('N', Material.NETHER_STAR);
			
			//Add recipe to bukkit
			Bukkit.getServer().addRecipe(diamondrecipe);
			
		}catch(Exception e) {return;}
	}
}
