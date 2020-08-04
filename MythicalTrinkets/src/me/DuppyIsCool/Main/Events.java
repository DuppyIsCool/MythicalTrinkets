package me.DuppyIsCool.Main;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;

public class Events implements Listener{
	
	//Prevents Crafting of Diamonds using normal Prismarine Shards
	@EventHandler
	public void restrictCrafting(PrepareItemCraftEvent e) {
		boolean check = true;
		//Checks if recipe is finished
		if(e.getRecipe() != null) {
			ItemStack resultItem = e.getRecipe().getResult();
			
			//Check if the result is a diamond
			if(resultItem.getType().equals(Material.DIAMOND)) {
				
				//Loop through the crafting grid
				for(ItemStack item: e.getInventory().getMatrix()) {
					//If the item exists
					if(item != null) {
						if(item.getType().equals(Material.PRISMARINE_CRYSTALS)) {
							if(item.hasItemMeta()) {
								//If the item is a prismarine shard
								if(!item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.BLUE + "Diamond Shard")) {
									check = false;
									break;
								}
							}
							else {
								check = false;
								break;}
						}
					}
				}
				
				if(check == false) {
					e.getInventory().setResult(null);
				}
			}
		}
	}
	
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
