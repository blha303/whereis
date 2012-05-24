package tk.blha303;

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
//		saveConfig();
        log.info(String.format("[%s] Enabled version %s", getDescription().getName(), getDescription().getVersion()));
	}
    
    // http://stackoverflow.com/a/2275030
    public boolean contains(String haystack, String needle) {
    	  haystack = haystack == null ? "" : haystack;
    	  needle = needle == null ? "" : needle;
    	  return haystack.toLowerCase().contains(needle.toLowerCase());
    }
    
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	Player player = null;
    	if (sender instanceof Player) {
    		player = (Player) sender;
    	}
     
    	if (cmd.getName().equalsIgnoreCase("whereis")) {
    		if (args.length != 1) {
    			return false;
    		}
    		if (player == null) {
    			String playername = args[0].toString().toLowerCase();
    	        Player other = (Bukkit.getServer().getPlayer(playername));
    	        if (other == null) {
    	           log.warning(ChatColor.RED + args[0] + " does not match a player name!");
    	           return false;
    	        }
    	        int targetx = other.getLocation().getBlockX();
	        	int targety = other.getLocation().getBlockY();
	        	int targetz = other.getLocation().getBlockZ();
	        	String targetw = other.getLocation().getWorld().getName();
	        	log.info(ChatColor.GREEN + Bukkit.getPlayer(playername).getDisplayName()+" is at X:"+targetx+", Y:"+targety+", Z:"+targetz+" in "+targetw);
    	        return true;
    		} 
    		if (player != null) {
    			if (perms.has(player, node)) {
    				String playername = args[0].toString().toLowerCase();
    				Player other = (Bukkit.getServer().getPlayer(playername));
    				if (other == null) {
    					sender.sendMessage(ChatColor.RED + args[0] + " does not match a player name!");
    					return false;
    				}
    				int targetx = other.getLocation().getBlockX();
    				int targety = other.getLocation().getBlockY();
    				int targetz = other.getLocation().getBlockZ();
    				String targetw = other.getLocation().getWorld().getName();
    				if (player.getWorld().getName() == other.getWorld().getName()) {
    					player.sendMessage(ChatColor.GREEN + Bukkit.getPlayer(playername).getDisplayName()+" is at X:"+targetx+", Y:"+targety+", Z:"+targetz);
    				} else {
    					player.sendMessage(ChatColor.GREEN + Bukkit.getPlayer(playername).getDisplayName()+" is at X:"+targetx+", Y:"+targety+", Z:"+targetz+" in "+targetw);
    				}
    				return true;
    			} else {
    				player.sendMessage("You don't have permission to use this command.");
    				return true;
    			}
    		}
    	}
		return false;
    }
    
}
