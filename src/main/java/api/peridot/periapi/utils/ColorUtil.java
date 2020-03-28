package api.peridot.periapi.utils;

import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ColorUtil {

    public static String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static List<String> color(List<String> list) {
        return list.stream()
                .map(ColorUtil::color).collect(Collectors.toList());
    }

    public static List<String> color(String... strings) {
        return color(Arrays.asList(strings));
    }

}
