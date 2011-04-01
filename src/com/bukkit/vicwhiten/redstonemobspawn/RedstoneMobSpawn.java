package com.bukkit.vicwhiten.redstonemobspawn;

import java.util.HashMap;
import java.util.logging.Logger;

import org.anjocaido.groupmanager.GroupManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

import com.bukkit.vicwhiten.moblimiter.MobLimiter;
import com.nijikokun.bukkit.Permissions.Permissions;

public class RedstoneMobSpawn extends JavaPlugin
{
	public static final Logger log = Logger.getLogger("Minecraft");
	public HashMap<Location,Boolean> signsTriggered = new HashMap<Location,Boolean>();
	private final RedstoneMobSpawnBlockListener blockListener = new RedstoneMobSpawnBlockListener(this);
	GroupManager gm = null;
	Permissions perm = null;
	MobLimiter lim = null;

	public void onDisable()
	{
		PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println(pdfFile.getName()+" version "+pdfFile.getVersion()+" is disabled!");
	}

	public void onEnable()
	{

		PluginManager pm = getServer().getPluginManager();
		
		 Plugin p = this.getServer().getPluginManager().getPlugin("GroupManager");
	       if (p != null) {
	           if (!this.getServer().getPluginManager().isPluginEnabled(p)) {
	                this.getServer().getPluginManager().enablePlugin(p);
	            }
	            gm = (GroupManager) p;
	        } 
	        p = this.getServer().getPluginManager().getPlugin("Permissions");
	        if (p != null) {
	            if (!this.getServer().getPluginManager().isPluginEnabled(p)) {
	                this.getServer().getPluginManager().enablePlugin(p);
	            }
	            perm = (Permissions) p;
	        } 
	        
	        p = this.getServer().getPluginManager().getPlugin("MobLimiter");
	        if (p != null) {
	            if (!this.getServer().getPluginManager().isPluginEnabled(p)) {
	                this.getServer().getPluginManager().enablePlugin(p);
	            }
	            lim = (MobLimiter) p;
	        } 
	        
	        
		pm.registerEvent(Event.Type.REDSTONE_CHANGE, this.blockListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.SIGN_CHANGE, this.blockListener, Event.Priority.Normal, this);
		
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println(pdfFile.getName()+" version "+pdfFile.getVersion()+" is enabled!");
	}
	
    public boolean checkPermission(Player player, String permission)
    {
    	if(player.isOp())
    	{
    		return true;
    	}else if(gm != null)
    	{
        return gm.getWorldsHolder().getWorldPermissions(player).has(player,permission);
    	}else if(perm != null)
    	{
    	return perm.getHandler().has(player, permission);
    	}else return false;
    }
}