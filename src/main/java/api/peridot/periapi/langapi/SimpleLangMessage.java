package api.peridot.periapi.langapi;

import api.peridot.periapi.utils.ColorUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleLangMessage {

    private boolean useChat = false;
    private List<String> chatContent = new ArrayList<>();

    public SimpleLangMessage(ConfigurationSection section) {
        try {
            useChat = section.get("chat.content") != null;
            if (section.isString("chat.content")) {
                chatContent.add(ColorUtil.color(section.getString("chat.content")));
            } else if (section.isList("chat.content")) {
                chatContent = ColorUtil.color(section.getStringList("chat.content"));
            }
        } catch (Exception ignored) { }
    }

    public void broadcast(Replacement... replacements) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            send(player, replacements);
        }
    }

    public void send(CommandSender sender, Replacement... replacements) {
        if (useChat) {
            getChatContent(replacements).forEach(sender::sendMessage);
        }
    }

    public boolean useChat() {
        return useChat;
    }

    public List<String> getChatContent(Replacement... replacements) {
        return chatContent.stream().map(s -> replace(s, replacements)).collect(Collectors.toList());
    }

    public String replace(String msg, Replacement... replacements) {
        String toReturn = msg;
        for (Replacement r : replacements) {
            toReturn = StringUtils.replace(toReturn, r.getFrom(), r.getTo());
        }
        return toReturn;
    }

    public String getStringLine() {
        if(chatContent.size() == 1) return chatContent.get(0);
        return chatContent.toString();
    }
}
