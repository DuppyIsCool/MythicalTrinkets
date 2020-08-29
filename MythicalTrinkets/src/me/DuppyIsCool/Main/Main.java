package me.DuppyIsCool.Main;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.DuppyIsCool.DiamondShards.ShardRecipe;
import me.DuppyIsCool.Scrolls.ConfigManager;
import me.DuppyIsCool.Scrolls.ScrollManager;
import me.DuppyIsCool.Scrolls.ScrollRecipe;
import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin implements CommandExecutor{
	
	ConfigManager cfgm = new ConfigManager();
	ScrollManager sm = new ScrollManager();
	public void onEnable() {
		//Starting Message
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "Attempting to load MythicalTrinkets");
		//Save Instance of Main to Plugin
		Plugin.plugin = this;
		this.getCommand("givescroll").setExecutor(this);
		this.getCommand("givecheapscroll").setExecutor(this);
		//Setup Config
		cfgm.setup();
		sm.setupDefaultConfig();
		sm.setupConfig();
		
		//Set event listener
		Bukkit.getServer().getPluginManager().registerEvents(new Events(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new ScrollManager(), this);
		
		//Set recipes
		ShardRecipe.createRecipe();
		ScrollRecipe.createRecipe();
		
		
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Sucesfully loaded MythicalTrinkets");
	}
	
	public void onDisable() {
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "Attempting to disable MythicalTrinkets");
		sm.saveConfig();
		cfgm.saveScrolls();
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Sucesfully disabled MythicalTrinkets");
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("givescroll")) {
			if(sender instanceof Player) {
				Player p = (Player) sender;
				if(p.hasPermission("scrolls.givescroll") || p.isOp()) {
					p.getInventory().addItem(sm.createScroll("REUSEABLE"));
					p.sendMessage(ChatColor.GREEN + "A reuseable scroll has been added to your inventory.");
					return true;
				}
				else {
					p.sendMessage(ChatColor.RED + "You do not have permission to use this command");
					return true;
				}
			}
			else {
				sender.sendMessage(ChatColor.RED + "You must be a player to use this command");
				return true;
			}
		}
		
		else if(command.getName().equalsIgnoreCase("givecheapscroll")) {
			if(sender instanceof Player) {
				Player p = (Player) sender;
				if(p.hasPermission("scrolls.givecheapscroll") || p.isOp()) {
					p.getInventory().addItem(sm.createScroll("CHEAP"));
					p.sendMessage(ChatColor.GREEN + "A cheap scroll has been added to your inventory.");
					return true;
				}
				else {
					p.sendMessage(ChatColor.RED + "You do not have permission to use this command");
					return true;
				}
			}
			else {
				sender.sendMessage(ChatColor.RED + "You must be a player to use this command");
				return true;
			}
		}
		
        return true;
    }
}
