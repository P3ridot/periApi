package api.peridot.periapi.packets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotificationPackets {

    private static final Class<?> CHAT_BASE_COMPONENT_CLASS = Reflection.getMinecraftClass("IChatBaseComponent");
    private static Class<?> TITLE_ACTION_CLASS;
    private static Class<?> CHAT_MESSAGE_TYPE_CLASS;
    private static Object CHAT_MESSAGE_TYPE;

    private static Reflection.MethodInvoker GET_TITLE_ACTION_ENUM;

    private static Enum<?> TITLE_ENUM;
    private static Enum<?> SUBTITLE_ENUM;
    private static Enum<?> TIMES_ENUM;

    private static final Reflection.ConstructorInvoker PACKET_PLAY_OUT_TITLE = Reflection.getConstructor("PacketPlayOutTitle", TITLE_ACTION_CLASS, CHAT_BASE_COMPONENT_CLASS, int.class, int.class, int.class);
    private static final Reflection.ConstructorInvoker PACKET_PLAY_OUT_CHAT = Reflection.getConstructor("PacketPlayOutChat", CHAT_BASE_COMPONENT_CLASS, CHAT_MESSAGE_TYPE_CLASS);

    static {
        try {
            if (Reflection.usePre12Methods) {
                CHAT_MESSAGE_TYPE_CLASS = null;
            } else {
                CHAT_MESSAGE_TYPE_CLASS = Reflection.getMinecraftClass("ChatMessageType");
                CHAT_MESSAGE_TYPE = CHAT_MESSAGE_TYPE_CLASS.getEnumConstants()[2];
            }

            TITLE_ACTION_CLASS = Reflection.serverVersion.equals("v1_8_R1") ? Reflection.getMinecraftClass("EnumTitleAction") : Reflection.getMinecraftClass("PacketPlayOutTitle$EnumTitleAction");
            GET_TITLE_ACTION_ENUM = Reflection.getMethod(TITLE_ACTION_CLASS, "valueOf", String.class);
            TITLE_ENUM = (Enum) GET_TITLE_ACTION_ENUM.invoke(TITLE_ACTION_CLASS, "TITLE");
            SUBTITLE_ENUM = (Enum) GET_TITLE_ACTION_ENUM.invoke(TITLE_ACTION_CLASS, "SUBTITLE");
            TIMES_ENUM = (Enum) GET_TITLE_ACTION_ENUM.invoke(TITLE_ACTION_CLASS, "TIMES");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static List<Object> createTitlePacket(String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        List<Object> packets = new ArrayList<>();
        try {
            Object titlePacket = PACKET_PLAY_OUT_TITLE.invoke(TITLE_ENUM, CommonPackets.createBaseComponent(title), -1, -1, -1);
            Object subtitlePacket = PACKET_PLAY_OUT_TITLE.invoke(SUBTITLE_ENUM, CommonPackets.createBaseComponent(subTitle), -1, -1, -1);
            Object timesPacket = PACKET_PLAY_OUT_TITLE.invoke(TIMES_ENUM, null, 10, 20, 20);

            packets.addAll(Arrays.asList(titlePacket, subtitlePacket, timesPacket));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return packets;
    }

    public static Object createActionBarPacket(String text) {
        Object packet = null;
        try {
            if (CHAT_MESSAGE_TYPE_CLASS != null) {
                packet = PACKET_PLAY_OUT_CHAT.invoke(CommonPackets.createBaseComponent(text), CHAT_MESSAGE_TYPE);
            } else {
                packet = PACKET_PLAY_OUT_CHAT.invoke(CommonPackets.createBaseComponent(text), (byte) 2);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return packet;
    }

}
