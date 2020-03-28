package api.peridot.periapi.langapi;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.logging.Logger;

public class LangAPI {

    private final ConfigurationSection section;
    private final Logger logger;

    private Map<String, LangMessage> messages;

    public LangAPI(ConfigurationSection section) {
        this.section = section;
        this.logger = Bukkit.getLogger();
        reload();
    }

    public LangMessage getMessage(String id) {
        LangMessage message = messages.get(id);

        if (message == null) {
            message = new LangMessage(section.getConfigurationSection("id"));
            messages.put(id, message);
        }

        return message;
    }

    public void broadcast(String id, Replacement... replacements) {
        LangMessage message = getMessage(id);
        message.broadcast(replacements);
    }

    public void sendMessage(String id, Player player, Replacement... replacements) {
        LangMessage message = getMessage(id);
        message.send(player, replacements);
    }

    public void reload() {
        if (section == null) {
            logger.warning("[LangAPI] Missing messages section!");
            return;
        }

        if(!messages.isEmpty()) {
            messages.keySet().forEach(id -> {
                messages.put(id, getMessage(id));
            });
        }
    }
}
