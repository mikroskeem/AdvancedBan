package me.leoko.advancedban;

import me.leoko.advancedban.listener.CommandReceiver;
import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.manager.UUIDManager;
import me.leoko.advancedban.utils.Punishment;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.UUID;

/**
 * Created by Leoko @ dev.skamps.eu on 23.07.2016.
 */
public class BukkitMethods implements MethodInterface {
    private YamlConfiguration data;
    private File dataFile = new File(getDataFolder(), "data.yml");

    private YamlConfiguration config;
    private File configFile = new File(getDataFolder(), "config.yml");

    private YamlConfiguration messages;
    private File messageFile = new File(getDataFolder(), "Messages.yml");

    private YamlConfiguration layouts;
    private File layoutFile = new File(getDataFolder(), "Layouts.yml");

    private YamlConfiguration mysql;

    @Override
    public void loadFiles() {
        if(!configFile.exists()) ((JavaPlugin) getPlugin()).saveResource("config.yml", true);
        if(!messageFile.exists()) ((JavaPlugin) getPlugin()).saveResource("Messages.yml", true);
        if(!layoutFile.exists()) ((JavaPlugin) getPlugin()).saveResource("Layouts.yml", true);

        config = YamlConfiguration.loadConfiguration(configFile);
        messages = YamlConfiguration.loadConfiguration(messageFile);
        layouts = YamlConfiguration.loadConfiguration(layoutFile);

        if(!config.contains("UUID-Fetcher")){
            configFile.renameTo(new File(getDataFolder(), "oldConfig.yml"));
            configFile = new File(getDataFolder(), "config.yml");
            ((JavaPlugin) getPlugin()).saveResource("config.yml", true);
            config = YamlConfiguration.loadConfiguration(configFile);
        }

        if(!dataFile.exists()) try {
            dataFile.createNewFile();
        } catch (IOException e) { e.printStackTrace(); }
        data = YamlConfiguration.loadConfiguration(dataFile);
    }

    @Override
    public String getFromURL_JSON(String url, String key) {
        try{
            HttpURLConnection request = (HttpURLConnection) new URL(url).openConnection();
            request.connect();

            JSONParser jp = new JSONParser();
            JSONObject json = (JSONObject) jp.parse(new InputStreamReader(request.getInputStream()));

            return json.get(key).toString();

        }catch(Exception exc){
            return null;
        }
    }

    @Override
    public String getVersion() {
        return ((JavaPlugin)getPlugin()).getDescription().getVersion();
    }

    @Override
    public String[] getKeys(Object file, String path) {
        String[] ss = new String[0];
        return (String[]) ((YamlConfiguration) file).getConfigurationSection(path).getKeys(false).toArray(ss);
    }

