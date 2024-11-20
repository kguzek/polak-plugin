package me.CornyFlakez.polak;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class Kits implements CommandExecutor {
	
	public static HashMap<String, String> messages = new HashMap<String, String>();
	public static HashMap<String, ItemStack> items = new HashMap<String, ItemStack>();
	
	public void getHashes() {
		// Predefined messages on getting items
		messages.put("sword", "§b§omocną pałkę§r§6!");
		messages.put("stick", "§b§odługi kij§r§6!");
		messages.put("boots", "§6Teraz masz §b§oszybkie adidasy§r§6 i ");
		// Which itemstack to give depending on argument
		items.put("sword", itemSword());
		items.put("stick", itemStick());
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// Polak command for receiving kits
		if (label.equalsIgnoreCase("polak")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("§4That command can only be used by players.");
				return true;
			}
			Player player = (Player) sender;
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("sword") || args[0].equalsIgnoreCase("stick")) {
					args[0] = args[0].toLowerCase();
					if(messages.isEmpty()) getHashes();
					if (player.hasPermission("polak.kit.use." + args[0])) {
						PlayerInventory i = player.getInventory();
						Boolean notGotBoots = false;
						if (!i.contains(itemBoots()) && i.getBoots() == null && (i.getItemInOffHand() == null || !i.getItemInOffHand().equals(itemBoots())))
							notGotBoots = true;
						if (!i.contains(items.get(args[0])) && (i.getItemInOffHand() == null || !i.getItemInOffHand().equals(items.get(args[0])))) {
							if (notGotBoots) { // Hasn't got either
								i.clear();
								player.sendMessage(messages.get("boots") + messages.get(args[0]));
								i.setBoots(itemBoots());
							}
							i.addItem(items.get(args[0]));
							return true;
						}
						if (notGotBoots) i.setBoots(itemBoots());
						return true;
					}
					player.sendMessage("§4Nie wolno użyć tej komendy za terenem areny PvP.");
					return true;
				}
			}
		}
		
		// incorrect command syntax
		sender.sendMessage("§cUsage: §6/polak §3<§6sword§3|§6stick§3>");
		return false;
	}
	
	private ItemStack itemBoots() {
		ItemStack item = new ItemStack(Material.LEATHER_BOOTS);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§o§lAdidasy");
		List<String> lore = new ArrayList<String>();
		lore.add("§oAdidasy dla najtwardszych Polaków");
		meta.setLore(lore);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10, true);
		item.setItemMeta(meta);		
		return item;
	}
	
	private ItemStack itemSword() {
		ItemStack item = new ItemStack(Material.WOODEN_SWORD);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§o§lPałka");
		List<String> lore = new ArrayList<String>();
		lore.add("§oPałka dla najtwardszych Polaków");
		meta.setLore(lore);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
		item.setItemMeta(meta);		
		return item;
	}
	
	private ItemStack itemStick() {
		ItemStack item = new ItemStack(Material.STICK);
		ItemMeta meta = item.getItemMeta();		
		meta.setDisplayName("§o§lKij");
		List<String> lore = new ArrayList<String>();
		lore.add("§oKij dla najtwardszych Polaków");
		meta.setLore(lore);		
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addEnchant(Enchantment.KNOCKBACK, 3, true);
		item.setItemMeta(meta);		
		return item;
	}

}
