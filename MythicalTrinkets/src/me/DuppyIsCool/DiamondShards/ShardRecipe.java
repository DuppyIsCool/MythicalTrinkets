package me.DuppyIsCool.DiamondShards;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import me.DuppyIsCool.Main.Plugin;
import net.md_5.bungee.api.ChatColor;

@SuppressWarnings("deprecation")
public class ShardRecipe {
	
	public static void createRecipe() {
		
		//Prevent double creation of recipes
		try {
		
			//Create diamond Itemstack
			ItemStack diamond = new ItemStack(Material.DIAMOND,1);
			
			//Create shard Itemstack
			ItemStack shard = new ItemStack(Material.PRISMARINE_CRYSTALS,1);
			
			//Set meta
			ItemMeta meta = shard.getItemMeta();
			ArrayList<String> lore = new ArrayList<String>();
			lore.add(ChatColor.LIGHT_PURPLE + ""+ChatColor.ITALIC+"A small fragment of a diamond");
			meta.setLore(lore);
	
			meta.setDisplayName(ChatColor.BLUE + "Diamond Shard");
	
			shard.setItemMeta(meta);
			
			//Recipe Choice
			ExactChoice diamondShard = new RecipeChoice.ExactChoice(shard);
			
			//Create key
			NamespacedKey diamondkey = new NamespacedKey(Plugin.plugin, "diamondkey");
			
			//Form shapeless recipe
			ShapedRecipe diamondrecipe = new ShapedRecipe(diamondkey, diamond);
			
			//Add "diamond shards" (9) to recipe
			diamondrecipe.shape("DDD","DDD","DDD");
			diamondrecipe.setIngredient('D', diamondShard);
			
			//Add recipe to bukkit
			Bukkit.getServer().addRecipe(diamondrecipe);
			//Create key
			NamespacedKey shardkey = new NamespacedKey(Plugin.plugin, "shardkey");
			shard.setAmount(9);
			//Form shapeless recipe
			ShapelessRecipe shardrecipe = new ShapelessRecipe(shardkey, shard);
			shardrecipe.addIngredient(1, Material.DIAMOND);
			Bukkit.addRecipe(shardrecipe);
		}catch(Exception e) {return;}
	}
}