    @Override
    public Object getConfig() {
        return config;
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public Object getMessages() {
        return messages;
    }

    @Override
    public Object getLayouts() {
        return layouts;
    }

    @Override
    public void saveData() {
        try {
            data.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getPlugin() {
        return BukkitMain.get();
    }

    @Override
    public File getDataFolder() {
        return ((JavaPlugin)getPlugin()).getDataFolder();
    }

    @Override
    public void setCommandExecutor(String cmd) {
        Bukkit.getPluginCommand(cmd).setExecutor(CommandReceiver.get());
    }

    @Override
    public void sendMessage(Object player, String msg){
        ((CommandSender) player).sendMessage(msg);
    }

    @Override
    public boolean hasPerms(Object player, String perms){
        return ((CommandSender) player).hasPermission(perms);
    }

    @Override
    public boolean isOnline(String name) {
        return Bukkit.getOfflinePlayer(name).isOnline();
    }

    @Override
    public Object getPlayer(String name) {
        return Bukkit.getPlayer(name);
    }

    @Override
    public void kickPlayer(Object player, String reason) {
        ((Player) player).kickPlayer(reason);
    }

    @Override
    public Object[] getOnlinePlayers() {
        return Bukkit.getOnlinePlayers().toArray();
    }

    @Override
    public void scheduleAsyncRep(Runnable rn, long l1, long l2) {
        Bukkit.getScheduler().scheduleAsyncRepeatingTask((JavaPlugin) getPlugin(), rn, l1, l2);
    }

    @Override
    public void scheduleAsync(Runnable rn, long l1) {
        Bukkit.getScheduler().scheduleAsyncDelayedTask((JavaPlugin) getPlugin(), rn, l1);
    }

    @Override
    public void runAsync(Runnable rn) {
        Bukkit.getScheduler().runTaskAsynchronously((JavaPlugin) getPlugin(), rn);
    }

    @Override
    public void runSync(Runnable rn) {
        Bukkit.getScheduler().runTask((JavaPlugin) getPlugin(), rn);
    }

    @Override
    public void executeCommand(String cmd) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
    }

    @Override
    public String getName(Object player){
        return ((CommandSender) player).getName();
    }

    @Override
    public String getName(String uuid){
        return Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
    }

    @Override
    public String getInternUUID(Object player) {
        return player instanceof OfflinePlayer ? ((OfflinePlayer) player).getUniqueId().toString().replaceAll("-", "") : "none";
    }

    @Override
    public String getInternUUID(String player) {
        return Bukkit.getOfflinePlayer(player).getUniqueId().toString().replaceAll("-", "");
    }

    @Override
    public boolean callChat(Object player) {
        Punishment pnt = PunishmentManager.get().getMute(UUIDManager.get().getUUID(getName(player)));
        if(pnt != null){
            for(String str : pnt.getLayout()) sendMessage(player, str);
            return true;
        }
        return false;
    }

    @Override
    public boolean callCMD(Object player, String cmd) {
        Punishment pnt;
        if(Universal.get().isMuteCommand(cmd.split(" ")[0].substring(1)) && (pnt = PunishmentManager.get().getMute(UUIDManager.get().getUUID(getName(player)))) != null){
            for(String str : pnt.getLayout()) sendMessage(player, str);
            return true;
        }
        return false;
    }

    @Override
    public void loadMySQLFile(File f) {
        mysql = YamlConfiguration.loadConfiguration(f);
    }

    @Override
    public void createMySQLFile(File f) {
        mysql.set("MySQL.IP", "localhost");
        mysql.set("MySQL.DB-Name", "YourDatabase");
        mysql.set("MySQL.Username", "root");
        mysql.set("MySQL.Password", "pw123");
        mysql.set("MySQL.Port", 3306);
        try { mysql.save(f); } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public Object getMySQLFile(){
        return mysql;
    }

    @Override
    public String parseJSON(InputStreamReader json, String key) {
        try {
            return ((JSONObject) new JSONParser().parse(json)).get(key).toString();
        } catch (ParseException e) {
            System.out.println("Error -> "+e.getMessage());
            return null;
        } catch (IOException e) {
            System.out.println("Error -> "+e.getMessage());
            return null;
        }
    }

    @Override
    public String parseJSON(String json, String key) {
        try {
            return ((JSONObject) new JSONParser().parse(json)).get(key).toString();
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public Boolean getBoolean(Object file, String path) {
        return ((YamlConfiguration)file).getBoolean(path);
    }

    @Override
    public String getString(Object file, String path) {
        return ((YamlConfiguration)file).getString(path);
    }

    @Override
    public Long getLong(Object file, String path) {
        return ((YamlConfiguration)file).getLong(path);
    }

    @Override
    public Integer getInteger(Object file, String path) {
        return ((YamlConfiguration)file).getInt(path);
    }

    @Override
    public List<String> getStringList(Object file, String path){
        return ((YamlConfiguration)file).getStringList(path);
    }

    @Override
    public boolean getBoolean(Object file, String path, boolean def) {
        return ((YamlConfiguration)file).getBoolean(path, def);
    }

    @Override
    public String getString(Object file, String path, String def) {
        return ((YamlConfiguration)file).getString(path, def);
    }

    @Override
    public long getLong(Object file, String path, long def) {
        return ((YamlConfiguration)file).getLong(path, def);
    }

    @Override
    public int getInteger(Object file, String path, int def) {
        return ((YamlConfiguration)file).getInt(path, def);
    }

    @Override
    public void set(Object file, String path, Object value) {
        ((YamlConfiguration)file).set(path, value);
    }

    @Override
    public boolean contains(Object file, String path){
        return ((YamlConfiguration)file).contains(path);
    }

    @Override
    public String getFileName(Object file){
        return ((YamlConfiguration)file).getName();
    }
}
