package me.leoko.advancedban.listener;

import me.leoko.advancedban.Universal;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

/**
 * Created by Leoko @ dev.skamps.eu on 16.07.2016.
 */
public class ConnectionListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onConnect(AsyncPlayerPreLoginEvent e){
        String result = Universal.get().callConnection(e.getName(), e.getAddress().getHostAddress());
        if(result != null) e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, result);
    }
}
