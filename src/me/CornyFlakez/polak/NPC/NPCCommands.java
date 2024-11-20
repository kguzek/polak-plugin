package me.CornyFlakez.polak.NPC;

import java.util.HashMap;
import java.util.TreeSet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.CornyFlakez.polak.Main;

public class NPCCommands implements CommandExecutor {
	private static HashMap<String, Float> directions = new HashMap<String, Float>();
	private static HashMap<String, String> parameters = new HashMap<String, String>();
	
	private static void initParameters() {
		String[] keys = new String[] {"-n", "-x", "-y", "-z", "-f", "-w", "-s"};
		String[] vals = new String[] {
				"name", "x", "y", "z", "north§3§l|§6§l§neast§3§l|§6§l§nsouth§3§l|§6§l§nwest", "world", "player skin"};
		for (int i = 0; i < keys.length; i++)
			parameters.put(keys[i], vals[i]);
		String[] keys2 = new String[] {null, "north", "east", "south", "west"};
		Float[] vals2 = new Float[] {0.0f, -180.0f, -90.0f, 0.0f, 90.0f};
		for (int i = 0; i < keys2.length; i++)
			directions.put(keys2[i], vals2[i]);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("createnpc")) {
			if (!sender.hasPermission("polak.npc.create")) {
				sender.sendMessage("§4You do not have permission to use that command!");
				return true;
			}
			String name = null;
			Double x = null;
			Double y = null;
			Double z = null;
			Float yaw = null;
			Float pitch = null;
			World world = null;
			String[] skin = null;
			Boolean usePlayerLocation = true;
			for (int i = 0; i < args.length; i++) {
				if (parameters.isEmpty()) initParameters();
				if (parameters.containsKey("-" + args[i].charAt(1))) {
					args[i] = "-" + args[i].charAt(1);
					if (args.length > i + 1) {
						if (args[i].equalsIgnoreCase("-n")) {
							name = args[i + 1];
							continue;
						} else if (args[i].equalsIgnoreCase("-x")) {
							try {
								x = Double.parseDouble(args[i + 1]);
								usePlayerLocation = false;
								continue;
							} catch (NumberFormatException e) {}
						} else if (args[i].equalsIgnoreCase("-y")) {
							try {
								y = Double.parseDouble(args[i + 1]);
								usePlayerLocation = false;
								continue;
							} catch (NumberFormatException e) {}
						} else if (args[i].equalsIgnoreCase("-z")) {
							try {
								z = Double.parseDouble(args[i + 1]);
								usePlayerLocation = false;
								continue;
							} catch (NumberFormatException e) {}
						} else if (args[i].equalsIgnoreCase("-f")) {
							if (directions.containsKey(args[i + 1].toLowerCase())) {
								yaw = directions.get(args[i + 1].toLowerCase());
								continue;
							}
						} else if (args[i].equalsIgnoreCase("-w")) {
							if (Bukkit.getWorld(args[i + 1]) != null) {
								world = Bukkit.getWorld(args[i + 1]);
								continue;
							}
						} else if (args[i].equalsIgnoreCase("-s")) {
							if (NPCManager.getSkin(sender, args[i + 1]) != null) {
								skin = NPCManager.getSkin(sender, args[i + 1]);
								continue;
							}
						}
					}
					String msg = "§cUsage: §6/createnpc";
					for (int j = 0; j < args.length; j++)
						if (parameters.containsKey(args[j])) {
							if (i == j)
								msg = msg + " §6§l" + args[j] + " §3§l<§6§l§n" + parameters.get(args[j]) + "§3§l>";	
							else
								msg = msg + " §6" + args[j] + " §3<§6" + parameters.get(args[j]).replace("§l", "").replace("§n", "") + "§3>";			
						}
					sender.sendMessage(msg);
					return true;
				}
			}
			if (name == null) {
				if (!(sender instanceof Player)) {
					sender.sendMessage("§cMissing argument: §6-name");
					return true;
				}
				name = ((Player) sender).getDisplayName();
			}
			if (x == null) {
				if (!(sender instanceof Player && usePlayerLocation)) {
					sender.sendMessage("§cMissing argument: §6-x");
					return true;
				}
				x = ((Player) sender).getLocation().getX();
			}
			if (y == null) {
				if (!(sender instanceof Player && usePlayerLocation)) {
					sender.sendMessage("§cMissing argument: §6-y");
					return true;
				}
				y = ((Player) sender).getLocation().getY();
			}
			if (z == null) {
				if (!(sender instanceof Player && usePlayerLocation)) {
					sender.sendMessage("§cMissing argument: §6-z");
					return true;
				}
				z = ((Player) sender).getLocation().getZ();
			}
			if (yaw == null) {
				if (sender instanceof Player && usePlayerLocation)
					yaw = ((Player) sender).getLocation().getYaw();
				else
					yaw = -180.0f;
			}
			if (sender instanceof Player && usePlayerLocation)
				pitch = ((Player) sender).getLocation().getPitch();
			else
				pitch = 0.0f;
			if (world == null) {
				if (!(sender instanceof Player)) {
					sender.sendMessage("§cMissing argument: §6-world");
					return true;
				}
				world = ((Player) sender).getWorld();
			}
			if (skin == null && sender instanceof Player)
				skin = NPCManager.getSkin(sender, ((Player) sender).getDisplayName());
			
			Location loc = new Location(world, x, y, z, yaw, pitch);
			sender.sendMessage(String.format("§6Created §3%s§6 at §bx: §3§l%s§6, §by: §3§l%s§6, §bz: §3§l%s§6 with ID §3%s§6.",
					name.replace('&', '§'), Math.round(loc.getX()), Math.round(loc.getY()), Math.round(loc.getZ()), NPCManager.saveNPC(loc, name, skin).toString()));
			return true;
		
		}
		else if (label.equalsIgnoreCase("deletenpc") || label.equalsIgnoreCase("delnpc")) {
			if (!sender.hasPermission("polak.npc.delete")) {
				sender.sendMessage("§4You do not have permission to use that command!");
				return true;
			}
			if (args.length > 0) {
				try {
					if (NPCManager.NPCs.containsKey(Integer.parseInt(args[0]))) {
						NPCManager.deleteNPC(Integer.parseInt(args[0]));
						NPCManager.NPCs.remove(Integer.parseInt(args[0]));
						Main.data.getConfig("data.yml").set("createdNPCs." + args[0], null);
						Main.data.saveConfigFile("data.yml");
						sender.sendMessage("§6Deleted NPC with ID §2" + args[0] + "§6.");
					}
					else
						sender.sendMessage("§4There is no NPC with ID §3" + args[0] + "§4.");
				}
				catch (NumberFormatException e) {
					sender.sendMessage("§3" + args[0] + "§4 is not an NPC ID.");
				}
				return true;
			}
			sender.sendMessage("§4Usage: §6/deletenpc §3<§6ID§3>");
			return true;
		}
		else if (label.equalsIgnoreCase("deleteallnpcs") || label.equalsIgnoreCase("delallnpcs")) {
			if (!sender.hasPermission("polak.npc.delete")) {
				sender.sendMessage("§4You do not have permission to use that command!");
				return true;
			}
			if (NPCManager.NPCs.isEmpty())
				sender.sendMessage("§6No NPCs have been created yet.");
			else {
				sender.sendMessage("§6Deleted §2" + NPCManager.NPCs.size() + " §6NPCs.");
				for (int id : NPCManager.NPCs.keySet())
					NPCManager.deleteNPC(id);
				NPCManager.NPCs.clear();
				Main.data.getConfig("data.yml").set("createdNPCs", null);
				Main.data.saveConfigFile("data.yml");
			}
			return true;
		}
		else if (label.equalsIgnoreCase("npcs")) {
			if (!sender.hasPermission("polak.npc.list")) {
				sender.sendMessage("§4You do not have permission to use that command!");
				return true;
			}			
			if (NPCManager.NPCs.isEmpty())
				sender.sendMessage("§6No NPCs have been created yet.");
			else {
				sender.sendMessage("§6Created NPCs:");
				for (Integer id : new TreeSet<Integer>(NPCManager.NPCs.keySet())) // Sorts the NPC IDs in ascending order
					sender.sendMessage("§6§o" + NPCManager.NPCs.get(id).getName() + "§6, ID: §b" + id.toString());
			}
			return true;
		}
		return false;
	}

}
