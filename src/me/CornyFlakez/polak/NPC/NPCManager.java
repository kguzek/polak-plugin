package me.CornyFlakez.polak.NPC;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import me.CornyFlakez.polak.Main;
import me.CornyFlakez.polak.Survival;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.MinecraftServer;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_15_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_15_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_15_R1.PlayerConnection;
import net.minecraft.server.v1_15_R1.PlayerInteractManager;
import net.minecraft.server.v1_15_R1.WorldServer;

public class NPCManager {

	public static HashMap<Integer, EntityPlayer> NPCs = new HashMap<Integer, EntityPlayer>();	
	
	static Integer saveNPC(Location loc, String displayName, String[] skin) {
		int id = 1;
		if (Main.data.getConfig("data.yml").contains("createdNPCs")) {
			Set<String> npcs = Main.data.getConfig("data.yml").getConfigurationSection("createdNPCs").getKeys(false);
			String[] npcArray = npcs.toArray(new String[npcs.size()]);
			if (!npcs.isEmpty())
				id += Integer.parseInt(npcArray[npcArray.length - 1]);
		}
		generateNPC(loc, displayName, skin, id);

		Main.data.getConfig("data.yml").set("createdNPCs." + Integer.toString(id) + ".location", Survival.getSerialisedLocation(loc));
		Main.data.getConfig("data.yml").set("createdNPCs." + Integer.toString(id) + ".name", displayName);
		Main.data.getConfig("data.yml").set("createdNPCs." + Integer.toString(id) + ".skin", skin);
		Main.data.saveConfigFile("data.yml");
		return id;
	}

	private static void generateNPC(Location loc, String displayName, String[] skin, int id) {
		MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
		WorldServer world = ((CraftWorld) loc.getWorld()).getHandle();
		GameProfile profile = new GameProfile(UUID.randomUUID(), displayName.replace('&', '§'));
		EntityPlayer npc = new EntityPlayer(server, world, profile, new PlayerInteractManager(world));
		npc.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
		if (skin != null)
			profile.getProperties().put("textures", new Property("textures", skin[0], skin[1]));
		for (Player p : Bukkit.getOnlinePlayers())
			sendNPCPacket(p, npc);
		NPCs.put(id, npc);
	}
	
	public static void restoreNPCs() {
		Bukkit.broadcastMessage("Reading NPCs from file...");
		Bukkit.broadcastMessage("NPCs in file: " + Main.data.getConfig("data.yml").getConfigurationSection("createdNPCs").getKeys(false));
		for (String id : Main.data.getConfig("data.yml").getConfigurationSection("createdNPCs").getKeys(false)) {
			Bukkit.broadcastMessage("Restoring NPC with id " + id);
			Location loc = Survival.getDeserialisedLocation(Main.data.getConfig("data.yml").getString("createdNPCs." + id + ".location"));
			String name = Main.data.getConfig("data.yml").getString("createdNPCs." + id + ".name");
			String[] skin = Main.data.getConfig("data.yml").getStringList("createdNPCs." + id + ".skin").toArray(new String[2]);
			Main.plugin.getLogger().log(Level.INFO, "World name: "  + loc.getWorld().getName());
			generateNPC(loc, name, skin, Integer.parseInt(id));
		}
		Bukkit.broadcastMessage("NPCs in game now: " + NPCs.keySet().toString());
	}

	public static void deleteNPC (int id) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(NPCs.get(id).getId()));
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(
					PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, NPCs.get(id)));
		}
	}
	
	static String[] getSkin(CommandSender sender, String name) {
		try {
			URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
			InputStreamReader reader = new InputStreamReader(url.openStream());
			String uuid = new JsonParser().parse(reader).getAsJsonObject().get("id").getAsString();
			
			URL url2 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
			InputStreamReader reader2 = new InputStreamReader(url2.openStream());
			JsonObject property = new JsonParser().parse(reader2).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
			String texture = property.get("value").getAsString();
			String signature = property.get("signature").getAsString();
			return new String[] {texture, signature};
			
		} catch (Exception e) {
			if (!(sender instanceof Player))
				return null;
			EntityPlayer p = ((CraftPlayer) sender).getHandle();
			GameProfile profile = p.getProfile();
			Property property = profile.getProperties().get("textures").iterator().next();
			String texture = property.getValue();
			String signature = property.getSignature();
			return new String[] {texture, signature};
		}
	}
	
	public static void sendNPCPacket(Player player, EntityPlayer npc) {
		PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
		connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
		connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
		connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256 / 360)));
	}
	
	public static void sendNPCPacketAfterTeleport(Player player, EntityPlayer npc) {
		// Does the same thing as sendNPCPacket() but scheduled one tick later to allow player to finish teleporting
		new BukkitRunnable() { public void run() { sendNPCPacket(player, npc); } }.runTaskLater(Main.plugin, 1);
	}
}
