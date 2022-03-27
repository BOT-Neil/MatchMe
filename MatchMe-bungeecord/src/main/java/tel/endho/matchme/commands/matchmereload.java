package tel.endho.matchme.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import tel.endho.matchme.MatchMeBungeeCord;

public class matchmereload extends Command {
  private MatchMeBungeeCord me;

  public matchmereload(MatchMeBungeeCord me) {
    super("gmatchmereload", "matchme.admin");
    this.me = me;
  }

  @Override
  public void execute(CommandSender commandSender, String[] string) {
    if (commandSender instanceof ProxiedPlayer) {
      if (commandSender.hasPermission("matchme.admin")) {
        me.loadData();
      } else {
        BaseComponent baseComponent = new TextComponent("You're not admin.");
        ((ProxiedPlayer) commandSender).sendMessage(baseComponent);
      }
    }
  }
}
