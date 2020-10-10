package me.DuppyIsCool.Vaal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import me.DuppyIsCool.Main.Plugin;
import me.DuppyIsCool.Scrolls.ScrollManager;

public class Orbs implements Listener{
	public static ArrayList<Enchantment> blockedEnchants;
	public static ArrayList<String> Armormaterial = new ArrayList<>(Arrays.asList("LEATHER", "GOLDEN", "CHAINMAIL", "IRON", "DIAMOND", "NETHERITE"));
	public static ArrayList<String> Weaponmaterial = new ArrayList<>(Arrays.asList("WOODEN", "STONE", "GOLDEN", "IRON", "DIAMOND", "NETHERITE"));
	public static ArrayList<String> loreList;
	public static ArrayList<Player> open;
	public static String orbDisplayName, displayName,failMessage,modifiedmessage;
	public static double enchantmentUpgradeChance,upgradeChance,downgradeChance,failChance,dropChance;
	public static boolean allowLoreItems,spawnerDrops;
	public static HashMap<Integer,Double> etable;
	
	public void setup() {
		Plugin.plugin.saveDefaultConfig();
		blockedEnchants = new ArrayList<Enchantment>();
		loreList = new ArrayList<String>();
		etable = new HashMap<Integer,Double>();
		open = new ArrayList<Player>();
		
		//Setup blocked enchantments
		ArrayList<String> temp = (ArrayList<String>) Plugin.plugin.getConfig().getStringList("orbs.blockedenchants");
		for(String e : temp) {
			if(getEnchantment(e) != null) {
				blockedEnchants.add(getEnchantment(e));
			}
		}
		//Setup Etable Level Map
		for(String num : Plugin.plugin.getConfig().getConfigurationSection("orbs.etable").getKeys(false)) {
			etable.put(Integer.parseInt(num), Plugin.plugin.getConfig().getDouble("orbs.etable."+num));
		}
		//Setup orb lore
		for(String s : Plugin.plugin.getConfig().getStringList("orbs.orbLore")) {
			loreList.add(ChatColor.translateAlternateColorCodes('&', s));
		}
		
		//Setup Values
		enchantmentUpgradeChance = Plugin.plugin.getConfig().getDouble("orbs.enchantmentUpgradeChance");
		upgradeChance = Plugin.plugin.getConfig().getDouble("orbs.itemUpgradeChance");
		downgradeChance = Plugin.plugin.getConfig().getDouble("orbs.downgradeChance");
		failChance = Plugin.plugin.getConfig().getDouble("orbs.failChance");
		dropChance = Plugin.plugin.getConfig().getDouble("orbs.dropChance");
		failMessage = Plugin.plugin.getConfig().getString("orbs.failMessage");
		displayName = Plugin.plugin.getConfig().getString("orbs.displayName");
		allowLoreItems = Plugin.plugin.getConfig().getBoolean("orbs.allowLoreItems");
		spawnerDrops = Plugin.plugin.getConfig().getBoolean("orbs.spawnerDrops");
		orbDisplayName = Plugin.plugin.getConfig().getString("orbs.orbDisplayName");
		modifiedmessage = Plugin.plugin.getConfig().getString("orbs.modifiedmessage");
		
		if((upgradeChance + downgradeChance + enchantmentUpgradeChance + failChance) != 1) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error: Total probability in the chance table does not add up to 1!");
		}
		
	}
	
	
	public ItemStack createOrb() {
		ItemStack orb = new ItemStack(Material.HEART_OF_THE_SEA, 1);
		ItemMeta orbMeta = orb.getItemMeta();
		orbMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', orbDisplayName));
		orbMeta.setLore(loreList);
		orb.setItemMeta(orbMeta);
		return orb;		
	}
	public boolean isOrb(ItemStack i) {
		if(i != null && i.getType() != Material.AIR) {
			if(i.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', orbDisplayName))) {
				return true;
			}
		}
		return false;
	}
	
	@EventHandler
	public void useOrb(PlayerInteractEvent e){
		Player player = e.getPlayer();
        Action action = e.getAction();
        // Check if the player has scroll in main hand
        if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
	        if(e.getHand().equals(EquipmentSlot.HAND)) {
		        if(isOrb(player.getInventory().getItemInMainHand())) {
		            // Right clicking
		            if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK ) {
		            	if(getValidItems(player) > 0) {
			            	Inventory i = Bukkit.createInventory(null, 45, ChatColor.DARK_PURPLE + "Click the item you wish to modify");
			            	for(ItemStack items : player.getInventory().getContents()) {
			            		if(items != null) {
				            		if(isIupgrade(items) || items.getEnchantments().size() > 0)
				            			if(items.getItemMeta().getLore() != null) {
				            				if(!items.getItemMeta().getLore().contains(ChatColor.translateAlternateColorCodes('&', modifiedmessage)))
				            					if(items.getItemMeta().getLore().size() > 0 || allowLoreItems)
				            						i.addItem(items);
				            			}	
				            			else {
				            				i.addItem(items);
				            			}
			            		}
			            	}
			            	open.add(player);
			            	player.openInventory(i);
			            	
			            	if(player.getInventory().getItemInMainHand().getAmount() == 1)
			            		player.getInventory().setItemInMainHand(null);
		            		else {
		            			player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount()-1);
		            		}
			            	return;
			            }
		            	else {
		            		player.sendMessage(ChatColor.RED + "You do not have any valid orb items in your inventory!");
		            	}
		            }
		        }
	        }
        }
	}
	
	@EventHandler 
	public void useInventory(InventoryClickEvent e){
		try {
			if(e.getView().getTitle().equals(ChatColor.DARK_PURPLE + "Click the item you wish to modify")) {
				if(e.getCurrentItem() != null && e.getInventory() != null && e.getClickedInventory() != null) {
					if(e.getInventory().getItem(e.getRawSlot()) != null) {
						
						//Removing the item from the player's inventory/armor
						ItemStack item = e.getInventory().getItem(e.getRawSlot());
						boolean done = false, changed = false;
						for(ItemStack i : e.getWhoClicked().getInventory().getArmorContents()) {
							if(i!= null)
								if(i.equals(item)) {
									i.setAmount(0);
									done = true;
									break;
								}
						}
						
						if(!done)
							for(ItemStack i : e.getWhoClicked().getInventory()) {
								if(i!= null)
									if(i.equals(item)) {
										i.setAmount(0);
										done = true;
										break;
									}
							}
						
						//Change the item's meta
						ItemMeta itemmeta = item.getItemMeta();
						if(itemmeta.hasDisplayName())
							itemmeta.setDisplayName(capitalizeWord(ChatColor.translateAlternateColorCodes('&', displayName.replaceAll("%currentname%", itemmeta.getDisplayName()))));
						else
							itemmeta.setDisplayName(capitalizeWord(ChatColor.translateAlternateColorCodes('&', displayName.replaceAll("%currentname%", item.getType().name().replace("_", " ").toLowerCase()))));
						ArrayList<String> templore = new ArrayList<String>();
						if(itemmeta.getLore() != null)
							for(String s : itemmeta.getLore()) {
								templore.add(s);
							}
						templore.add(ChatColor.translateAlternateColorCodes('&', modifiedmessage));
						itemmeta.setLore(templore);
						item.setItemMeta(itemmeta);
						ArrayList<Enchantment> validEnchants = new ArrayList<Enchantment>();
						
						//Create map of valid enchantments to upgrade
						if(item.getEnchantments().size() > 0) {
							for(Enchantment a : item.getEnchantments().keySet()) {
								if(!blockedEnchants.contains(a)) {
									validEnchants.add(a);
								}
							}
						}
						
						//Enchantment if it is probable
						double rand = Math.random();
						if(!changed)
							if((validEnchants.size() > 0 && rand <= enchantmentUpgradeChance) || !isIupgrade(item)) {
								int num = (int) ((Math.random() * (validEnchants.size())));
								for(int i = 0; i < validEnchants.size(); i++) {
									if(i == num) {
										int level = item.getEnchantmentLevel(validEnchants.get(i));
										
										Integer levelmodifier = null;
										Integer tempn = null;
										for(Integer n : etable.keySet()) {
											tempn = n;
											double temprand = Math.random();
											if(temprand <= etable.get(n)) {
												levelmodifier = n;
												break;
											}
										}	
										
										if(levelmodifier == null && tempn != null) {
											levelmodifier = tempn;
										}
										else if(levelmodifier == null)
											levelmodifier = 1;
										
										item.removeEnchantment(validEnchants.get(i));
										item.addUnsafeEnchantment(validEnchants.get(i), level+levelmodifier);
										changed = true;
									}
								}
							}
						double tempprob = 0;
						if(validEnchants.size() == 0) {
							tempprob = enchantmentUpgradeChance/3;
						}
						
						rand = Math.random();
						if(!changed)
							if(rand <= (failChance + tempprob)) {
								changed = true;
							}
						//Item Upgrades
						if(!changed)
							if(isIupgrade(item)) {
								String type = item.getType().toString().substring(0, item.getType().toString().indexOf('_'));
								boolean up = true,down = true;
								
								if(type.equalsIgnoreCase("WOODEN")) {
									down = false;
								}
								else if(type.equalsIgnoreCase("LEATHER")) {
									down = false;
								}
								
								if(type.equalsIgnoreCase("NETHERITE"))
									up = false;
								
								double random = Math.random();
								if(up) {

									if(random <= (upgradeChance + tempprob)) {
										
										if(isArmor(item)) {
											type = Armormaterial.get(Armormaterial.indexOf(type)+1);
											
											item.setType(Material.getMaterial(type + item.getType().toString().substring(item.getType().toString().indexOf('_'))));
										}
										else if(isWeapon(item)) {
											type = Weaponmaterial.get(Weaponmaterial.indexOf(type)+1);
											
											item.setType(Material.getMaterial(type + item.getType().toString().substring(item.getType().toString().indexOf('_'))));
										}
										
									}
									else if(!down) {
										if(isArmor(item)) {
											type = Armormaterial.get(Armormaterial.indexOf(type)+1);
											
											item.setType(Material.getMaterial(type + item.getType().toString().substring(item.getType().toString().indexOf('_'))));
										}
										else if(isWeapon(item)) {
											type = Weaponmaterial.get(Weaponmaterial.indexOf(type)+1);
											
											item.setType(Material.getMaterial(type + item.getType().toString().substring(item.getType().toString().indexOf('_'))));
										}
									}
									
									else {
										if(isArmor(item)) {
											type = Armormaterial.get(Armormaterial.indexOf(type)-1);
											
											item.setType(Material.getMaterial(type + item.getType().toString().substring(item.getType().toString().indexOf('_'))));
										}
										else if(isWeapon(item)) {
											type = Weaponmaterial.get(Weaponmaterial.indexOf(type)-1);
											
											item.setType(Material.getMaterial(type + item.getType().toString().substring(item.getType().toString().indexOf('_'))));
										}
									}
									
								}
								else{
									if(isArmor(item)) {
										type = Armormaterial.get(Armormaterial.indexOf(type)-1);
										
										item.setType(Material.getMaterial(type + item.getType().toString().substring(item.getType().toString().indexOf('_'))));
									}
									else if(isWeapon(item)) {
										type = Weaponmaterial.get(Weaponmaterial.indexOf(type)-1);
										
										item.setType(Material.getMaterial(type + item.getType().toString().substring(item.getType().toString().indexOf('_'))));
									}
								}
								changed = true;
							}
						//End of item editing
						e.getWhoClicked().getInventory().addItem(item);
						open.remove(e.getWhoClicked());
						e.getWhoClicked().closeInventory();
						
					}
				}
			}
		}catch(ArrayIndexOutOfBoundsException error) {e.setCancelled(true);}
	}
	
	//Prevent players from picking up items if they are in the inventory
	@EventHandler
	public void onPickup(EntityPickupItemEvent event){
		if(event.getEntity() instanceof Player){
			Player p = (Player) event.getEntity();
			if(p.getOpenInventory().getTitle().equals(ChatColor.DARK_PURPLE + "Click the item you wish to modify"))
				event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onMobSpawn(CreatureSpawnEvent e) {
		if(!spawnerDrops) {
			if(e.getSpawnReason().equals(SpawnReason.SPAWNER)) {
				if(ScrollManager.mobs.contains(e.getEntityType())) {
					e.getEntity().setMetadata("orbTag", new FixedMetadataValue(Plugin.plugin, true));
				}
			}
		}
	}
	
	@EventHandler
	public void onMobDeath(EntityDeathEvent e) {
		if(e.getEntity().getKiller() instanceof Player) {
			if(ScrollManager.getNearbyEntities(e.getEntity().getLocation(),ScrollManager.maxEntitiesRadius).length <= ScrollManager.maxEntities) {
				if(ScrollManager.mobs.contains(e.getEntityType())) {
					if(e.getEntity().hasMetadata("orbTag")) {
						return;
					}
					else {
						if(Math.random() <= dropChance) {
							e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), createOrb());
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onInventoryClick(PrepareAnvilEvent e){
		if(e.getInventory().getItem(0) != null) {
			if(isOrb(e.getInventory().getItem(0))) {
				e.setResult(null);
			}
			
			else if(e.getInventory().getItem(0).getItemMeta().hasLore())
				if(e.getInventory().getItem(0).getItemMeta().getLore().contains(ChatColor.translateAlternateColorCodes('&', modifiedmessage))){
					e.setResult(null);
				}
		}
		else if(e.getInventory().getItem(1) != null) {
			if(isOrb(e.getInventory().getItem(1))) {
				e.setResult(null);
			}
			else if(e.getInventory().getItem(1).getItemMeta().hasLore())
				if(e.getInventory().getItem(1).getItemMeta().getLore().contains(ChatColor.translateAlternateColorCodes('&', modifiedmessage))){
					e.setResult(null);
				}
		}
	}
	
	@EventHandler
	public void playerCloseInventory(InventoryCloseEvent e) {
		if(open.contains(e.getPlayer())) {
			e.getPlayer().getInventory().addItem(createOrb());
			open.remove(e.getPlayer());
		}
	}
	
	public void onPlayerClickOnItem(InventoryClickEvent e){
        if(e.getRawSlot() == e.getSlot() && e.getView().getTitle().equals(ChatColor.DARK_PURPLE + "Click the item you wish to modify")){
        	e.setCancelled(true);
    	} 
	}
	
	private boolean isIupgrade(ItemStack itemStack) {
        if (itemStack == null)
            return false;
        final String typeNameString = itemStack.getType().toString();
        if (typeNameString.endsWith("_HELMET")
                || typeNameString.endsWith("_CHESTPLATE")
                || typeNameString.endsWith("_LEGGINGS")
                || typeNameString.endsWith("_BOOTS")
                || typeNameString.endsWith("_AXE")
                || typeNameString.endsWith("_PICKAXE")
                || typeNameString.endsWith("_HOE")
                || typeNameString.endsWith("_SHOVEL")
                || typeNameString.endsWith("_SWORD")) {
            return true;
        }

        return false;
    }
	
	private boolean isArmor(ItemStack itemstack) {
		 if (itemstack == null)
	            return false;
	        final String typeNameString = itemstack.getType().toString();
	        if (typeNameString.endsWith("_HELMET")
	                || typeNameString.endsWith("_CHESTPLATE")
	                || typeNameString.endsWith("_LEGGINGS")
	                || typeNameString.endsWith("_BOOTS")) {
	            return true;
	        }
			return false;

	}
	
	private boolean isWeapon(ItemStack itemStack) {
		if (itemStack == null)
            return false;
        final String typeNameString = itemStack.getType().toString();
        if (typeNameString.endsWith("_AXE")
                || typeNameString.endsWith("_PICKAXE")
                || typeNameString.endsWith("_HOE")
                || typeNameString.endsWith("_SHOVEL")
                || typeNameString.endsWith("_SWORD")) {
            return true;
        }

        return false;
	}
	
	public static int getAmount(Player player)
	{
	        PlayerInventory inventory = player.getInventory();
	        ItemStack[] items = inventory.getContents();
	        int has = 0;
	        for (ItemStack item : items)
	        {
	            if ((item != null))
	            {
	                has ++;
	            }
	        }
	        return has;
    }
	
	public static int getAmount(Player player, Material id)
	{
	        PlayerInventory inventory = player.getInventory();
	        ItemStack[] items = inventory.getContents();
	        int has = 0;
	        for (ItemStack item : items)
	        {
	            if ((item != null) && (item.getType().equals(id)) && (item.getAmount() > 0))
	            {
	                has += item.getAmount();
	            }
	        }
	        return has;
    }
	
	public static String capitalizeWord(String str){  
	    String words[]=str.split("\\s");  
	    String capitalizeWord="";  
	    for(String w:words){  
	        String first=w.substring(0,1);  
	        String afterfirst=w.substring(1);
	        capitalizeWord+=first.toUpperCase()+afterfirst+" ";  
	    }  
	    return capitalizeWord.trim();  
	} 
	
	public int getValidItems(Player p) {
		int amount = 0;
		ArrayList<Enchantment> validEnchants = new ArrayList<Enchantment>();
		
		
		for(ItemStack item : p.getInventory().getContents()) {
			if(item != null) {
				validEnchants = new ArrayList<Enchantment>();
				
				if(item.getEnchantments().size() > 0) {
					for(Enchantment a : item.getEnchantments().keySet()) {
						if(!blockedEnchants.contains(a)) {
							validEnchants.add(a);
						}
					}
				}
				
				if ((isIupgrade(item) || validEnchants.size() > 0))
	            {
					if(item.getItemMeta().hasLore()) {
						if(!item.getItemMeta().getLore().contains(ChatColor.translateAlternateColorCodes('&', modifiedmessage)))
							amount ++;
					}
					else
						amount++;
	            }
			}
		}
		return amount;
	}
	
	public static Enchantment getEnchantment(String name) {
	    switch (name.trim().toLowerCase()) {
	        case "protection":
	            return Enchantment.PROTECTION_ENVIRONMENTAL;
	        case "fireprotection":
	            return Enchantment.PROTECTION_FIRE;
	        case "featherfalling":
	            return Enchantment.PROTECTION_FALL;
	        case "blastprotection":
	            return Enchantment.PROTECTION_EXPLOSIONS;
	        case "projectileprotection":
	            return Enchantment.PROTECTION_PROJECTILE;
	        case "respiration":
	            return Enchantment.OXYGEN;
	        case "aquaaffinity":
	            return Enchantment.WATER_WORKER;
	        case "thorns":
	            return Enchantment.THORNS;
	        case "unbreaking":
	            return Enchantment.DURABILITY;
	        case "sharpness":
	            return Enchantment.DAMAGE_ALL;
	        case "smite":
	            return Enchantment.DAMAGE_UNDEAD;
	        case "baneofarthropods":
	            return Enchantment.DAMAGE_ARTHROPODS;
	        case "knockback":
	            return Enchantment.KNOCKBACK;
	        case "fireaspect":
	            return Enchantment.FIRE_ASPECT;
	        case "looting":
	            return Enchantment.LOOT_BONUS_MOBS;
	        case "efficiency":
	            return Enchantment.DIG_SPEED;
	        case "silk_touch":
	            return Enchantment.SILK_TOUCH;
	        case "fortune":
	            return Enchantment.LOOT_BONUS_BLOCKS;
	        case "power":
	            return Enchantment.ARROW_DAMAGE;
	        case "punch":
	            return Enchantment.ARROW_KNOCKBACK;
	        case "flame":
	            return Enchantment.ARROW_FIRE;
	        case "infinity":
	            return Enchantment.ARROW_INFINITE;
	        case "loyalty":
	        	return Enchantment.LOYALTY;
	        case "soul_speed":
	        	return Enchantment.SOUL_SPEED;
	        case "channeling":
	        	return Enchantment.CHANNELING;
	        case "curse_of_binding":
	        	return Enchantment.BINDING_CURSE;
	        case "curse_of_vanishing":
	        	return Enchantment.VANISHING_CURSE;
	        case "mending":
	        	return Enchantment.MENDING;
	        case "depth_stider":
	        	return Enchantment.DEPTH_STRIDER;
	        case "frost_walker":
	        	return Enchantment.FROST_WALKER;
	        case "lure":
	        	return Enchantment.LURE;
	        case "luck_of_the_sea":
	        	return Enchantment.LUCK;
	        case "multishot":
	        	return Enchantment.MULTISHOT;
	        case "quick_charge":
	        	return Enchantment.QUICK_CHARGE;
	        case "riptide":
	        	return Enchantment.RIPTIDE;
	        case "sweeping_edge":
	        	return Enchantment.SWEEPING_EDGE;
	        default:
	        	Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Failed to get enchantment: "+name);
	            return null;
	    }
	}
}

