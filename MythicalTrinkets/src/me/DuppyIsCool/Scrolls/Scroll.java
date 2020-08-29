package me.DuppyIsCool.Scrolls;

import java.util.UUID;

import org.bukkit.Location;

public class Scroll {
	private int ID;
	
	private UUID lastUser;
	private String lastUsername;
	
	private Location location;
	
	private int timeBeforeUse;

	private String type;
	
	
	//Getters and Setters
	public int timeBeforeUse() {
		return timeBeforeUse;
	}

	public void setTimeBeforeUse(int cooldownTime) {
		this.timeBeforeUse = cooldownTime;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getLastUsername() {
		return lastUsername;
	}

	public void setLastUsername(String lastUsername) {
		this.lastUsername = lastUsername;
	}

	public UUID getLastUser() {
		return lastUser;
	}

	public void setLastUser(UUID uuid) {
		this.lastUser = uuid;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
}
