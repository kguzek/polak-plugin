package me.CornyFlakez.polak;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class Arena implements CommandExecutor, Listener {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// Arena command for teleporting to arenas
		if (label.equalsIgnoreCase("arena")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("§4That command can only be used by players.");
				return true;
			}
			Player player = (Player) sender;
			if (player.hasPermission("polak.arena.teleport")) {
				List<String> arenaList = Arrays.asList(new String[]{"1", "2"});
				if (args.length > 0 && arenaList.contains(args[0])) {
					//teleport to random slot in arena [x]
					Integer slotNumber = new Random().nextInt(4) + 1;
					String slotID = "slot" + slotNumber.toString();
					player.teleport(Survival.getDeserialisedLocation("arena" + args[0] + ";" + Main.data.getConfig("data.yml").getString("warpCoords." + slotID)));
					if (args[0].equalsIgnoreCase("2")) player.performCommand("polak stick");
					else player.performCommand("polak sword");
					return true;
				}
			}
			else {
				player.sendMessage("§4You do not have access to that command");
				return true;
			}
		}
		else if (label.equalsIgnoreCase("hub") || label.equalsIgnoreCase("lobby")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("§4That command can only be used by players.");
				return true;
			}
			Player player = (Player) sender;
			if (player.hasPermission("polak.hub.use")) {
				player.teleport(Survival.getDeserialisedLocation("lobby;" + Main.data.getConfig("data.yml").getString("warpCoords.lobby")));
				return true;
			}
			else {
				player.sendMessage("§4You do not have access to that command");
				return true;
			}
		}
		sender.sendMessage("§cUsage: §6/arena §3<§61§3|§62§3>");
		return false;
	}
		
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		World world = player.getWorld();
		// Random respawn in arenas
		if (world.getName().contains("arena")) {
			Integer slotNumber = new Random().nextInt(4) + 1;
			String slotID = "slot" + slotNumber.toString();
			event.setRespawnLocation(Survival.getDeserialisedLocation(world.getName() + ";" + Main.data.getConfig("data").getString("warpCoords." + slotID)));
		}
	}
}
