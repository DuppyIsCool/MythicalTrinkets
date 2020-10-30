package me.DuppyIsCool.Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.DuppyIsCool.Scrolls.ConfigManager;
import me.DuppyIsCool.Scrolls.ScrollManager;

public class Achievements implements Listener {
	public ConfigManager cfgm = new ConfigManager();
	//String is the name of the achievement
	//UUID list is a list of players who have completed the achievement.
	public static HashMap<String,ArrayList<UUID>> map = new HashMap<String,ArrayList<UUID>>();
	
	public void setup() {
		//Setup presets
		map.put("Diamond Shard", new ArrayList<UUID>());
		map.put("A Scroll?", new ArrayList<UUID>());
		map.put("Teleportation on demand", new ArrayList<UUID>());
		map.put("Vaal Orb", new ArrayList<UUID>());
		map.put("'f' in chat", new ArrayList<UUID>());
		map.put("Spicy Rewards", new ArrayList<UUID>());
		
		//Get players who've completed the achievements
		if(cfgm.getAchievements().isConfigurationSection("achievements"))
			for(String ach : cfgm.getAchievements().getConfigurationSection("achievements").getKeys(false)) {
				ArrayList<UUID> players = new ArrayList<UUID>();
				for(String s : cfgm.getAchievements().getStringList("achievements."+ach)) {
					players.add(UUID.fromString(s));
				}
				map.put(ach, players);
			}

	}
	
	public void saveProgress() {
		for (Entry<String, ArrayList<UUID>> entry : map.entrySet()) {
			if(entry.getValue() != null) {
				ArrayList<String> temp = new ArrayList<String>();
				for(UUID u : entry.getValue())
					temp.add(u.toString());
				cfgm.getAchievements().set("achievements."+entry.getKey(), temp);
			}
		}  
		cfgm.saveAch();
	}
	
	public static void completeAchievement(String name, Player player) {
		if(!map.get(name).contains(player.getUniqueId())) {
			map.get(name).add(player.getUniqueId());
		
			//Print completion message
			if(name.equals("Spicy Rewards"))
				player.sendMessage("You have completed the achievement "+ChatColor.DARK_PURPLE +"["+name+"]");
			else
				player.sendMessage("You have completed the achievement "+ChatColor.GREEN +"["+name+"]");
		}
	}
	
	public void showAchievements(Player p) {
		Inventory i = Bukkit.createInventory(null, 36, ChatColor.DARK_RED + "Your Achievements");
		
		//Create Diamond Shard Item
		ItemStack d = new ItemStack(Material.PRISMARINE_SHARD,1);
		ItemMeta meta = d.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD + "Diamond Shard");
		if(map.get("Diamond Shard").contains(p.getUniqueId()))
			meta.setLore(Arrays.asList(new String[]{ChatColor.YELLOW+"You got a shard of a diamond!",
					ChatColor.WHITE+"Objective: Recieve/Craft/Pickup a diamond shard",
					ChatColor.WHITE+"Status: "+ChatColor.GREEN+"COMPLETED"}));
		else
			meta.setLore(Arrays.asList(new String[]{ChatColor.YELLOW+"You got a shard of a diamond!",
					ChatColor.WHITE+"Objective: Recieve/Craft/Pickup a diamond shard", 
					ChatColor.WHITE+"Status: "+ChatColor.YELLOW+"IN PROGRESS"}));
		d.setItemMeta(meta);
		i.setItem(10, d);
		
		//Scroll
		ItemStack s = new ItemStack(Material.PAPER,1);
		ItemMeta smeta = s.getItemMeta();
		smeta.setDisplayName(ChatColor.GOLD + "A Scroll?");
		if(map.get("A Scroll?").contains(p.getUniqueId()))
			smeta.setLore(Arrays.asList(new String[]{ChatColor.YELLOW+"You found a mysterious scroll",
					ChatColor.WHITE+"Objective: Recieve/Craft/Pickup",ChatColor.WHITE+"     a teleportation scroll",
					ChatColor.WHITE+"Status: "+ChatColor.GREEN+"COMPLETED"}));
		else
			smeta.setLore(Arrays.asList(new String[]{ChatColor.YELLOW+"You found a mysterious scroll",
					ChatColor.WHITE+"Objective: Recieve/Craft/Pickup",ChatColor.WHITE+ "     a cheap teleportation scroll", 
					ChatColor.WHITE+"Status: "+ChatColor.YELLOW+"IN PROGRESS"}));
		s.setItemMeta(smeta);
		i.setItem(12, s);
		
		//Teleportation
		ItemStack t = new ItemStack(Material.ENDER_PEARL,1);
		ItemMeta tmeta = t.getItemMeta();
		tmeta.setDisplayName(ChatColor.GOLD + "Teleportation on demand");
		if(map.get("Teleportation on demand").contains(p.getUniqueId()))
			tmeta.setLore(Arrays.asList(new String[]{ChatColor.YELLOW+"Like cable, but teleportation!",
					ChatColor.WHITE+"Objective: Recieve/Craft/Pickup",ChatColor.WHITE+"     a reusable teleportation scroll",
					ChatColor.WHITE+"Status: "+ChatColor.GREEN+"COMPLETED"}));
		else
			tmeta.setLore(Arrays.asList(new String[]{ChatColor.YELLOW+"Like cable, but teleportation!",
					ChatColor.WHITE+"Objective: Recieve/Craft/Pickup",ChatColor.WHITE+"     a reusable teleportation scroll", 
					ChatColor.WHITE+"Status: "+ChatColor.YELLOW+"IN PROGRESS"}));
		t.setItemMeta(tmeta);
		i.setItem(14, t);
		
