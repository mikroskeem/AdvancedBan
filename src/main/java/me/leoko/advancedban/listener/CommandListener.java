package me.leoko.advancedban.listener;

import me.leoko.advancedban.BukkitMain;
import me.leoko.advancedban.Universal;
import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.manager.UUIDManager;
import me.leoko.advancedban.utils.Punishment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Created by Leoko @ dev.skamps.eu on 16.07.2016.
 */
public class CommandListener implements Listener{

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e){
        if(Universal.get().getMethods().callCMD(e.getPlayer(), e.getMessage())) e.setCancelled(true);
    }
}
