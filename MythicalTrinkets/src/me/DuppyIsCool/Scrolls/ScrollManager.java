package me.DuppyIsCool.Scrolls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;

import me.DuppyIsCool.Main.Plugin;

public class ScrollManager implements Listener{
	public static ArrayList<Scroll> activeScrolls = new ArrayList<Scroll>();
	public static int cooldownTime,maxEntities,maxEntitiesRadius,tpdelay;
	public static String scrollName,cheapScrollName,tpmsg;
	public static boolean spawnerDrops,doubleDrops;
	public static ArrayList<EntityType> mobs = new ArrayList<EntityType>();
	public static double cheapScrollDrop,scrollDrop;
	 ConfigManager cfgm = new ConfigManager();
	public static HashMap<Player,TeleportTask> tpplayers = new HashMap<Player,TeleportTask>();
	
	public void setupDefaultConfig() {
		Plugin.plugin.saveDefaultConfig();
		
		//Get config defaults
		try {
		cooldownTime = Plugin.plugin.getConfig().getInt("scrolls.time");
		maxEntities = Plugin.plugin.getConfig().getInt("scrolls.maxEntities");
		maxEntitiesRadius = Plugin.plugin.getConfig().getInt("scrolls.maxEntitiesRadius");
		scrollName = Plugin.plugin.getConfig().getString("scrolls.scrollName");
		cheapScrollName = Plugin.plugin.getConfig().getString("scrolls.cheapScrollName");
		ArrayList<String> temp = (ArrayList<String>) Plugin.plugin.getConfig().getStringList("scrolls.mobDrops");
		for(String e : temp)
			mobs.add(EntityType.valueOf(e));
		cheapScrollDrop = Plugin.plugin.getConfig().getDouble("scrolls.cheapScrollDrop");
		scrollDrop = Plugin.plugin.getConfig().getDouble("scrolls.scrollDrop");
		
		spawnerDrops = Plugin.plugin.getConfig().getBoolean("scrolls.spawnerDrops");
		doubleDrops = Plugin.plugin.getConfig().getBoolean("scrolls.doubleDrops");
		tpdelay = Plugin.plugin.getConfig().getInt("scrolls.tpdelay");
		tpmsg = Plugin.plugin.getConfig().getString("scrolls.tpdelaymsg");
		} catch(Exception ex) {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Error setting up config. Did you misenter values?");
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Disabling Plugin.");
			Bukkit.getServer().getPluginManager().disablePlugin(Plugin.plugin);
		}
	}
	
