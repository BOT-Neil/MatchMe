package tel.endho.matchme;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.Objects;

public class ServerStatus implements Comparable<ServerStatus>{
    private String name, ip, status;

    private int port;
    private Integer playercount;
    private int maxpcount;
    private int timoutms;
    private Boolean Open;
    public ServerStatus(String name, String ip, int port,int timeoutms) {
        this.setClosed();
        this.playercount = 0;
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.timoutms = timeoutms;
    }
    public void setOpen(){
        this.Open = true;
    }
    public void setClosed(){
        this.Open = false;
    }
    public Boolean isOpen(){ return Open; }
    public String getName() {
        return name;
    }
    public String getIP() {
        return ip;
    }
    public int getPort() {
        return port;
    }
    public Integer getOnline() {
        return playercount;
    }
    public int getmaxPlayers() {
        return maxpcount;
    }
    public String getStatus() {
        return status;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ServerStatus simpson = (ServerStatus) o;
        return name == simpson.name &&
                name.equals(simpson.name);
    }
    @Override
    public int compareTo(ServerStatus other) {
        return Comparator.comparingInt(ServerStatus::getOnline).thenComparing(ServerStatus::isOpen).thenComparing(ServerStatus::getName).thenComparingInt(ServerStatus::getOnline).compare(this, other);
    }
    @Override
    public int hashCode() {
        int prime = 31;
        return prime + Objects.hashCode(this.name);
    }
    public void update() throws IOException {

        Socket socket = new Socket();
        OutputStream outputStream;
        DataOutputStream dataOutputStream;
        InputStream inputStream;
        InputStreamReader inputStreamReader;
        socket.setSoTimeout(timoutms);

        socket.connect(new InetSocketAddress(ip, port), timoutms);
        outputStream = socket.getOutputStream();
        dataOutputStream = new DataOutputStream(outputStream);
        inputStream = socket.getInputStream();
        inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_16BE);
        dataOutputStream.write(new byte[]{(byte) 0xFE,(byte) 0x01});
        int packetId = inputStream.read();

        if (packetId == -1) {
            dataOutputStream.close();
            outputStream.close();
            inputStreamReader.close();
            inputStream.close();
            socket.close();
            throw new IOException("Premature end of stream");
        } if (packetId != 0xFF) {
            dataOutputStream.close();
            outputStream.close();
            inputStreamReader.close();
            inputStream.close();
            socket.close();
            throw new IOException("Invalid packet ID (" + packetId + ").");
            //packet moze byt spravny
        }

        int length = inputStreamReader.read();

        if (length == -1) {
            dataOutputStream.close();
            outputStream.close();
            inputStreamReader.close();
            inputStream.close();
            socket.close();
            throw new IOException("Premature end of stream");
        } if (length == 0) {
            dataOutputStream.close();
            outputStream.close();
            inputStreamReader.close();
            inputStream.close();
            socket.close();
            throw new IOException("Invalid string length");
        }

        char[] chars = new char[length];

        if (inputStreamReader.read(chars,0,length) != length) {
            dataOutputStream.close();
            outputStream.close();
            inputStreamReader.close();
            inputStream.close();
            socket.close();
            throw new IOException("Premature end of stream.");
        }

        String string = new String(chars);
        if (string.startsWith("§")) {
            String[] data = string.split("\0");
            String motd = data[3];

            int onlinePlayers = Integer.valueOf(data[4]);
            int maxPlayers = Integer.valueOf(data[5]);
            this.status = motd;
            this.playercount = onlinePlayers;
            this.maxpcount = maxPlayers;


            dataOutputStream.close();
            outputStream.close();
            inputStreamReader.close();
            inputStream.close();
            socket.close();
        }
    }
}
