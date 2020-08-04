package me.DuppyIsCool.DiamondShards;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import me.DuppyIsCool.Main.Plugin;
import net.md_5.bungee.api.ChatColor;

public class ShardRecipe {
	
	public static void createRecipe() {
		//Create itemstack
		ItemStack diamond = new ItemStack(Material.DIAMOND,1);
		//Create key
		NamespacedKey diamondkey = new NamespacedKey(Plugin.plugin, "diamondkey");
		
		//Form shapeless recipe
		ShapelessRecipe diamondrecipe = new ShapelessRecipe(diamondkey, diamond);
		
		//Add "diamond shards" (9) to recipe
		diamondrecipe.addIngredient(9,Material.PRISMARINE_CRYSTALS);
		
		//Add recipe to bukkit
		Bukkit.addRecipe(diamondrecipe);
		
		//Create Itemstack
		ItemStack shard = new ItemStack(Material.PRISMARINE_CRYSTALS,9);
		
		//Set meta
		ItemMeta meta = shard.getItemMeta();

		meta.setDisplayName(ChatColor.BLUE + "Diamond Shard");

		shard.setItemMeta(meta);
		//Create key
		NamespacedKey shardkey = new NamespacedKey(Plugin.plugin, "shardkey");
		
		//Form shapeless recipe
		ShapelessRecipe shardrecipe = new ShapelessRecipe(shardkey, shard);
		shardrecipe.addIngredient(1, Material.DIAMOND);
		Bukkit.addRecipe(shardrecipe);
	}
}