	public void setupConfig() {
		for(String ID : cfgm.getScrolls().getKeys(false)) {
			Scroll s = new Scroll();
			s.setID(Integer.parseInt(ID));
			s.setType(cfgm.getScrolls().getString(ID+".type"));
			s.setTimeBeforeUse(cfgm.getScrolls().getInt(ID+".timeBeforeUse"));
			if(cfgm.getScrolls().getConfigurationSection(ID+".location") != null) {
				try {
					World w = Bukkit.getServer().getWorld(cfgm.getScrolls().getString(ID+".location.world"));
					int x = cfgm.getScrolls().getInt(ID+".location.x");
					int y = cfgm.getScrolls().getInt(ID+".location.y");
					int z = cfgm.getScrolls().getInt(ID+".location.z");
					
					s.setLocation(new Location(w,x,y,z));
				}
				//This catch can occur if the world is no longer valid (ex: has been deleted).
				catch(NullPointerException e) {
					Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error setting scroll "+ID+ " 's location. Is the world "
							+cfgm.getScrolls().getString(ID+".location.world")+" still valid?");
				}
			}
			
			if(cfgm.getScrolls().getString(ID+".lastUser") != null) {
				s.setLastUser(UUID.fromString(cfgm.getScrolls().getString(ID+".lastUser")));
			}
			
			if(cfgm.getScrolls().getString(ID+".lastUserName") != null) {
				s.setLastUsername(cfgm.getScrolls().getString(ID+".lastUserName"));
			}
			
			if(s.timeBeforeUse() > 0) {
				@SuppressWarnings("unused")
				BukkitTask scrollTask = new ScrollTask(s).runTaskTimer(Plugin.plugin,0,20);
			}
			
			activeScrolls.add(s);
		}
	}
	
	public void saveConfig() {
		//Being scroll saving
		for(Scroll s : activeScrolls) {
			int id = s.getID();
			cfgm.getScrolls().set(id + ".type", s.getType());
			cfgm.getScrolls().set(id + ".timeBeforeUse",s.timeBeforeUse());
			
			if(s.getLocation() != null) {
				cfgm.getScrolls().set(id + ".location.world", s.getLocation().getWorld().getName());
				cfgm.getScrolls().set(id + ".location.x", s.getLocation().getX());
				cfgm.getScrolls().set(id + ".location.y", s.getLocation().getY());
				cfgm.getScrolls().set(id + ".location.z", s.getLocation().getZ());
			}
			
			if(s.getLastUser() != null) {
				cfgm.getScrolls().set(id + ".lastUser", s.getLastUser().toString());
			}
			if(s.getLastUsername() != null) {
				cfgm.getScrolls().set(id + ".lastUserName", s.getLastUsername());
			}
		}
		//End Scroll Saving
	}
	
	//EVENTS
	
	//Tag creatures if they are spawned via spawners and spawner drops are not allowed
	@EventHandler
	public void onMobSpawn(CreatureSpawnEvent e) {
		if(!spawnerDrops) {
			if(e.getSpawnReason().equals(SpawnReason.SPAWNER)) {
				if(mobs.contains(e.getEntityType())) {
					e.getEntity().setMetadata("scrollTag", new FixedMetadataValue(Plugin.plugin, true));
				}
			}
		}
	}
	
	@EventHandler
	public void onMobDeath(EntityDeathEvent e) {
		if(e.getEntity().getKiller() instanceof Player) {
			if(getNearbyEntities(e.getEntity().getLocation(),maxEntitiesRadius).length <= maxEntities) {
				if(mobs.contains(e.getEntityType())) {
					if(e.getEntity().hasMetadata("scrollTag")) {
						return;
					}
					else {
						//Has a chance to drop double if enabled
						if(doubleDrops ) {
							if(Math.random() <= scrollDrop) {
								e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), createScroll("REUSEABLE"));
							}
							
							if(Math.random() <= cheapScrollDrop) {
								e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), createScroll("CHEAP"));
							}	
						}
						//Drops only 1 scroll if double drops disabled
						else {
							if(Math.random() <= scrollDrop) {
								e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), createScroll("REUSEABLE"));
							}
							
							else if(Math.random() <= cheapScrollDrop) {
								e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), createScroll("CHEAP"));
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void useScroll(PlayerInteractEvent e) {
		try {
		Player player = e.getPlayer();
        Action action = e.getAction();
        // Check if the player has scroll in main hand
        if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
	        if(e.getHand().equals(EquipmentSlot.HAND)) {
		        if(isScroll(player.getInventory().getItemInMainHand())) {
		            // Right clicking
		            if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK ) {
		            	//Reuseable Scrolls
		                if(player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', scrollName))){
		                	Scroll s = null;
		                	
		                	//Get Scroll object from ID
		                	try {
		                	s = getScroll(Integer.parseInt(player.getInventory().getItemInMainHand().getItemMeta().getLore().get(3).substring(8))); 
		                	}catch(Exception ex) {Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error getting ID from a reuseable scroll used by "+player.getName()+". Replacing the scroll.");
			                	player.getInventory().remove(player.getInventory().getItemInMainHand());
			                	player.getInventory().addItem(createScroll("REUSEABLE"));
			                	player.sendMessage(ChatColor.GREEN + "You seem to have an old scroll, here is a new one. Sorry for the inconvenience.");
			                	return;}
		                            	
		                	if(s.timeBeforeUse() > 0) {
		                		if(s.timeBeforeUse() > 60) {
		                			e.getPlayer().sendMessage(ChatColor.RED + "You cannot use this for another "
		                					+ s.timeBeforeUse()/60 + " minutes and "+s.timeBeforeUse()%60 + " seconds");
		                			return;
		                		}
		                		else{
		                			e.getPlayer().sendMessage(ChatColor.RED + "You cannot use this for another "+s.timeBeforeUse()+ " seconds");
		                			return;
		                		}
		                	} else if(s.getLocation() == null) {
		                		s.setLocation(e.getPlayer().getLocation());
		                		setScrollLocation(player.getInventory().getItemInMainHand(),s.getLocation());
		                		e.getPlayer().sendMessage(ChatColor.GREEN + "Your scroll location has been set!");
		                		return;
		                	}
		                	else {
		                		if(!tpplayers.containsKey(player)) {
			                		String message = tpmsg.replaceAll("%seconds%",tpdelay+"");
			                		e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
			                		TeleportTask task = new TeleportTask(player, tpdelay, s.getLocation(), s);
			                		task.runTaskTimer(Plugin.plugin, 0, 20);
			                		tpplayers.put(player,task);
			                		return;
		                		}
		                		else {
		                			player.sendMessage(ChatColor.RED + "You can only use one scroll at a time!");
		                			return;
		                		}
		                	}
		                }
			            //Cheap Scrolls
		                else {
		                	if(player.getInventory().getItemInMainHand().getItemMeta().hasDisplayName()) {
		                		if(player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', cheapScrollName))){
					            	ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();
					            	
					            	 //No location set
					            	if(meta.getLore().get(1).contains("N/A")) {
					            		setScrollLocation(player.getInventory().getItemInMainHand(), player.getLocation());
					            		player.sendMessage(ChatColor.GREEN + "Your scroll location has been set!");
					            	}
					            	//Location has been set
					            	else {
					            		try {
						            		//Parse location from Lore
						            		int x,y,z;
						            		World w;
						            		ArrayList<Integer> temp = new ArrayList<Integer>();
						            		String s = meta.getLore().get(1);
						            		s = s.substring(22,s.indexOf('.'));
						            		s = s.replaceAll("§", "");
						            		s = s.replaceAll("e", "");
						            		s = s.replaceAll("a", "");
						            		s = s.replaceAll(",", " ");
						            		
						            		for (String number : s.split("\\s"))
						            		{
						            		    temp.add(Integer.parseInt(number));
						            		}
						            		
						            		x = temp.get(0);
						            		y = temp.get(1);
						            		z = temp.get(2);
						            		w = Bukkit.getWorld(meta.getLore().get(1).substring(meta.getLore().get(1).indexOf('.')+11,meta.getLore().get(1).length()));
						            		
						            		if(!tpplayers.containsKey(player)) {
						                		String message = tpmsg.replaceAll("%seconds%",tpdelay+"");
						                		e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
						                		TeleportTask task = new TeleportTask(player, tpdelay, new Location(w,x,y,z));
						                		task.runTaskTimer(Plugin.plugin, 0, 20);
						                		tpplayers.put(player,task);
						                		if(player.getInventory().getItemInMainHand().getAmount() == 1)
							            			player.getInventory().remove(player.getInventory().getItemInMainHand());
							            		else {
							            			player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount()-1);
							            		}
						                		return;
					                		}
					                		else {
					                			player.sendMessage(ChatColor.RED + "You can only use one scroll at a time!");
					                			return;
					                		}			            		
					            		}catch(NullPointerException error) {
					            			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error getting location from scroll used by "+player.getName());
					            			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Is the Scroll's World valid?");
					            		}
					            	return;
					            	}
		                		}
				            }
		                }
		                
		            }
		     
		        }
	        }
        }
		}catch(Exception ex) {Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error Using Scroll. \n" + ex.getMessage());}
    }
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onInventoryClick(PrepareAnvilEvent e){
		if(e.getInventory().getItem(0) != null) {
			if(isScroll(e.getInventory().getItem(0))) {
				e.setResult(null);
			}
		}
		else if(e.getInventory().getItem(1) != null) {
			if(isScroll(e.getInventory().getItem(1))) {
				e.setResult(null);
			}
		}
	}
	
	@EventHandler
	public void scrollCraft(CraftItemEvent e) {
		if(e.getInventory().getResult() != null) {
			
			if(e.getInventory().getResult().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', scrollName))) {
				e.getInventory().setResult(createScroll("REUSEABLE"));
			}
			
		}
	}
	
	@EventHandler 
	public void movementCancel(PlayerMoveEvent e){
		if(tpplayers.containsKey(e.getPlayer())) {
			Location movedFrom = e.getFrom();
	        Location movedTo = e.getTo();
	        Player p = e.getPlayer();
	        if ((movedFrom.getBlockX() != movedTo.getBlockX()) || (movedFrom.getBlockY() != movedTo.getBlockY()) || (movedFrom.getBlockZ() != movedTo.getBlockZ())) {
	        	e.getPlayer().sendMessage(ChatColor.RED + "You have moved! Teleportation canceled");
	        	if(tpplayers.get(p).getScroll() == null) {
            		ItemStack scroll = createScroll("CHEAP");
            		setScrollLocation(scroll,tpplayers.get(p).getLoc());
            		p.getInventory().addItem(scroll);
            	}
	        	tpplayers.get(e.getPlayer()).cancel();
	        	tpplayers.remove(p);
	        }
		}
	}
	
	@EventHandler
    public void onHit(EntityDamageEvent event){
        if (event.getEntity() instanceof Player){
        	Player p = (Player) event.getEntity();
            if(tpplayers.containsKey(p)) {
            	p.sendMessage(ChatColor.RED + "You have taken damage! Teleportation canceled");
            	if(tpplayers.get(p).getScroll() == null) {
            		ItemStack scroll = createScroll("CHEAP");
            		setScrollLocation(scroll,tpplayers.get(p).getLoc());
            		p.getInventory().addItem(scroll);
            	}
	        	tpplayers.get(p).cancel();
	        	tpplayers.remove(p);
            }
        }
    }

	//END EVENTS
	
	//Begin methods
	public ItemStack createScroll(String type) {
		
		//Begin ItemStack Creation
		ItemStack scroll = new ItemStack(Material.PAPER,1);
		ItemMeta meta = scroll.getItemMeta();
		//See if scroll is cheap or not
		if(type.equalsIgnoreCase("CHEAP"))
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', cheapScrollName));
		else
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', scrollName));
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GREEN + "This is an unbound teleportation scroll. It can only be bound once.");
		lore.add(ChatColor.GREEN + "Current Location: N/A");
		lore.add(ChatColor.DARK_GRAY + "Right Click with main hand to save your position in the scroll");
		//End ItemStack Creation
		if(type.equalsIgnoreCase("REUSEABLE")){
			//Begin Scroll Object Creation
			Scroll s = new Scroll();
			s.setType(type);
			s.setID(activeScrolls.size()+1);
			activeScrolls.add(s);
			//End Scroll Object Creation
			lore.add(ChatColor.GRAY + "ID: "+ChatColor.YELLOW+""+s.getID());
		}
		meta.setLore(lore);
		scroll.setItemMeta(meta);
		
		return scroll;
	}
	
	public Scroll getScroll(int ID) {
		for(Scroll s : activeScrolls) {
			if(s.getID() == ID)
				return s;
		}
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error finding scroll with ID of "+ID);
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Was the config edited manually?");
		return null;
	}
	
	public void setScrollLocation(ItemStack i, Location l) {
		ItemMeta temp = i.getItemMeta();
		ArrayList<String> lore = (ArrayList<String>) temp.getLore();
		lore.set(0, ChatColor.GREEN + "This teleportation scroll is bound to a location");
		lore.set(1, ChatColor.GREEN + "Current Location: "+ChatColor.YELLOW+""+l.getBlockX() +
				ChatColor.GREEN+","+ChatColor.YELLOW+""+l.getBlockY()+ChatColor.GREEN+","+ChatColor.YELLOW+""
				+l.getBlockZ()+ChatColor.GREEN+". World: "+ChatColor.YELLOW+ "" +l.getWorld().getName());
		lore.set(2, ChatColor.DARK_GRAY + "Right Click with main hand to teleport to the saved position");
		temp.setLore(lore);
		i.setItemMeta(temp);
	}
	
	public static Entity[] getNearbyEntities(Location l, int radius) {
	    int chunkRadius = radius < 16 ? 1 : (radius - (radius % 16)) / 16;
	    HashSet <Entity> radiusEntities = new HashSet < Entity > ();
	 
	    for (int chX = 0 - chunkRadius; chX <= chunkRadius; chX++) {
	        for (int chZ = 0 - chunkRadius; chZ <= chunkRadius; chZ++) {
	            int x = (int) l.getX(), y = (int) l.getY(), z = (int) l.getZ();
	            for (Entity e: new Location(l.getWorld(), x + (chX * 16), y, z + (chZ * 16)).getChunk().getEntities()) {
	                if (e.getLocation().distance(l) <= radius && e.getLocation().getBlock() != l.getBlock())
	                	if(e instanceof Creature)
	                		if(e.getType() != EntityType.PLAYER)
	                			radiusEntities.add(e);
	            }
	        }
	    }
	 
	    return radiusEntities.toArray(new Entity[radiusEntities.size()]);
	}
	
	public boolean isScroll(ItemStack i) {
		try {
		if(i != null)
			if(i.getType().equals(Material.PAPER)) {
				if(i.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', scrollName))
				|| i.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', cheapScrollName))) {
					return true;
				}
			}
		}catch(Exception e) {
			
		}
		return false;
	}
	
	//End Methods
}
