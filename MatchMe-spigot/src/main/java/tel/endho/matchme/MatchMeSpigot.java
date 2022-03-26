package tel.endho.matchme;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class MatchMeSpigot extends JavaPlugin {
  public static MatchMeSpigot plugin;
  @Override
  public void onEnable() {
    plugin=this;

    this.getServer().getMessenger().registerOutgoingPluginChannel(this, "matchme:matchme");
    this.getCommand("matchme").setExecutor(new CommandKit());
  }

  public void matchPlayer(Player p, String s) {
    ByteArrayDataOutput out = ByteStreams.newDataOutput();
    out.writeUTF("matchme");
    out.writeUTF(s);
    out.writeUTF(p.getName());
    Player player = p;
    player.sendPluginMessage(this, "matchme:matchme", out.toByteArray());
  }

  public void matchMe(Player p, String s) {
    try {
      matchPlayer(p, s);
      // sendPlayer(p,
      // groupMap.get(s).keySet().stream().filter(ServerStatus::isOpen).sorted(Comparator.comparingInt(ServerStatus::getOnline).reversed().thenComparing(ServerStatus::getName)).iterator().next().getName());
    } catch (Exception e) {
      System.out.println(e.getMessage());
      p.sendMessage("No Server Available.");
    }
  }

  public class CommandKit implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (sender instanceof Player) {
        Player player = (Player) sender;
        matchMe(player, args[0]);
      }
      // If the player (or console) uses our command correct, we can return true
      return true;
    }
  }
}