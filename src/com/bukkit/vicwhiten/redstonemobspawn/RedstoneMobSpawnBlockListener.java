package com.bukkit.vicwhiten.redstonemobspawn;


import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockRedstoneEvent;




public class RedstoneMobSpawnBlockListener extends BlockListener {

	private RedstoneMobSpawn plugin;
	public BlockFace[] faces = BlockFace.values();
	public CreatureType[] types = CreatureType.values();
	public RedstoneMobSpawnBlockListener(RedstoneMobSpawn plug) {
		plugin = plug;
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
				worldGuardCheck(adjacent);
				//spawn the creature, place sign in hash
				adjacent.getWorld().spawnCreature(loc, type);
				plugin.signsTriggered.put(adjacent.getLocation(), true);
				}catch(Exception E){
				}
			}
		}

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

	public void worldGuardCheck(Block block)
	{
		if(plugin.mobMax >= 0 )
		{
			List<LivingEntity> mobs = block.getWorld().getLivingEntities();
			for(int j=0; j<mobs.size(); j++)
			{
				if(!Creature.class.isInstance(mobs.get(j)))
				{
					mobs.remove(j);
					j--;
				}
			}
			if(mobs.size() == plugin.mobMax)
			{
				mobs.get(0).remove();
			}
		}
	}
}