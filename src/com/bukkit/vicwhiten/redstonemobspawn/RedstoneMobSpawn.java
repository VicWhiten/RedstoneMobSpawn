package com.bukkit.vicwhiten.redstonemobspawn;

import java.util.HashMap;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

public class RedstoneMobSpawn extends JavaPlugin
{
	public static final Logger log = Logger.getLogger("Minecraft");
	public HashMap<Location,Boolean> signsTriggered = new HashMap<Location,Boolean>();
	private final RedstoneMobSpawnBlockListener blockListener = new RedstoneMobSpawnBlockListener(this);

	public void onDisable()
	{
		PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println(pdfFile.getName()+" version "+pdfFile.getVersion()+" is disabled!");
	}

	public void onEnable()
	{

		PluginManager pm = getServer().getPluginManager();
		
		
		pm.registerEvent(Event.Type.REDSTONE_CHANGE, this.blockListener, Event.Priority.Normal, this);

        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println(pdfFile.getName()+" version "+pdfFile.getVersion()+" is enabled!");
	}
}