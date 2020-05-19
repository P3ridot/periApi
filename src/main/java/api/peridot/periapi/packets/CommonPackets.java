package api.peridot.periapi.packets;

import org.apache.commons.lang.StringUtils;

public class CommonPackets {

    private static Reflection.MethodInvoker createBaseComponentMethod;

    static {
        try {
            createBaseComponentMethod = Reflection.serverVersion.equals("v1_8_R1") ? Reflection.getMethod(Reflection.getMinecraftClass("ChatSerializer"), "a", String.class) : Reflection.getMethod(Reflection.getMinecraftClass("IChatBaseComponent$ChatSerializer"), "a", String.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Object createBaseComponent(String text) {
        String resultText = text != null ? text : "";

        try {
            return createBaseComponentMethod.invoke(null, StringUtils.replace("{\"text\": \"{TEXT}\"}", "{TEXT}", resultText));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
