package tel.endho.matchme;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

@Plugin(
        id = "matchme-velocity",
        name = "MatchMe",
        version = BuildConstants.VERSION,
        description = "Universal MatchMaking plugin for minigames or lobbies",
        url = "https://endho.tel",
        authors = {"Trashuliius"}
)
public class MatchMeVelocity {
    private ProxyServer proxyServer;
    private Toml config;
    private Toml generalconfig;
    private Logger logger;
    private Toml loadConfig(Path path) {
        File folder = path.toFile();
        File file = new File(folder, "config.toml");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (!file.exists()) {
            try (InputStream input = getClass().getResourceAsStream("/" + file.getName())) {
                if (input != null) {
                    Files.copy(input, file.toPath());
                } else {
                    file.createNewFile();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
                return null;
            }
        }

        return new Toml().read(file);
    }

    @Inject
    public MatchMeVelocity(ProxyServer proxyServer,Logger logger, @DataDirectory final Path folder){
        this.proxyServer = proxyServer;
        this.logger = logger;
        config = loadConfig(folder);
        loadGroups();
        proxyServer.getScheduler()
                .buildTask(this,new Pinger())
                .repeat(200L,TimeUnit.MILLISECONDS)
                .schedule();
        this.logger.info("MatchMe Loaded");
    }
    @Subscribe
    public void OnEvent(PluginMessageEvent event){
        if(event.getIdentifier().getId().equals("matchme:matchme")){
            ByteArrayDataInput byteArray = ByteStreams.newDataInput(event.getData());
            //String mm = byteArray.readUTF();
            byteArray.readUTF();
            String group = byteArray.readUTF();
            String name = byteArray.readUTF();
            this.matchMe(name,group);
        }
    }
    public void matchMe(String playe, String servergroup){
        if(proxyServer.getPlayer(playe).isEmpty()){
            return;
        }
        Player player=proxyServer.getPlayer(playe).get();
        try {
            player.createConnectionRequest(proxyServer.getServer(MMConfig.groupMap.get(servergroup).keySet().stream().filter(ServerStatus::isOpen).sorted(Comparator.comparing(ServerStatus::getName).reversed().thenComparingInt(ServerStatus::getOnline).reversed()).iterator().next().getName()).get());
    }catch (Exception e){
            TextComponent textComponent2 = Component.text("No Server Available.");
            player.sendMessage(textComponent2);
        }
    }
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

    }
    private void loadGroups(){
        generalconfig=config.getTable("config");
        config.getTables("groups").forEach(grouptoml -> {
            String groupname = grouptoml.getString("groupname");
            MMConfig.groupMap.putIfAbsent(groupname,new TreeMap<>());
            grouptoml.getList("servers").forEach(servname->{
                String servername= (String)servname;
                proxyServer.getAllServers().forEach(registeredServer -> {
                    ServerInfo serverInfo= registeredServer.getServerInfo();
                    if(serverInfo.getName().equals(servername)){
                        MMConfig.groupMap.get(groupname).put(new ServerStatus(servername,serverInfo.getAddress().getHostName(),serverInfo.getAddress().getPort(),Integer.parseInt(generalconfig.getString("timeout"))),servername);
                    }
                });
            });
        });
    }
}
