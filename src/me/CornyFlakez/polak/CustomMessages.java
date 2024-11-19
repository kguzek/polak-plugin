package me.CornyFlakez.polak;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class CustomMessages implements Listener {
	
	@EventHandler
	public void onChatMessage(AsyncPlayerChatEvent event) {
		String[] chatColorCodes = new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "k", "l", "m", "n", "o", "r"};
		for (String code : chatColorCodes)
			event.setMessage(event.getMessage().replace("&" + code, "§" + code));
		for (String group : Main.plugin.getConfig().getConfigurationSection("chatFormats").getKeys(false)) {
			if (event.getPlayer().hasPermission("group." + group)) {
				String messageFormat = Main.plugin.getConfig().getString("chatFormats." + group);
				for (String code : chatColorCodes)
					messageFormat = messageFormat.replace("&" + code, "§" + code);
				messageFormat = messageFormat.replace("{SENDER}", event.getPlayer().getDisplayName()).replace("{MESSAGE}", event.getMessage());
				event.setFormat(messageFormat);
			}
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		String deathCause = event.getEntity().getLastDamageCause().getCause().toString();
		Boolean changedDeathMessage = false;
		for (String messageKey : Main.plugin.getConfig().getConfigurationSection("deathMessages").getKeys(false)) {
			if (messageKey.equalsIgnoreCase(deathCause)) {
				String newDeathMessage = Main.plugin.getConfig().getString("deathMessages." + messageKey);
				event.setDeathMessage(newDeathMessage.replace('&','§').replace("{PLAYER}",event.getEntity().getDisplayName()));
				changedDeathMessage = true;
				break;
			}
		}
		if (!(changedDeathMessage))
			event.getEntity().sendMessage("§6Your death cause was: §3" + deathCause);
	}
}
