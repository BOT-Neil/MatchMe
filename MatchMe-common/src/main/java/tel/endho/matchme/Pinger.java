package tel.endho.matchme;

import tel.endho.matchme.MMConfig;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Pinger implements Runnable {
    public Pinger() {

    }
    @Override
    public void run() {
        MMConfig.groupMap.values().forEach((x)->{
            //todo somewhere around||make y asynchronus maybe for ultra performance
            x.keySet().forEach(y->{
                try {
                    y.update();
                } catch (Exception e) {
                    y.setClosed();
                }
                if (y.getStatus() == null){
                    y.setClosed();
                }else{
                    //String newString = y.getStatus().trim();
                    //me.getLogger().info(newString);
                    //System.out.println(newString);
                    AtomicInteger n = new AtomicInteger();
                    while(n.get() ==0){
                        AtomicReference<Boolean> loop = new AtomicReference<>(true);
                        MMConfig.motd.forEach((f)->{

                            if(loop.get()){
                                if (y.getStatus().toLowerCase().contains(f.toLowerCase())){
                                    //todo make this configurable per group || boolean checkiffull
                                    if(y.getOnline()<=y.getmaxPlayers()-1){
                                        //System.out.println("debug2");
                                        y.setOpen();
                                        n.getAndIncrement();
                                        loop.set(false);
                                    }else {y.setClosed();
                                        //System.out.println("debug3");
                                        n.getAndIncrement();}
                                }else{y.setClosed();
                                    //System.out.println("debug4");
                                    n.getAndIncrement();}
                            }

                        });
                    }
                }

            });
        });
    }
}