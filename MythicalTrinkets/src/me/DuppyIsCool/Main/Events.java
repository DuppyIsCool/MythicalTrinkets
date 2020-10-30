package me.DuppyIsCool.Main;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import net.md_5.bungee.api.ChatColor;

public class Events implements Listener{
	
	//Prevents renaming of diamond shards (to prevent griefs or loss of shards)
	@EventHandler(priority = EventPriority.MONITOR)
	public void onInventoryClick(PrepareAnvilEvent e){
		if(e.getInventory().getItem(0) != null) {
			if(e.getInventory().getItem(0).getType().equals(Material.PRISMARINE_CRYSTALS)) {
				if(e.getInventory().getItem(0).getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.BLUE+"Diamond Shard")) {
					e.setResult(null);
				}
			}
		}
		else if(e.getInventory().getItem(1) != null) {
			if(e.getInventory().getItem(1).getType().equals(Material.PRISMARINE_CRYSTALS)) {
				if(e.getInventory().getItem(1).getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.BLUE+"Diamond Shard")) {
					e.setResult(null);
				}
			}
		}
	}
	
}
