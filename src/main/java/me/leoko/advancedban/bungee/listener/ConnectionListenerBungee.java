package me.leoko.advancedban.bungee.listener;

import me.leoko.advancedban.Universal;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by Leoko @ dev.skamps.eu on 24.07.2016.
 */
public class ConnectionListenerBungee implements Listener {
    @EventHandler
    public void onConnection(PreLoginEvent e){
        String result = Universal.get().callConnection(e.getConnection().getName(), e.getConnection().getAddress().getAddress().getHostAddress());
        if(result != null){
            e.setCancelled(true);
            e.setCancelReason(result);
        }
    }
}
