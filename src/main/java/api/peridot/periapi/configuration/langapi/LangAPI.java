package api.peridot.periapi.configuration.langapi;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class LangAPI {

    private final ConfigurationSection section;
    private final Logger logger;

    private final Map<String, LangMessage> messages = new ConcurrentHashMap<>();
    private final Map<String, SimpleLangMessage> simpleMessages = new ConcurrentHashMap<>();

    public LangAPI(ConfigurationSection section) {
        this.section = section;
        this.logger = Bukkit.getLogger();
        reload();
    }

    /* Getting or Creating messages */

    public LangMessage getMessage(String id) {
        LangMessage message = messages.get(id);

        if (message == null) {
            message = new LangMessage(section.getConfigurationSection(id));
            messages.put(id, message);
        }

        return message;
    }

    public SimpleLangMessage getSimpleMessage(String id) {
        SimpleLangMessage message = simpleMessages.get(id);

        if (message == null) {
            message = new SimpleLangMessage(section.getConfigurationSection(id));
            simpleMessages.put(id, message);
        }

        return message;
    }

    /* Sending */

    public void broadcast(String id, Replacement... replacements) {
        LangMessage message = getMessage(id);
        message.broadcast(replacements);
    }

    public void sendMessage(CommandSender sender, String id, Replacement... replacements) {
        LangMessage message = getMessage(id);
        message.send(sender, replacements);
    }

    public void broadcastSimple(String id, Replacement... replacements) {
        SimpleLangMessage message = getSimpleMessage(id);
        message.broadcast(replacements);
    }

    public void sendSimpleMessage(CommandSender sender, String id, Replacement... replacements) {
        SimpleLangMessage message = getSimpleMessage(id);
        message.send(sender, replacements);
    }

    /* Reload */

    public void reload() {
        if (section == null) {
            logger.warning("[LangAPI] Missing messages section!");
            return;
        }

        if (!messages.isEmpty()) {
            messages.keySet().forEach(id -> {
                messages.put(id, getMessage(id));
            });
        }

        if (!simpleMessages.isEmpty()) {
            simpleMessages.keySet().forEach(id -> {
                simpleMessages.put(id, getSimpleMessage(id));
            });
        }
    }
}
