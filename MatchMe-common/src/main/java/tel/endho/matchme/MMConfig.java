package tel.endho.matchme;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

public class MMConfig {
    public static List<String> motd = new LinkedList<>();
    public static TreeMap<String, TreeMap<ServerStatus, String>> groupMap = new TreeMap<>();
    public static TreeMap<String, Boolean> groupsortOption= new TreeMap<>();
}
