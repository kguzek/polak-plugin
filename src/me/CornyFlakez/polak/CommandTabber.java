package me.CornyFlakez.polak;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.CornyFlakez.polak.NPC.NPCManager;

public class CommandTabber implements TabCompleter {

	private static HashMap<String, String[]> commandArguments; // This stores commands and their arguments
	/* This stores which argument has what value, ie. there is a different value if we are typing the 1st argument of a command vs. the 5th argument
	The integer in this HashMap is which argument we are currently typing */
	private static HashMap<Integer, HashMap<String, String[]>> getCommandArguments = new HashMap<Integer, HashMap<String, String[]>>();
	
	private static void getCommandArguments() {
		commandArguments = new HashMap<String, String[]>();
		commandArguments.put("arena", new String[] {"1", "2"});
		commandArguments.put("polak", new String[] {"stick", "sword"});
		commandArguments.put("delnpc", getNPCids());
		commandArguments.put("deletenpc", getNPCids());
		commandArguments.put("gm", new String[] {"adventure", "creative", "spectator", "survival"});
		getCommandArguments.put(1, commandArguments); commandArguments = new HashMap<String, String[]>();
		commandArguments.put("gm", getPlayers());
		getCommandArguments.put(3, commandArguments); commandArguments = new HashMap<String, String[]>();
	}
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		getCommandArguments();
		List<String> result = new ArrayList<String>();
		if (getCommandArguments.containsKey(args.length) && getCommandArguments.get(args.length).containsKey(label.toLowerCase()))
			for (String arg : getCommandArguments.get(args.length).get(label.toLowerCase()))
				if (arg.startsWith(args[args.length - 1].toLowerCase()))
					result.add(arg);
		return result;
	}

	private static String[] getPlayers() {
		List<String> result = new LinkedList<String>(Arrays.asList("@a", "@e", "@p", "@r", "@s"));
		for (Player player : Bukkit.getOnlinePlayers())
			result.add(player.getName());
		return result.toArray(new String[result.size()]);
	}
	
	private static String[] getNPCids() {
		ArrayList<String> npcs = new ArrayList<String>();
		for (Integer id : NPCManager.NPCs.keySet())
			npcs.add(id.toString());
		return npcs.toArray(new String[npcs.size()]);
	}
}
