package com.survivorbob.bobmod;

import com.survivorbob.bobmod.Commands.HistoryCommand;
import com.survivorbob.bobmod.Commands.ModMenu;
import com.survivorbob.bobmod.GUIs.menus.historyMenu;
import com.survivorbob.bobmod.GUIs.menus.mainMenu;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Bobmod extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        PluginManager PM = Bukkit.getPluginManager();
        PM.registerEvents( new mainMenu(), this );
        PM.registerEvents( new historyMenu(), this );
        getCommand("bob-mod").setExecutor(new ModMenu());
        getCommand("bob-history").setExecutor(new HistoryCommand());
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
