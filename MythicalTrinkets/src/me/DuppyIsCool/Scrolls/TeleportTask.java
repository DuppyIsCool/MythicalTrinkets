package me.DuppyIsCool.Scrolls;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.DuppyIsCool.Main.Plugin;

public class TeleportTask extends BukkitRunnable {
	Player tp;
	int time;
	private Location l;
	private Scroll s = null;
	public TeleportTask(Player p, int t, Location l) {
		tp = p;
		time = t;
		this.l = l;
		if(!p.isOnline()) {
			
		}
	}
	
	public TeleportTask(Player p, int t, Location l, Scroll s) {
		tp = p;
		time = t;
		this.l = l;
		this.s = s;
	}
	
	@Override
	public void run() {
		if(tp == null) {
			this.cancel();
		}
		if(!tp.isOnline() && !this.isCancelled()) {
			this.cancel();
		}
		
		if(time > 0)
			time = time - 1;
		else{
			if(s != null) {
				tp.teleport(l);
				tp.playSound(tp.getLocation(), Sound.ENTITY_FOX_TELEPORT, 1f, 1f);
				s.setTimeBeforeUse(ScrollManager.cooldownTime);
				@SuppressWarnings("unused")
				BukkitTask scrollTask = new ScrollTask(s).runTaskTimer(Plugin.plugin,0,20);
				this.cancel();
				ScrollManager.tpplayers.remove(tp);;
			}
			else {
				tp.teleport(l);
				tp.playSound(tp.getPlayer().getLocation(), Sound.ENTITY_FOX_TELEPORT, 1f, 1f);
				this.cancel();
				ScrollManager.tpplayers.remove(tp);
			}
		}
		
	}
	
	public Location getLoc() {
		return this.l;
	}
	
	public Scroll getScroll() {
		return this.s;
	}
}
