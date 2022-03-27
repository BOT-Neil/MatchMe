package tel.endho.matchme;

import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Pinger implements Runnable {
  public Pinger() {

  }

  @Override
  public void run() {
    MMConfig.groupMap.entrySet().forEach(xxx -> {
      String groupname = xxx.getKey();
      TreeMap<ServerStatus, String> groupmap = xxx.getValue();
      groupmap.keySet().forEach(serverstatus -> {
        try {
          serverstatus.update();
        } catch (Exception e) {
          serverstatus.setClosed();
        }
        if (serverstatus.getStatus() == null) {
          serverstatus.setClosed();
          return;
        } else {
          if (serverstatus.getOnline() >= serverstatus.getmaxPlayers() - 1) {
            serverstatus.setClosed();
            return;
          }
          if (MMConfig.groupsortOption.get(groupname).booleanValue() == true) {
            serverstatus.setOpen();
            return;
          }
          AtomicInteger n = new AtomicInteger();
          while (n.get() == 0) {
            AtomicReference<Boolean> loop = new AtomicReference<>(true);
            MMConfig.motd.forEach((motd) -> {
              if (loop.get()) {
                if (serverstatus.getStatus().toLowerCase().contains(motd.toLowerCase())) {
                  serverstatus.setOpen();
                  n.getAndIncrement();
                  loop.set(false);
                } else {
                  serverstatus.setClosed();
                  // System.out.println("debug4");
                  n.getAndIncrement();
                }
              }

            });
          }
        }

      });
    });
  }
}