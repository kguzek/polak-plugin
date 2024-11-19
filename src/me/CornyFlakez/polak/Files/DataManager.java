package me.CornyFlakez.polak.Files;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import me.CornyFlakez.polak.Main;

public class DataManager {

	private HashMap<String, FileConfiguration> configData = new HashMap<String, FileConfiguration>();
	
	public void reloadConfigData(String filename) {
		configData.put(filename, (YamlConfiguration.loadConfiguration(new File(Main.plugin.getDataFolder(), filename))));
		InputStream defaultStream = Main.plugin.getResource(filename);
		if (defaultStream == null)
			Main.plugin.getLogger().log(Level.SEVERE, "Could not reload config file for: " + filename);
		else
			configData.get(filename).setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream)));
		
	}
	
	public FileConfiguration getConfig(String filename) {
		if (configData.get(filename) == null || configData.isEmpty())
			reloadConfigData(filename);
		return configData.get(filename);
	}
	
	public void saveConfigFile(String filename) {
		try {
			getConfig(filename).save(new File(Main.plugin.getDataFolder(), filename));
		} catch (IOException e) {
			Main.plugin.getLogger().log(Level.INFO, "Could not save the config to " + filename, e);
		}
	}
	
	// Saves project resource to plugin directory if it doesn't exist yet
	public void saveDefaultConfig(String filename) {
		if (!new File(Main.plugin.getDataFolder(), filename).exists())
			Main.plugin.saveResource(filename, false);
	}
	
}
