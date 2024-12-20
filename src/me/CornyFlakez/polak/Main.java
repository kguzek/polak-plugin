package me.CornyFlakez.polak;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import me.CornyFlakez.polak.Files.DataManager;
import me.CornyFlakez.polak.NPC.NPCCommands;
import me.CornyFlakez.polak.NPC.NPCManager;

<<<<<<< HEAD
import java.util.Objects;

=======
>>>>>>> d6d09edb8142ad5d9bcd1826b42faf51d36a14a7
public class Main extends JavaPlugin implements Listener {
	
	public static Main plugin;
	public static DataManager data = new DataManager();
	
	@Override
	public void onEnable() {
		plugin = this;
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getServer().getPluginManager().registerEvents(new Arena(), this);
		this.getServer().getPluginManager().registerEvents(new CustomMessages(), this);
		this.getServer().getPluginManager().registerEvents(new Navigator(), this);
		Objects.requireNonNull(this.getCommand("gm")).setExecutor(new Survival());
		Objects.requireNonNull(this.getCommand("gm")).setTabCompleter(new CommandTabber());
		Objects.requireNonNull(this.getCommand("hub")).setExecutor(new Arena());
		Objects.requireNonNull(this.getCommand("wild")).setExecutor(new Survival());
		Objects.requireNonNull(this.getCommand("npcs")).setExecutor(new NPCCommands());
		Objects.requireNonNull(this.getCommand("polak")).setExecutor(new Kits());
		Objects.requireNonNull(this.getCommand("polak")).setTabCompleter(new CommandTabber());
		Objects.requireNonNull(this.getCommand("arena")).setExecutor(new Arena());
		Objects.requireNonNull(this.getCommand("arena")).setTabCompleter(new CommandTabber());
		Objects.requireNonNull(this.getCommand("survival")).setExecutor(new Survival());
		Objects.requireNonNull(this.getCommand("navigator")).setExecutor(new Navigator());
		Objects.requireNonNull(this.getCommand("createnpc")).setExecutor(new NPCCommands());
		Objects.requireNonNull(this.getCommand("createnpc")).setTabCompleter(new me.CornyFlakez.polak.NPC.CreateNPCTabber());
		Objects.requireNonNull(this.getCommand("deletenpc")).setExecutor(new NPCCommands());
		Objects.requireNonNull(this.getCommand("deletenpc")).setTabCompleter(new CommandTabber());
		Objects.requireNonNull(this.getCommand("deleteallnpcs")).setExecutor(new NPCCommands());
		this.saveDefaultConfig();
		data.saveDefaultConfig("data.yml");
		Navigator.createGUI();
		if (data.getConfig("data.yml").contains("createdNPCs") && !Objects.requireNonNull(data.getConfig("data.yml").getConfigurationSection("createdNPCs")).getKeys(false).isEmpty())
			NPCManager.restoreNPCs();
	}

	@Override
	public void onDisable() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getWorld().getName().contains("survival"))
				Survival.saveLocation(p);
		}
		for (Integer id : NPCManager.NPCs.keySet())
			NPCManager.deleteNPC(id);	
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// Reload plugin config.yml file
		if (label.equalsIgnoreCase("config") || label.equalsIgnoreCase("cfg")) {
			if (!(sender.hasPermission("polak.config.reload"))) {
				sender.sendMessage("§4You do not have permission to use that command.");
				return true;
			}
			this.reloadConfig();
			data.reloadConfigData("data.yml");
			sender.sendMessage("§7Reloaded config file for §6data.yml§7.");
			return true;
		}
		return false;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		String name = player.getDisplayName();
		event.setJoinMessage("§a§o[+]§7§o " + name + " has just joined!");
		player.performCommand("hub");
		// Give navigator compass when player joins
		if (player.getInventory().contains(Navigator.getItemCompass())) return;
		player.getInventory().clear();
		player.getInventory().addItem(Navigator.getItemCompass());
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		String name = player.getDisplayName();
		event.setQuitMessage("§c§o[-]§7§o " + name + " has just left!");
		if (player.getWorld().getName().contains("survival"))
			Survival.saveLocation(event.getPlayer());
	}
	
	@EventHandler
	private void onTeleport(PlayerTeleportEvent event) {
		// if teleporting from survival dimensions to other worlds, ie. lobby
		if (event.getFrom().getWorld().getName().contains("survival"))
			Survival.saveLocation(event.getPlayer());
		// send player each NPC packet if they are teleporting to the lobby, no matter where from
		if (event.getTo().getWorld().getName().equalsIgnoreCase("lobby"))
			for (Integer id : NPCManager.NPCs.keySet())
				NPCManager.sendNPCPacketAfterTeleport(event.getPlayer(),  NPCManager.NPCs.get(id));
	}
}