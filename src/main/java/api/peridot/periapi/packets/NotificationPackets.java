package api.peridot.periapi.packets;

import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotificationPackets {

    private static final Class<?> PACKET_PLAY_OUT_TITLE_CLASS = Reflections.getNMSClass("PacketPlayOutTitle");
    private static final Class<?> PACKET_PLAY_OUT_CHAT_CLASS = Reflections.getNMSClass("PacketPlayOutChat");
    private static final Class<?> CHAT_BASE_COMPONENT_CLASS = Reflections.getNMSClass("IChatBaseComponent");
    private static Class<?> CRAFT_CHAT_MESSAGE_CLASS;
    private static Class<?> TITLE_ACTION_CLASS;
    private static Class<?> CHAT_MESSAGE_TYPE_CLASS;

    private static Method CREATE_BASE_COMPONENT;
    private static Method GET_TITLE_ACTION_ENUM;

    private static Enum<?> TITLE_ENUM;
    private static Enum<?> SUBTITLE_ENUM;
    private static Enum<?> TIMES_ENUM;

    static {
        try {
            CRAFT_CHAT_MESSAGE_CLASS = Reflections.getBukkitClass("util.CraftChatMessage");
            CREATE_BASE_COMPONENT = Reflections.server_version.equals("v1_8_R1") ? Reflections.getNMSClass("ChatSerializer").getMethod("a", String.class) : Reflections.getNMSClass("IChatBaseComponent$ChatSerializer").getMethod("a", String.class);

            if (Reflections.use_pre_12_methods) {
                CHAT_MESSAGE_TYPE_CLASS = null;
            } else {
                CHAT_MESSAGE_TYPE_CLASS = Reflections.getNMSClass("ChatMessageType");
            }

            TITLE_ACTION_CLASS = Reflections.server_version.equals("v1_8_R1") ? Reflections.getNMSClass("EnumTitleAction") : Reflections.getNMSClass("PacketPlayOutTitle$EnumTitleAction");
            GET_TITLE_ACTION_ENUM = TITLE_ACTION_CLASS.getMethod("valueOf", String.class);
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
            Constructor<?> titlePacketConstructor = PACKET_PLAY_OUT_TITLE_CLASS.getConstructor(TITLE_ACTION_CLASS, CHAT_BASE_COMPONENT_CLASS, int.class, int.class, int.class);

            Object titlePacket = titlePacketConstructor.newInstance(TITLE_ENUM, createBaseComponent(title), -1, -1, -1);
            Object subtitlePacket = titlePacketConstructor.newInstance(SUBTITLE_ENUM, createBaseComponent(subTitle), -1, -1, -1);
            Object timesPacket = titlePacketConstructor.newInstance(TIMES_ENUM, null, 10, 20, 20);

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
                Constructor<?> actionbarPacketConstructor = PACKET_PLAY_OUT_CHAT_CLASS.getConstructor(CHAT_BASE_COMPONENT_CLASS, CHAT_MESSAGE_TYPE_CLASS);

                packet = actionbarPacketConstructor.newInstance(createBaseComponent(text), CHAT_MESSAGE_TYPE_CLASS.getEnumConstants()[2]);
            } else {
                Constructor<?> actionbarPacketConstructor = PACKET_PLAY_OUT_CHAT_CLASS.getConstructor(CHAT_BASE_COMPONENT_CLASS, byte.class);

                packet = actionbarPacketConstructor.newInstance(createBaseComponent(text), (byte) 2);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return packet;
    }

    public static Object createBaseComponent(String text) {
        String resultText = text != null ? text : "";

        try {
            return CREATE_BASE_COMPONENT.invoke(null, StringUtils.replace("{\"text\": \"{TEXT}\"}", "{TEXT}", resultText));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
