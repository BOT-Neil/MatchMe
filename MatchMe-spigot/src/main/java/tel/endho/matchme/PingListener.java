package tel.endho.matchme;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class PingListener implements Listener {
  @EventHandler
  public void onPing(ServerListPingEvent event){
    double[] actualTps = TPSUtil.getRecentTps();
    double bozo = actualTps[1];
    event.setMotd(String.valueOf(bozo));
  }

  
}
