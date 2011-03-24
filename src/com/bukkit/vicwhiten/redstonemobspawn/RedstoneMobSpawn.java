package com.bukkit.vicwhiten.redstonemobspawn;


import java.util.HashMap;
import java.util.logging.Logger;



import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class RedstoneMobSpawn extends JavaPlugin
{
	public static final Logger log = Logger.getLogger("Minecraft");
	public HashMap<Location,Boolean> signsTriggered = new HashMap<Location,Boolean>();
	public int mobMax = -1;
	private final RedstoneMobSpawnBlockListener blockListener = new RedstoneMobSpawnBlockListener(this);

	public void onDisable()
	{
		PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println(pdfFile.getName()+" version "+pdfFile.getVersion()+" is disabled!");
	}

	public void onEnable()
	{

		PluginManager pm = getServer().getPluginManager();
		
		Plugin p = this.getServer().getPluginManager().getPlugin("WorldGuard");
        if (p != null) {
            if (!this.getServer().getPluginManager().isPluginEnabled(p)) {
                this.getServer().getPluginManager().enablePlugin(p);
            }
            WorldGuardPlugin wg = (WorldGuardPlugin) p;
            mobMax = wg.getConfiguration().getInt("mobs.max-amount", -1);
            System.out.println("max is " + mobMax);
        }
		pm.registerEvent(Event.Type.REDSTONE_CHANGE, this.blockListener, Event.Priority.Normal, this);

        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println(pdfFile.getName()+" version "+pdfFile.getVersion()+" is enabled!");
	}
}