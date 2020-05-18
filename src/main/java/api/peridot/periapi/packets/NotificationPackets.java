package api.peridot.periapi.packets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotificationPackets {

    private static Class<?> chatBaseComponentClass;
    private static Class<?> titleActionClass;
    private static Class<?> chatMessageTypeClass;
    private static Object chatMessageType;

    private static Reflection.MethodInvoker titleActionEnumMethod;

    private static Enum<?> titleEnum;
    private static Enum<?> subtitleEnum;
    private static Enum<?> timesEnum;

    private static Reflection.ConstructorInvoker packetPlayOutTitleConstructor;
    private static Reflection.ConstructorInvoker packetPlayOutChatConstructor;

    static {
        try {
            chatBaseComponentClass = Reflection.getMinecraftClass("IChatBaseComponent");

            if (Reflection.usePre12Methods) {
                chatMessageTypeClass = null;
            } else {
                chatMessageTypeClass = Reflection.getMinecraftClass("ChatMessageType");
                chatMessageType = chatMessageTypeClass.getEnumConstants()[2];
            }

            titleActionClass = Reflection.serverVersion.equals("v1_8_R1") ? Reflection.getMinecraftClass("EnumTitleAction") : Reflection.getMinecraftClass("PacketPlayOutTitle$EnumTitleAction");
            titleActionEnumMethod = Reflection.getMethod(titleActionClass, "valueOf", String.class);
            titleEnum = (Enum) titleActionEnumMethod.invoke(titleActionClass, "TITLE");
            subtitleEnum = (Enum) titleActionEnumMethod.invoke(titleActionClass, "SUBTITLE");
            timesEnum = (Enum) titleActionEnumMethod.invoke(titleActionClass, "TIMES");

            packetPlayOutTitleConstructor = Reflection.getConstructor(Reflection.getMinecraftClass("PacketPlayOutTitle"), titleActionClass, chatBaseComponentClass, int.class, int.class, int.class);
            if (chatMessageTypeClass == null) {
                packetPlayOutChatConstructor = Reflection.getConstructor(Reflection.getMinecraftClass("PacketPlayOutChat"), chatBaseComponentClass, byte.class);
            } else {
                packetPlayOutChatConstructor = Reflection.getConstructor(Reflection.getMinecraftClass("PacketPlayOutChat"), chatBaseComponentClass, chatMessageTypeClass);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static List<Object> createTitlePacket(String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        List<Object> packets = new ArrayList<>();
        try {
            Object titlePacket = packetPlayOutTitleConstructor.invoke(titleEnum, CommonPackets.createBaseComponent(title), -1, -1, -1);
            Object subtitlePacket = packetPlayOutTitleConstructor.invoke(subtitleEnum, CommonPackets.createBaseComponent(subTitle), -1, -1, -1);
            Object timesPacket = packetPlayOutTitleConstructor.invoke(timesEnum, null, 10, 20, 20);

            packets.addAll(Arrays.asList(titlePacket, subtitlePacket, timesPacket));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return packets;
    }

    public static Object createActionBarPacket(String text) {
        Object packet = null;
        if (chatMessageTypeClass != null) {
            packet = packetPlayOutChatConstructor.invoke(CommonPackets.createBaseComponent(text), chatMessageType);
        } else {
            packet = packetPlayOutChatConstructor.invoke(CommonPackets.createBaseComponent(text), (byte) 2);
        }
        return packet;
    }

}