		//Vaal Orb
		ItemStack v = new ItemStack(Material.HEART_OF_THE_SEA,1);
		ItemMeta vmeta = v.getItemMeta();
		vmeta.setDisplayName(ChatColor.GOLD + "Vaal Orb");
		if(map.get("Vaal Orb").contains(p.getUniqueId()))
			vmeta.setLore(Arrays.asList(new String[]{ChatColor.YELLOW+"A chaotic orb. Is it worth the risk?",
					ChatColor.WHITE+"Objective: Recieve/Craft/Pickup a vaal orb",
					ChatColor.WHITE+"Status: "+ChatColor.GREEN+"COMPLETED"}));
		else
			vmeta.setLore(Arrays.asList(new String[]{ChatColor.YELLOW+"A chaotic orb. Is it worth the risk?",
					ChatColor.WHITE+"Objective: Recieve/Craft/Pickup a vaal orb", 
					ChatColor.WHITE+"Status: "+ChatColor.YELLOW+"IN PROGRESS"}));
		v.setItemMeta(vmeta);
		i.setItem(16, v);
		
		//F in chat
		ItemStack f = new ItemStack(Material.WOODEN_SWORD,1);
		ItemMeta fmeta = f.getItemMeta();
		fmeta.setDisplayName(ChatColor.GOLD + "'f' in chat");
		if(map.get("'f' in chat").contains(p.getUniqueId()))
			fmeta.setLore(Arrays.asList(new String[]{ChatColor.YELLOW+"A tool was downgraded",
					ChatColor.WHITE+"Objective: Downgrade a tool using a vaal orb",
					ChatColor.WHITE+"Status: "+ChatColor.GREEN+"COMPLETED"}));
		else
			fmeta.setLore(Arrays.asList(new String[]{ChatColor.YELLOW+"A tool was downgraded",
					ChatColor.WHITE+"Objective: Downgrade a tool using a vaal orb", 
					ChatColor.WHITE+"Status: "+ChatColor.YELLOW+"IN PROGRESS"}));
		f.setItemMeta(fmeta);
		i.setItem(20, f);
		
		//Spicy
		ItemStack sp = new ItemStack(Material.NETHERITE_SWORD,1);
		ItemMeta spmeta = sp.getItemMeta();
		spmeta.setDisplayName(ChatColor.DARK_PURPLE + ""+ChatColor.BOLD+ "Spicy Rewards");
		if(map.get("Spicy Rewards").contains(p.getUniqueId()))
			spmeta.setLore(Arrays.asList(new String[]{"A tool received better enchantments",
					ChatColor.GOLD+"Objective: Upgrade a tool using a vaal orb",
					ChatColor.GOLD+"Status: "+ChatColor.GREEN+"COMPLETED"}));
		else
			spmeta.setLore(Arrays.asList(new String[]{"A tool received better enchantments",
					ChatColor.GOLD+"Objective: Upgrade a tool using a vaal orb", 
					ChatColor.GOLD+"Status: "+ChatColor.YELLOW+"IN PROGRESS"}));
		sp.setItemMeta(spmeta);
		i.setItem(24, sp);
		
		p.openInventory(i);
	}
	
	@EventHandler
	public void onUse(InventoryClickEvent e) {
		if(e.getView().getTitle().equals(ChatColor.DARK_RED + "Your Achievements"))
			e.setCancelled(true);	
	}
	
	@EventHandler
	public void pickup(EntityPickupItemEvent e) {
		if(e.getEntity() instanceof Player){
			Player p = (Player) e.getEntity();
			if(e.getItem().getItemStack().hasItemMeta()) {
				if(e.getItem().getItemStack().getItemMeta().getDisplayName().equals(
						ChatColor.translateAlternateColorCodes('&', Plugin.plugin.getConfig().getString("orbs.orbDisplayName")))) {
					completeAchievement("Vaal Orb", p);
				}
				else if (e.getItem().getItemStack().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', ScrollManager.scrollName)))
					completeAchievement("Teleportation on demand",p);
				else if (e.getItem().getItemStack().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', ScrollManager.cheapScrollName)))
					completeAchievement("A Scroll?",p);
				else if(e.getItem().getItemStack().getType().equals(Material.PRISMARINE_CRYSTALS)) {
					if(e.getItem().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.BLUE+"Diamond Shard")) {
						Achievements.completeAchievement("Diamond Shard",p);
					}
				}
			}
				
		}
	}
	
	@EventHandler
	public void dcraft(CraftItemEvent e) {
		if(e.getRecipe().getResult().getType().equals(Material.PRISMARINE_CRYSTALS)) {
			if(e.getRecipe().getResult().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.BLUE+"Diamond Shard")) {
				Achievements.completeAchievement("Diamond Shard",(Player) e.getWhoClicked());
			}
		}
	}
}
