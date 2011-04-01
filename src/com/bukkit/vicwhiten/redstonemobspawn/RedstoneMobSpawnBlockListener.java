package com.bukkit.vicwhiten.redstonemobspawn;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;

public class RedstoneMobSpawnBlockListener extends BlockListener {

	private RedstoneMobSpawn plugin;
	public BlockFace[] faces = BlockFace.values();
	public CreatureType[] types = CreatureType.values();
	public RedstoneMobSpawnBlockListener(RedstoneMobSpawn plug) {
		plugin = plug;
	}
	
	public void onSignChange(SignChangeEvent event) {
	
		System.out.println("Sign Changed!");
		String[] lines = event.getLines();
		//is a mobspawn sign
		if(lines.length >1 && lines[0].compareTo("MOBSPAWN") == 0)
		{
			Player player = event.getPlayer();
			if(!plugin.checkPermission(player, "redstonemobspawn.spawn"))
			{
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You do not have the require permissions to do this");
			}
		}
	}
	public void onBlockRedstoneChange(BlockRedstoneEvent event) 
	{	
		Block block = event.getBlock();
		if(event.getOldCurrent() > 0 && event.getNewCurrent() == 0)
		{

			//check adjacent blocks for signs
			for(int i=0; i<6; i++)
			{
				Block adjacent = block.getRelative(faces[i]);
				if(adjacent.getType() == Material.SIGN_POST)
				{
					Sign sign = (Sign)adjacent.getState();
					//make sure its a MobSpawn sign
					if(sign.getLine(0).toLowerCase().compareTo("mobspawn") == 0)	
					{
						plugin.signsTriggered.remove(adjacent.getLocation());
					}
				}
			}
		}
		
		if(event.getOldCurrent() == 0 && event.getNewCurrent() > 0)
		{
			//check adjacent blocks for signs
			for(int i=0; i<6; i++)
			{
				try{
				Block adjacent = event.getBlock().getRelative(faces[i]);
				Sign sign = checkSign(adjacent);
				CreatureType type = getCreatureType(sign);
				Location loc = getSpawnLocation(adjacent, sign);
				//spawn the creature, place sign in hash
				spawnCreature(adjacent.getWorld(), loc, type);
				plugin.signsTriggered.put(adjacent.getLocation(), true);
				}catch(Exception E){
				}
			}
		}

	}
	
	public void spawnCreature(World world, Location loc, CreatureType type)
	{
		if(plugin.lim != null)
		{
			if(plugin.lim.mobMax <= plugin.lim.getMobAmount(world))
			{
				List<LivingEntity> entities = world.getLivingEntities();
				for(LivingEntity entity : entities)
				{
					if(Creature.class.isInstance(entity))
					{
						entity.remove();
						break;
					}
				}
			}
		}
		world.spawnCreature(loc, type);
	}
	public Location getSpawnLocation(Block block, Sign sign)
	{
		try{
			String[] mobSpawn = sign.getLine(2).split(" ");
			//correct number of args
			if(mobSpawn.length <1 || mobSpawn.length > 2)
			{
				throw new Exception();
			}
		
			BlockFace spawnFace = null;
			for(BlockFace face: faces)
			{
				//blockface match
				if(face.toString().toLowerCase().compareTo(mobSpawn[0].toLowerCase()) == 0)
				{
					spawnFace = face;
					break;
				}
			}
			if(mobSpawn.length == 1)
			{
				return block.getRelative(spawnFace).getLocation();
			}
			
			int length = Integer.parseInt(mobSpawn[1]);
			for(int i=0; i< length; i++)
			{
				block = block.getRelative(spawnFace);
			}
			return block.getLocation();
		
		}catch(Exception E)
		{
			return block.getLocation();	
		}
	}
	public Sign checkSign(Block block)
	{
		if(block.getType() == Material.SIGN_POST)
		{
			Sign sign = (Sign)block.getState();
			//make sure its a MobSpawn sign
			if(sign.getLine(0).toLowerCase().compareTo("mobspawn") == 0)	
			{
				//makes sure sign hasen't been triggered
				if(!plugin.signsTriggered.containsKey(block.getLocation()))
				{
				return sign;
				}
			}
		}
		return null;
	}
	
	public CreatureType getCreatureType(Sign sign)
	{
		//checks what creature will be spawned
		for(CreatureType type : types)
		{
			//match
			if(type.getName().toLowerCase().compareTo(sign.getLine(1).toLowerCase()) == 0)
			{
				return type;
			}
		}return null;
	}

}