package me.CornyFlakez.polak.NPC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class CreateNPCTabber implements TabCompleter {
	
	private static HashMap<Character, String[]> commandArguments = new HashMap<Character, String[]>();
	
	private static void getCommandArguments(CommandSender sender) {
		Character[] keys = new Character[] {'n', 'x', 'y', 'z', 'f', 'w', 's'};
		String[][] values = new String[][] {getPlayers(), new String[] {Double.toString(((Player) sender).getLocation().getX())}, new String[] {
				Double.toString(((Player) sender) .getLocation().getY())}, new String[] {
						Double.toString(((Player) sender).getLocation().getZ())}, new String[] {"north", "east", "south", "west"}, getWorlds(), getPlayers()};
		for (int i = 0; i < keys.length; i++)
			commandArguments.put(keys[i], values[i]);
	}
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		getCommandArguments(sender);
		List<String> result = new ArrayList<String>();
		String[] possibleResults = new String[] {"-name", "-x", "-y", "-z", "-facing", "-world", "-skin"};
		if (args.length >= 2) {
			String lastArg = args[args.length - 2];
			if (lastArg.startsWith("-") &&lastArg.length() >= 2 && commandArguments.containsKey(lastArg.charAt(1)))
						possibleResults = commandArguments.get(args[args.length - 2].charAt(1));
		}
		for (String arg : possibleResults)
			if (arg.startsWith(args[args.length - 1].toLowerCase()))
				result.add(arg);
		return result;
				
	}

	private static String[] getPlayers() {
		List<String> result = new ArrayList<String>(); //(Arrays.asList("@a", "@e", "@p", "@r", "@s"));
		for (Player player : Bukkit.getOnlinePlayers())
			result.add(player.getName());
		return result.toArray(new String[result.size()]);
	}
	
	private static String[] getWorlds() {
		List<String> result = new ArrayList<String>();
		for (World world : Bukkit.getWorlds())
			result.add(world.getName());
		return result.toArray(new String[result.size()]);
	}
}
