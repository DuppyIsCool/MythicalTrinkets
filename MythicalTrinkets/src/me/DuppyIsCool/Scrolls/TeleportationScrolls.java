package me.DuppyIsCool.Scrolls;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TeleportationScrolls implements Listener {
	
	public ItemStack createCooldownScroll() {
		ItemStack scroll = new ItemStack(Material.GLOBE_BANNER_PATTERN,1);
		
		ItemMeta meta = scroll.getItemMeta();
		meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Teleportation Scroll");
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GREEN + "This scroll can be used to teleport to a saved location");
		lore.add(ChatColor.GREEN + "Current Location: N/A");
		lore.add(ChatColor.DARK_GRAY + "Right Click to save your position in the scroll");
		lore.add(ChatColor.DARK_GRAY + "Right Click to teleport to the saved position");
		meta.setLore(lore);
		
		scroll.setItemMeta(meta);
		return scroll;
	}
	
	public boolean isScroll(ItemStack i) {
		
		if(i.getType().equals(Material.GLOBE_BANNER_PATTERN)) {
			if(i.getItemMeta().getDisplayName().equals(ChatColor.LIGHT_PURPLE + "Teleportation Scroll")
			|| i.getItemMeta().getDisplayName().equals(ChatColor.LIGHT_PURPLE + "Cheap Teleportation Scroll")) {
				return true;
				
			}
		}
		return false;
	}
	
	public Location getScrollLocation(ItemStack i) {
		Location l;
		if(isScroll(i)) {
			String lore = i.getItemMeta().getLore().get(1);
			if(!lore.contains("N/A")) {
				Double x = Double.parseDouble(lore.substring(lore.indexOf(' '), lore.indexOf(',')));
			}
		}
		return null;
	}
	
	public void setScrollLocation(ItemStack i, Location l) {
		ArrayList<String> lore = (ArrayList<String>) i.getItemMeta().getLore();
		lore.set(1, ChatColor.GREEN + "Current Location: "+ChatColor.YELLOW+""+l.getBlockX() + ","+l.getBlockY()+","+l.getBlockZ()+". World: "+l.getWorld().getName());
		i.getItemMeta().setLore(lore);
	}
	
	@EventHandler
	public void useScroll(PlayerInteractEvent e) {
		if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if(e.getItem() != null) {
				if(isScroll(e.getItem())) {
				
					
				}
			}
		}
	}
	
}
