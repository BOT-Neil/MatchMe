package tel.endho.matchme;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

public class MMConfig {
  //todo make a class for groupsettings instead of strings in maps  
  // TreeMap<GroupClass, TreeMap<ServerStatus, String>>
    public static List<String> motd = new LinkedList<>();
    public static TreeMap<String, TreeMap<ServerStatus, String>> groupMap = new TreeMap<>();
    public static TreeMap<String, Boolean> groupsortOption= new TreeMap<>();
}
