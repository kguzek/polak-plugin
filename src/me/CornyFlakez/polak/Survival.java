package me.CornyFlakez.polak;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class Survival implements CommandExecutor, Listener {
	
	private static HashMap<String, String> gamemodes = new HashMap<String, String>();
	
	private static void getGamemodes() {
		gamemodes.put("a", "adventure");
		gamemodes.put("c", "creative");
		gamemodes.put("s", "survival");
		gamemodes.put("ss", "spectator");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("survival") || label.equalsIgnoreCase("sv")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("§4That command can only be used by players.");
				return true;
			}
			Player player = (Player) sender;
			player.teleport(getSavedLocation(player));
			return true;
		}
		else if (label.equalsIgnoreCase("wilderness") || label.equalsIgnoreCase("wild")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("§4That command can only be used by players.");
				return true;
			}
			Player player = (Player) sender;
			if (player.getWorld().getName().equalsIgnoreCase("survival"))
				player.teleport(randomLocation(player));
			else
				player.sendMessage("§4You can only use that command in the survival world.");
			return true;
		}
		else if (label.equalsIgnoreCase("gm")) {
			if (gamemodes.isEmpty()) getGamemodes();
			String command = "gamemode ";
			if (args.length > 0) {
				if (gamemodes.containsKey(args[0]))
					command = command + gamemodes.get(args[0]);
				else {
					for (String key : gamemodes.keySet())
						if (gamemodes.get(key).equalsIgnoreCase(args[0]))
							command = command + gamemodes.get(key);
					if (command.equals("gamemode ")) {
						sender.sendMessage("§4Usage: §6/gamemode §3[§6gamemode§3]");
						return true;
					}
				}
				if (args.length > 1)
					command = command + " " + args[1];
				if (sender instanceof Player)
					((Player) sender).performCommand(command);
				else if (args.length > 1)
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("/", ""));
				else 
					sender.sendMessage("§4Usage: §6/gamemode §3[§6gamemode§3] [§6player§3]");
				return true;
				
			}
			sender.sendMessage("§4Usage: §6/gamemode §3[§6gamemode§3] (§6player§3)");
			return true;
		}
		return false;
	}
	
	private Location randomLocation(Player player) {
		Location loc = new Location(Bukkit.getWorld("survial"), 0, 0, 0);
		Random r = new Random();
		Integer x = 0;
		Integer z = 0;
		while (true) {
			x = r.nextInt(20000) - 9999; z = r.nextInt(20000) - 9999;
			loc = Bukkit.getWorld("survival").getHighestBlockAt(x, z).getLocation().add(0.5, 0, 0.5);
			Block b = loc.getBlock();
			if (b.isEmpty() || b.isLiquid() || b.isPassable()) continue;
			loc.setY(loc.getY() + 2);
			break;
		}
		player.sendMessage(String.format("§6Teleporting to §b§l%s§r§6, §b§l%s§r§6.", x, z));
		return loc;
	}
	
	private Location getSavedLocation(Player player) {
		try {
			return getDeserialisedLocation(Main.data.getConfig("data.yml").getString("lastPlayerLocations." + player.getUniqueId().toString()));
		} catch (NullPointerException e) {
			 // default spawn for survival
			player.sendMessage(String.format("§6Welcome to the survival world, §b%s§6! Use §f/§2wild §6to get started.", player.getName()));
			return new Location(Bukkit.getWorld("survival"), 0.5, 252, 0.5);
		}
	}
	
	public static void saveLocation(Player p) {
		Main.data.getConfig("data.yml").set("lastPlayerLocations." + p.getUniqueId().toString(), getSerialisedLocation(p.getLocation()));
		Main.data.saveConfigFile("data.yml");
	}
	
	public static String getSerialisedLocation(Location loc) {
        return loc.getWorld().getName() + ";" + loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getYaw() + ";" + loc.getPitch();
    }
 
    public static Location getDeserialisedLocation(String serialisedLocation) {
            String[] s = serialisedLocation.split(";");
            return new Location(Bukkit.getServer().getWorld(s[0]), Double.parseDouble(s[1]), Double.parseDouble(s[2]), Double.parseDouble(s[3]), Float.parseFloat(s[4]), Float.parseFloat(s[5]));
    }
}
