package me.CornyFlakez.polak;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Navigator implements CommandExecutor, Listener {

	private static Inventory inv;
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// Navigator command for navigator GUI
		if (label.equalsIgnoreCase("navigator") || label.equalsIgnoreCase("nav")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("§4That command can only be used by players.");
				return true;
			}
			Player player = (Player) sender;
			if (player.hasPermission("polak.navigator.use")) {
				updateGUI();
				player.openInventory(inv);
				return true;
			}
			player.sendMessage("§4You do not have access to that command");
			return true;
		}
		return false;
	}
	
	public static void createGUI() {
		inv = Bukkit.createInventory(null,  45, "Server Navigator");
		updateGUI();
	}
	
	private static void updateGUI() {
		ItemStack item = new ItemStack(Material.WOODEN_SWORD);
		ItemMeta meta = item.getItemMeta();
		
		// Arena 1
		meta.setDisplayName("§9PvP Arena 1");
		List<String> lore = new ArrayList<String>();
		lore.add("§7Click to join PvP Arena 1!");
		Integer inArena1 = 0;
		for (Player p : Bukkit.getOnlinePlayers()) // gets player count for world
			if (p.getWorld().getName().equalsIgnoreCase("arena")) inArena1 += 1;
		lore.add("§7Currently online: §l" + inArena1.toString());
		meta.setLore(lore);
		meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(meta);
		inv.setItem(10,  item);
		
		// Arena 2
		item.setType(Material.STICK);
		meta.setDisplayName("§2PvP Arena 2");
		lore.clear();
		lore.add("§7Click to join PvP Arena 2!");
		Integer inArena2 = 0;
		for (Player p : Bukkit.getOnlinePlayers()) // gets player count for world
			if (p.getWorld().getName().equalsIgnoreCase("arena2")) inArena2 += 1;
		lore.add("§7Currently online: §l" + inArena2.toString());
		meta.setLore(lore);
		item.setItemMeta(meta);
		inv.setItem(12, item);
		
		// Survival
		item.setType(Material.CRAFTING_TABLE);
		meta.setDisplayName("§6Survival");
		meta.removeEnchant(Enchantment.DAMAGE_ALL);
		lore.clear();
		lore.add("§7Click to join survival!");
		Integer inSurvival = 0;
		for (Player p : Bukkit.getOnlinePlayers()) // gets player count for world
			if (p.getWorld().getName().equalsIgnoreCase("survival")) inSurvival ++;
		lore.add("§7Currently online: §l" + inSurvival.toString());
		meta.setLore(lore);
		item.setItemMeta(meta);
		inv.setItem(14, item);
		
		// Lobby
		item.setType(Material.PAPER);
		meta.setDisplayName("§eLobby");
		lore.clear();
		lore.add("§7Click to return to lobby.");
		Integer inLobby = 0;
		for (Player p : Bukkit.getOnlinePlayers()) // gets player count for world
			if (p.getWorld().getName().equalsIgnoreCase("lobby")) inLobby ++;
		lore.add("§7Currently online: §l" + inLobby.toString());
		meta.setLore(lore);
		item.setItemMeta(meta);
		inv.setItem(16, item);
		
		// Close
		item.setType(Material.BARRIER);
		meta.setDisplayName("§cClose");
		lore.clear();
		lore.add("§7Click to close menu.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		inv.setItem(40, item);
	}
		
	public static ItemStack getItemCompass() {
		ItemStack item = new ItemStack(Material.COMPASS);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§o§lNavigator");
		List<String> lore = new ArrayList<String>();
		lore.add("§oUse this to navigate the server!");
		meta.setLore(lore);
		item.setItemMeta(meta);		
		return item;
	}
	
	@EventHandler
	public void onClickInventory(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (!(event.getInventory().equals(inv))) {
			if (player.getWorld().getName().equalsIgnoreCase("lobby")) {
					if (event.getClickedInventory().getType() == InventoryType.PLAYER)
						if (player.hasPermission("polak.navigator.inventory.use.lobby"))
							return;
			}
			else return;
		}
		if (event.getCurrentItem() == null) return;
		if (event.getCurrentItem().getItemMeta() == null) return;
		if (event.getCurrentItem().getItemMeta().getDisplayName() == null) return;
		event.setCancelled(true);
		if (event.getSlot() == 10)
			player.performCommand("arena 1");
		else if (event.getSlot() == 12)
			player.performCommand("arena 2");
		else if (event.getSlot() == 14)
			player.performCommand("survival");
		else if (event.getSlot() == 16)
			player.performCommand("lobby");
		else if (event.getSlot() == 40)
			player.closeInventory();
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (player.getInventory().getItemInMainHand().equals(getItemCompass())) {
			if (player.hasPermission("polak.navigator.use")) {
				updateGUI();
				player.openInventory(inv);
				return;
			}
			player.sendMessage("§4You do not have permission to use the server navigator.");
		}
	}
}
