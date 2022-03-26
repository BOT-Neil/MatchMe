package tel.endho.matchme;

import java.util.concurrent.TimeUnit;

public class DelayedStart implements Runnable {
  private MatchMeBungeeCord instance;
  public DelayedStart(MatchMeBungeeCord plugin){
    this.instance=plugin;
  }
  @Override
  public void run(){
    instance.loadData();
    instance.getProxy().getScheduler().schedule(instance, new Pinger(), 0, instance.config.getInt("updatespeedms"),
        TimeUnit.MILLISECONDS);
  }
}
