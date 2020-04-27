package api.peridot.periapi.packets;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class PacketSender {

    private static Reflection.MethodInvoker sendPacket;
    private static Class<?> PLAYER_CONNECTION_CLASS;

    public static void sendPacket(Object... packets) {
        sendPacket(Arrays.asList(packets));
    }

    public static void sendPacket(List<Object> packets) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendPacket(player, packets);
        }
    }

    public static void sendPacket(List<Player> players, Object... packets) {
        sendPacket(players, Arrays.asList(packets));
    }


    public static void sendPacket(List<Player> players, List<Object> packets) {
        if (players.isEmpty()) return;

        for (Player player : players) {
            sendPacket(player, packets);
        }
    }

    public static void sendPacket(Player player, Object... packets) {
        sendPacket(player, Arrays.asList(packets));
    }

    public static void sendPacket(Player player, List<Object> packets) {
        if (player == null) return;
        try {
            for (Object packet : packets) {
                sendPacket.invoke(getConnection(player), packet);
            }
        } catch (Exception ex) {
            Bukkit.getLogger().severe("Could not send packet to player");
            ex.printStackTrace();
        }
    }

    private static Object getConnection(Player player) {
        Object connection = null;
        try {
            Reflection.MethodInvoker getHandle = Reflection.getMethod(player.getClass(), "getHandle");
            Object nmsPlayer = getHandle.invoke(player);
            Reflection.FieldAccessor<?> connectionField = Reflection.getField(nmsPlayer.getClass(),"playerConnection", PLAYER_CONNECTION_CLASS);
            connection = connectionField.get(nmsPlayer);
        } catch (Exception ex) {
            Bukkit.getLogger().severe("Could not get player connection");
            ex.printStackTrace();
        }
        return connection;
    }

    static {
        try {
            PLAYER_CONNECTION_CLASS = Reflection.getMinecraftClass("PlayerConnection");
            sendPacket = Reflection.getMethod(PLAYER_CONNECTION_CLASS, "sendPacket", Reflection.getMinecraftClass("Packet"));
        } catch (Exception ex) {
            Bukkit.getLogger().severe("Could not setup PacketSender");
            ex.printStackTrace();
        }
    }

}
