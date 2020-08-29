package me.DuppyIsCool.Scrolls;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.DuppyIsCool.Main.Main;
import me.DuppyIsCool.Main.Plugin;

public class ConfigManager {
	public Main plugin;
	//Variable Declaration
	private ConsoleCommandSender sender = Bukkit.getServer().getConsoleSender();
	private static FileConfiguration scrollcfg;
	private static File scrollfile;
	
	public void setup() {
		plugin = Plugin.plugin;
		
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}
		
		scrollfile = new File(plugin.getDataFolder(), "scrolls.yml");
		
		if (!scrollfile.exists()) {
			plugin.saveResource("scrolls.yml", false);
			sender.sendMessage(ChatColor.GREEN + "Scrolls.yml has been created");

		}
		
		scrollcfg = YamlConfiguration.loadConfiguration(scrollfile);
		
	}
	
	public FileConfiguration getScrolls() {
		return scrollcfg;
	}
	

	public void saveScrolls() {
		try {
			scrollcfg.save(scrollfile);
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Scrolls.yml has been saved");

		} catch (IOException e) {
			sender.sendMessage(ChatColor.RED + "Could not save the Scrolls.yml file");
		}
	}

	public void reloadScrolls() {
		scrollcfg = YamlConfiguration.loadConfiguration(scrollfile);
		sender.sendMessage(ChatColor.BLUE + "Scrolls.yml has been reload");

	}
}
	