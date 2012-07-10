package tk.blha303;

/*
 * v2.3
 * Implemented multi-world support, on request from lochlain on mcaddictgallery.info.
 * It's the same multi-world setup I used on PlacePay. Maybe buggy, but I'm sure server owners can work around it.
 * 
 * v2.2
 * Changed colours on the location messages. Now if a player is op, the red op colour won't reset the rest of the message to white.
 * (For some reason, Eclipse thinks 'colour' is spelt wrong. Nooooope. I'm right.)
 * World names are also in red now.
 * Modified 'permission denied' message slightly to tell the console that someone was denied access to the command.
 * 
 * v2.1
 * '/whereis' on its own now shows version number as well as usage info.
 * 
 * v2.0
 * Now has permission node (whereis.allow) and therefore requires Vault. Players have this node by default.
 * Now shows what world a player is in, if they're in a different world. (player command only)
 * 
 * v1.1
 * Recompiled in Java 6. Is now compatible with 6/7.
 * 
 * v1.0
 * /whereis PLAYERNAME returns their coordinates
 * No restrictions as yet. Permissions will be in the next full release.
 * Not multi-world. This will also be in the next full release.
 */

import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class WhereIs extends JavaPlugin implements Listener {
	private static final Logger log = Logger.getLogger("Minecraft");
	public static WhereIs plugin;
    public static Permission perms = null;
    String node = "whereis.allow";
    
    @Override
    public void onDisable() {
        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }
    
	@Override
	public void onEnable() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			log.severe(String.format("[%s] Disabled. Vault is missing!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		getServer().getPluginManager().registerEvents(this, this);
		setupPermissions();
		saveConfig();
        log.info(String.format("[%s] Enabled version %s", getDescription().getName(), getDescription().getVersion()));
	}
    
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
    
    // http://stackoverflow.com/a/2275030
    public boolean contains(String haystack, String needle) {
    	  haystack = haystack == null ? "" : haystack;
    	  needle = needle == null ? "" : needle;
    	  return haystack.toLowerCase().contains(needle.toLowerCase());
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	Player player = null;
    	if (sender instanceof Player) {
    		player = (Player) sender;
    	}
     
    	if (cmd.getName().equalsIgnoreCase("whereis")) {
    		if (args.length != 1) {
    			if (player == null) {
    				log.info("WhereIs version " + getDescription().getVersion());
    				log.info("whereis [player]");
    			}
    			if (player != null) {
    				player.sendMessage("WhereIs version " + getDescription().getVersion());
    				player.sendMessage("/whereis [player]");
    			}
    			return true;
    		}
    		if (player == null) {
    			String playername = args[0].toString().toLowerCase();
    	        Player other = (Bukkit.getServer().getPlayer(playername));
    	        if (other == null) {
    	           log.warning(ChatColor.RED + args[0] + " does not match a player name!");
    	           return true;
    	        }
    	        int targetx = other.getLocation().getBlockX();
	        	int targety = other.getLocation().getBlockY();
	        	int targetz = other.getLocation().getBlockZ();
	        	String targetw = other.getLocation().getWorld().getName();
	        	log.info(ChatColor.GREEN + Bukkit.getPlayer(playername).getDisplayName()+ChatColor.GREEN+" is at X:"+targetx+", Y:"+targety+", Z:"+targetz+" in "+ChatColor.RED+targetw);
    	        return true;
    		} 
    		if (player != null) {
    			if (perms.has(player, node)) {
    				log.info("[PLAYER COMMAND] " + player.getDisplayName() + " used /whereis " + args[0]);
    				String playername = args[0].toString().toLowerCase();
    				Player other = Bukkit.getServer().getPlayer(playername);
    				if (other == null) {
    					sender.sendMessage(ChatColor.RED + args[0] + " does not match a player name!");
    					return true;
    				}
    				if (contains(getConfig().getString("world"), other.getWorld().getName())) {
    					sender.sendMessage(ChatColor.RED + other.getDisplayName() + " is on a world where location is prohibited.");
    					return true;
    				}
    				int targetx = other.getLocation().getBlockX();
    				int targety = other.getLocation().getBlockY();
    				int targetz = other.getLocation().getBlockZ();
    				String targetw = other.getLocation().getWorld().getName();
    				if (player.getWorld().getName() == other.getWorld().getName()) {
    					player.sendMessage(ChatColor.GREEN + Bukkit.getPlayer(playername).getDisplayName()+ChatColor.GREEN+" is at X:"+targetx+", Y:"+targety+", Z:"+targetz);
    				} else {
    					player.sendMessage(ChatColor.GREEN + Bukkit.getPlayer(playername).getDisplayName()+ChatColor.GREEN+" is at X:"+targetx+", Y:"+targety+", Z:"+targetz+" in "+ChatColor.RED+targetw);
    				}
    				return true;
    			} else {
    				player.sendMessage("You don't have permission to use this command.");
    				log.info("[WhereIs] " + player.getName() + " was denied access to /whereis " + args[0]);
    				return true;
    			}
    		}
    	}
		return false;
    }
    
}
