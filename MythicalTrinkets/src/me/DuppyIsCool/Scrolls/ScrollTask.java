package me.DuppyIsCool.Scrolls;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import me.DuppyIsCool.Main.Plugin;

public class ScrollTask extends BukkitRunnable{
	Scroll s;
	public ScrollTask(Scroll s) {
		this.s = s;
		if(s.timeBeforeUse() < 0) {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "ERROR: A Scroll's time is less than or equal to 0 (ID: "+s.getID()+")");
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Disabling due to error");
			Bukkit.getPluginManager().disablePlugin(Plugin.plugin);
		}
	}
	
	@Override
	public void run() {
		if(s.timeBeforeUse() > 0) {
			s.setTimeBeforeUse(s.timeBeforeUse()-1);
		}
		else {
			s.setTimeBeforeUse(0);
			this.cancel();
		}
	}

}
