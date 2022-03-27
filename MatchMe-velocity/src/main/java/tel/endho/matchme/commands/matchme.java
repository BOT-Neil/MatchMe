package tel.endho.matchme.commands;

import java.util.Arrays;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;

import tel.endho.matchme.MatchMeVelocity;

public class matchme implements SimpleCommand {

  private final MatchMeVelocity server;

  public matchme(MatchMeVelocity server) {
        this.server = server;
    }

    @Override
    public void execute(final Invocation invocation) {
      CommandSource source = invocation.source();
      // Get the arguments after the command alias
      String[] args = invocation.arguments();
      if(source instanceof Player){
        Player player = (Player)source;
        server.matchMe(player.getUsername(), Arrays.stream(args).skip(0).iterator().next());
      }
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
      return invocation.source().hasPermission("matchme.matchme");
    }
}