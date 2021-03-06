package tel.endho.matchme;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;
import tel.endho.matchme.commands.matchme;
import tel.endho.matchme.commands.matchmereload;

public final class MatchMeBungeeCord extends Plugin implements Listener {
  public Configuration config;
  private Plugin plugin;

  public Plugin getPlugin() {
    return plugin;
  }

  public MatchMeBungeeCord() throws IOException {
    this.plugin = this;
  }

  @Override
  public void onEnable() {
    getProxy().registerChannel("matchme:matchme");
    getProxy().getPluginManager().registerListener(this, this);
    getLogger().info("MatchMeBungee Loaded");
    try {
      this.saveDefaultConfig();
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
    getProxy().getScheduler().schedule(this, new DelayedStart(this), 60, TimeUnit.SECONDS);
    getProxy().getPluginManager().registerCommand(this, new matchme(this));
    getProxy().getPluginManager().registerCommand(this, new matchmereload(this));
  }

  @EventHandler
  public void onPluginMessage(PluginMessageEvent event) {
    if (event.getTag().equalsIgnoreCase("matchme:matchme")) {
      ByteArrayDataInput byteArray = ByteStreams.newDataInput(event.getData());
      // String mm = byteArray.readUTF();
      byteArray.readUTF();
      String group = byteArray.readUTF();
      String name = byteArray.readUTF();
      this.matchMe(name, group);
    }
  }

  public void loadData() {
    this.reloadConfig();
    getLogger().info("MatchMeDebug");
    MMConfig.motd.clear();
    MMConfig.groupMap.clear();
    MMConfig.groupsortOption.clear();
    this.config.getStringList("motd").forEach(motd -> {
      MMConfig.motd.add(motd);
    });
    this.config.getSection("groups").getKeys().forEach(groupname -> {
      MMConfig.groupMap.put(groupname, new TreeMap<>());
      Boolean sortmotd = this.config.getSection("groups." + groupname).getBoolean("sortmotd");
      MMConfig.groupsortOption.put(groupname, sortmotd);
      Configuration groupsection = this.config.getSection("groups." + groupname);
      groupsection.getStringList("servers").forEach(servername -> {
        ServerInfo serverInfo = getProxy().getServerInfo(servername);
        if (serverInfo != null) {
          // getLogger().info("serveraddressdebug:
          // "+serverInfo.getSocketAddress().toString());
          String[] parts = serverInfo.getSocketAddress().toString().split("/");
          String[] partsb = parts[1].split(":");
          String ip = partsb[0];
          String port = partsb[1];
          MMConfig.groupMap.get(groupname)
              .put(new ServerStatus(servername, ip, Integer.valueOf(port), config.getInt("timeoutms")), servername);
        }
      });
      /*
       * groupsection.getKeys().forEach(servername -> {
       * 
       * //System.out.println("x:" + groupname + " y:" + servername);
       * //System.out.println(this.config.getSection("groups." + groupname + "." +
       * servername).getString("ip")+ this.config.getSection("groups." + groupname +
       * "." + servername).getInt("port"));
       * MMConfig.groupMap.get(groupname)
       * .put(new ServerStatus(servername,
       * this.config.getSection("groups." + groupname + "." +
       * servername).getString("ip"),
       * this.config.getSection("groups." + groupname + "." +
       * servername).getInt("port"),
       * this.config.getInt("timeoutms")), servername);
       * });
       */
      /*
       * this.config.getSection("groups").getList(groupname).forEach(l -> {
       * ServerInfo serverInfo = getProxy().getServerInfo((String) l);
       * getLogger().info("ggetsocketaddresstostring" +
       * serverInfo.getSocketAddress().toString());
       * // groupMap.get(x).put(new
       * // ServerStatus(serverInfo.getName(),serverInfo.getSocketAddress().))
       * });
       */
      // else {this.config.getSection("motd").getKeys().forEach((mtd)->{motd.add(mtd);
      // });}
    });
  }

  public void matchMeProxiedPlayer(ProxiedPlayer pp, String servergroup) {
    try {
      if (MMConfig.groupsortOption.get(servergroup).booleanValue() == false) {
pp.connect(this.getProxy()
          .getServerInfo(MMConfig.groupMap.get(servergroup).keySet().stream().filter(ServerStatus::isOpen)
              .sorted(Comparator.comparing(ServerStatus::getName).thenComparingInt(ServerStatus::getOnline).reversed())
              .iterator().next().getName()));
      }else{
        pp.connect(this.getProxy()
            .getServerInfo(MMConfig.groupMap.get(servergroup).keySet().stream().filter(ServerStatus::isOpen)
                .sorted(
                    Comparator.comparing(ServerStatus::getName).thenComparingInt(ServerStatus::getOnline).reversed().thenComparing(ServerStatus::getStatus))
                .iterator().next().getName()));
      }
      
      /*getLogger().info("is open:" + MMConfig.groupMap.get(servergroup).keySet().stream().filter(ServerStatus::isOpen)
          .sorted(Comparator.comparing(ServerStatus::getName).thenComparingInt(ServerStatus::getOnline).reversed())
          .iterator().next().isOpen());*/
      // pp.connect(this.getProxy().getServerInfo(groupMap.get(servergroup).keySet().stream().filter(ServerStatus::isOpen).sorted(Comparator.comparingInt(ServerStatus::getOnline).reversed().thenComparing(ServerStatus::getName)).iterator().next().getName()));
    } catch (Exception e) {
      BaseComponent baseComponent = new TextComponent("No Server Available.");
      pp.sendMessage(baseComponent);
      // pp.sendMessage("No Server Available.");
    }
  }

  public void matchMe(String player, String servergroup) {
    ProxiedPlayer pp = this.getProxy().getPlayer(player);
    try {
      // todo randomize instead of sort by name
      // getLogger().info(servergroup);
      // getLogger().info(groupMap.keySet().iterator().next());
      // System.out.println(groupMap.get(servergroup).keySet().stream().iterator().next().getStatus());

      if (MMConfig.groupsortOption.get(servergroup).booleanValue() == false) {
        pp.connect(this.getProxy()
            .getServerInfo(MMConfig.groupMap
                .get(servergroup).keySet().stream().filter(ServerStatus::isOpen).sorted(Comparator
                    .comparing(ServerStatus::getName).reversed().thenComparingInt(ServerStatus::getOnline).reversed())
                .iterator().next().getName()));
      } else {
        pp.connect(this.getProxy()
            .getServerInfo(MMConfig.groupMap
                .get(servergroup).keySet().stream().filter(ServerStatus::isOpen).sorted(Comparator
                    .comparing(ServerStatus::getName).reversed().thenComparingInt(ServerStatus::getOnline).reversed().thenComparing(ServerStatus::getStatus))
                .iterator().next().getName()));
      }
      // getLogger().info("is
      // open:"+groupMap.get(servergroup).keySet().stream().filter(ServerStatus::isOpen).sorted(Comparator.comparing(ServerStatus::getName).reversed().thenComparingInt(ServerStatus::getOnline).reversed()).iterator().next().isOpen());
      // pp.connect(this.getProxy().getServerInfo(groupMap.get(servergroup).keySet().stream().filter(ServerStatus::isOpen).sorted(Comparator.comparingInt(ServerStatus::getOnline).reversed().thenComparing(ServerStatus::getName)).iterator().next().getName()));
    } catch (Exception e) {
      BaseComponent baseComponent = new TextComponent("No Server Available.");
      pp.sendMessage(baseComponent);
      /*
       * getLogger().info(e.getMessage());
       * Arrays.stream(e.getStackTrace()).forEach(x->{
       * getLogger().info(x.toString());
       * });
       */
      // pp.sendMessage("No Server Available.");
    }
  }

  protected Configuration getConfig() {
    return this.config;
  }

  protected void reloadConfig() {
    try {
      this.config = ConfigurationProvider.getProvider(YamlConfiguration.class)
          .load(new File(this.getDataFolder(), "config.yml"));
    } catch (IOException var4) {
      throw new RuntimeException("Unable to load configuration", var4);
    }
  }

  private void saveDefaultConfig() throws Throwable {
    if (!getDataFolder().exists()) {
      getDataFolder().mkdir();
    }
    File configFile = new File(getDataFolder(), "config.yml");
    if (!configFile.exists()) {
      try {
        configFile.createNewFile();
        try (InputStream is = getResourceAsStream("config.yml");
            OutputStream os = new FileOutputStream(configFile)) {
          ByteStreams.copy(is, os);
        }
      } catch (IOException e) {
        throw new RuntimeException("Unable to create configuration file", e);
      }
    }
  }
}
