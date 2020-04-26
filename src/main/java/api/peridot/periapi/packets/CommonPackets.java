package api.peridot.periapi.packets;

import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Method;

public class CommonPackets {

    private static Method CREATE_BASE_COMPONENT;

    static {
        try {
            CREATE_BASE_COMPONENT = Reflections.server_version.equals("v1_8_R1") ? Reflections.getNMSClass("ChatSerializer").getMethod("a", String.class) : Reflections.getNMSClass("IChatBaseComponent$ChatSerializer").getMethod("a", String.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
