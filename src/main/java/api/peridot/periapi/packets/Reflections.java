package api.peridot.periapi.packets;

import org.bukkit.Bukkit;

public class Reflections {

    public static String server_version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    public static final boolean use_pre_12_methods = Integer.parseInt(server_version.split("_")[1]) < 12;

    public static Class<?> getNMSClass(String name) {
        Class<?> nmsClass = null;
        try {
            nmsClass = Class.forName("net.minecraft.server." + server_version + "." + name);
        } catch (Exception ex) {
            Bukkit.getLogger().severe("Could not get class with specify name");
            ex.printStackTrace();
        }
        return nmsClass;
    }

    public static Class<?> getBukkitClass(String name) {
        Class<?> bukkitClass = null;
        try {
            bukkitClass = Class.forName("org.bukkit.craftbukkit." + server_version + "." + name);
        } catch (Exception ex) {
            Bukkit.getLogger().severe("Could not get class with specify name");
            ex.printStackTrace();
        }
        return bukkitClass;
    }

}
