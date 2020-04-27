package api.peridot.periapi.packets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotificationPackets {

    private static Class<?> chatBaseComponentClass;
    private static Class<?> titleActionClass;
    private static Class<?> chatMessageTypeClass;
    private static Object chatMessageType;

    private static Reflection.MethodInvoker getTitleActionEnum;

    private static Enum<?> titleEnum;
    private static Enum<?> subtitleEnum;
    private static Enum<?> timesEnum;

    private static Reflection.ConstructorInvoker packetPlayOutTitle;
    private static Reflection.ConstructorInvoker packetPlayOutChat;

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
            getTitleActionEnum = Reflection.getMethod(titleActionClass, "valueOf", String.class);
            titleEnum = (Enum) getTitleActionEnum.invoke(titleActionClass, "TITLE");
            subtitleEnum = (Enum) getTitleActionEnum.invoke(titleActionClass, "SUBTITLE");
            timesEnum = (Enum) getTitleActionEnum.invoke(titleActionClass, "TIMES");

            packetPlayOutTitle = Reflection.getConstructor(Reflection.getMinecraftClass("PacketPlayOutTitle"), titleActionClass, chatBaseComponentClass, int.class, int.class, int.class);
            if (chatMessageTypeClass == null) {
                packetPlayOutChat = Reflection.getConstructor(Reflection.getMinecraftClass("PacketPlayOutChat"), chatBaseComponentClass, byte.class);
            } else {
                packetPlayOutTitle = Reflection.getConstructor(Reflection.getMinecraftClass("PacketPlayOutChat"), chatBaseComponentClass, chatMessageTypeClass);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static List<Object> createTitlePacket(String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        List<Object> packets = new ArrayList<>();
        try {
            Object titlePacket = packetPlayOutTitle.invoke(titleEnum, CommonPackets.createBaseComponent(title), -1, -1, -1);
            Object subtitlePacket = packetPlayOutTitle.invoke(subtitleEnum, CommonPackets.createBaseComponent(subTitle), -1, -1, -1);
            Object timesPacket = packetPlayOutTitle.invoke(timesEnum, null, 10, 20, 20);

            packets.addAll(Arrays.asList(titlePacket, subtitlePacket, timesPacket));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return packets;
    }

    public static Object createActionBarPacket(String text) {
        Object packet = null;
        try {
            if (chatMessageTypeClass != null) {
                packet = packetPlayOutChat.invoke(CommonPackets.createBaseComponent(text), chatMessageType);
            } else {
                packet = packetPlayOutChat.invoke(CommonPackets.createBaseComponent(text), (byte) 2);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return packet;
    }

}
