package api.peridot.periapi.packets;

import io.netty.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.function.BiFunction;

public class SignInput {

    private final Class<?> packetPlayInUpdateSign = Reflection.getMinecraftClass("PacketPlayInUpdateSign");
    private final Class<?> chatBaseComponentClass = Reflection.getMinecraftClass("IChatBaseComponent");
    private final Class<?> chatBaseComponentArrayClass = Reflection.getClass("[L" + chatBaseComponentClass.getName() + ";");
    private final Class<?> blockPositionClass = Reflection.getMinecraftClass("BlockPosition");
    private final Reflection.ConstructorInvoker packetPlayOutOpenSignEditor = Reflection.getConstructor(Reflection.getMinecraftClass("PacketPlayOutOpenSignEditor"), blockPositionClass);
    private final Reflection.ConstructorInvoker blockPosition = Reflection.getConstructor(blockPositionClass, int.class, int.class, int.class);
    private final Reflection.MethodInvoker getText = Reflection.getMethod(chatBaseComponentClass, "getText");
    private final Reflection.MethodInvoker getPositionX = Reflection.getMethod(blockPositionClass, "getX");
    private final Reflection.MethodInvoker getPositionY = Reflection.getMethod(blockPositionClass, "getY");
    private final Reflection.MethodInvoker getPositionZ = Reflection.getMethod(blockPositionClass, "getZ");
    private final Reflection.FieldAccessor<?> SIGN_POSITION = Reflection.getField(packetPlayInUpdateSign, "a", blockPositionClass);
    private final Reflection.FieldAccessor<?> SIGN_MESSAGE = Reflection.getField(packetPlayInUpdateSign, "b", chatBaseComponentArrayClass);

    private final Plugin plugin;
    private final TinyProtocol protocol;

    private String[] text;
    private BiFunction<Player, String[], Boolean> completeFunction;
    private boolean openOnFail;

    private SignInput(Plugin plugin) {
        this.plugin = plugin;

        protocol = new TinyProtocol(plugin) {
            @Override
            public Object onPacketInAsync(Player sender, Channel channel, Object packet) {
                if (SIGN_MESSAGE.hasField(packet)) {
                    String[] result = new String[4];
                    Object[] casted = (Object[]) SIGN_MESSAGE.get(packet);
                    for (int i = 0; i < 4; i++) {
                        result[i] = getText.invoke(casted[i]).toString();
                    }

                    boolean response = completeFunction.apply(sender, result);

                    Object blockPosition = SIGN_POSITION.get(packet);

                    int signX = (int) getPositionX.invoke(blockPosition);
                    int signY = (int) getPositionY.invoke(blockPosition);
                    int signZ = (int) getPositionZ.invoke(blockPosition);

                    Location signLocation = new Location(sender.getWorld(), signX, signY, signZ);
                    Block block = signLocation.getWorld().getBlockAt(signLocation);

                    if (!response && openOnFail) {
                        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> open(sender), 2);
                    } else {
                        sender.sendBlockChange(signLocation, block.getType(), block.getData());
                        protocol.close();
                    }
                }

                return super.onPacketInAsync(sender, channel, packet);
            }
        };
    }

    public void open(Player player) {
        Location location = player.getLocation();
        int y = 255;
        if (location.getBlockX() >= 128) {
            y = 1;
        }
        location.setY(y);

        player.sendBlockChange(location, Material.SIGN_POST, (byte) 0);
        player.sendSignChange(location, text);

        try {
            Object openSign = packetPlayOutOpenSignEditor.invoke(blockPosition.invoke(location.getBlockX(), location.getBlockY(), location.getBlockZ()));

            PacketSender.sendPacket(player, openSign);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String[] text = new String[]{"", "", "", ""};
        private BiFunction<Player, String[], Boolean> completeFunction;
        private boolean openOnFail = false;

        private Plugin plugin;

        public Builder text(String... text) {
            this.text = text;
            return this;
        }

        public Builder completeFunction(BiFunction<Player, String[], Boolean> completeFunction) {
            this.completeFunction = completeFunction;
            return this;
        }

        public Builder openOnFail(boolean openOnFail) {
            this.openOnFail = openOnFail;
            return this;
        }

        public Builder plugin(Plugin plugin) {
            this.plugin = plugin;
            return this;
        }

        public SignInput build() {
            if (this.plugin == null) {
                throw new IllegalStateException("The plugin instance is required");
            }

            SignInput signInput = new SignInput(plugin);

            signInput.text = this.text;
            signInput.completeFunction = completeFunction;
            signInput.openOnFail = openOnFail;

            return signInput;
        }
    }

}