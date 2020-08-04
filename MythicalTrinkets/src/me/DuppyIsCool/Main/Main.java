package me.DuppyIsCool.Main;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.DuppyIsCool.DiamondShards.ShardRecipe;
import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin{
	
	
	public void onEnable() {
		//Starting Message
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "Attempting to load MythicalTrinkets");
		//Save Instance of Main to Plugin
		Plugin.plugin = this;
		//Set event listener
		Bukkit.getServer().getPluginManager().registerEvents(new Events(), this);
		
		//Set recipes
		ShardRecipe.createRecipe();
		
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Sucesfully loaded MythicalTrinkets");
	}
	
	public void onDisable() {
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "Attempting to disable MythicalTrinkets");
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Sucesfully disabled MythicalTrinkets");
	}
}
