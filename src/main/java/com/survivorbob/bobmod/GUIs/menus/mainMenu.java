package com.survivorbob.bobmod.GUIs.menus;
import com.survivorbob.bobmod.GUIs.punishments.banPlayer;
import com.survivorbob.bobmod.GUIs.punishments.mutePlayer;
import com.survivorbob.bobmod.GUIs.punishments.warnPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import litebans.api.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Logger;


public class mainMenu implements Listener {
    public void getMain(Player player, String targetPlayerName)
    {
        Inventory newInventory = Bukkit.createInventory(null, 9, ChatColor.GOLD + "Moderating: " + targetPlayerName.toString().toUpperCase());
        Thread newThread = new Thread(() -> {
            String username = targetPlayerName;
            String query = "SELECT uuid FROM {history} WHERE name=? ORDER BY date DESC LIMIT 1";
            OfflinePlayer targetPlayer = null;
            try (PreparedStatement st = Database.get().prepareStatement(query)) {
                st.setString(1, username);
                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) {
                        String playerUUID = rs.getString("uuid");
                        targetPlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));
                    } else {
                        player.sendMessage(ChatColor.RED + "Could not find that player!");
                        return;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                player.sendMessage(ChatColor.RED + "Could not find that player!");
                return;
            }

            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta playerHeadMeta = (SkullMeta) playerHead.getItemMeta();
            playerHeadMeta.setOwningPlayer(Bukkit.getOfflinePlayer(targetPlayer.getUniqueId()));
            playerHeadMeta.setDisplayName(ChatColor.YELLOW+"Moderating: " + ChatColor.RESET + targetPlayer.getName());
            playerHead.setItemMeta(playerHeadMeta);

            ItemStack viewWarnHistory = new ItemStack(Material.CLOCK, 1);
            ItemMeta viewWarnHistoryMeta = viewWarnHistory.getItemMeta();
            viewWarnHistoryMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            viewWarnHistoryMeta.setDisplayName(ChatColor.YELLOW + "View History" + ChatColor.RESET);
            viewWarnHistory.setItemMeta(viewWarnHistoryMeta);

            ItemStack banPlayer = new ItemStack(Material.DIAMOND_AXE, 1);
            ItemMeta banPlayerMeta = banPlayer.getItemMeta();
            banPlayerMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            banPlayerMeta.setDisplayName(ChatColor.RED + "Ban Player" + ChatColor.RESET);
            banPlayer.setItemMeta(banPlayerMeta);

            ItemStack warnPlayer = new ItemStack(Material.WRITABLE_BOOK, 1);
            ItemMeta warnPlayerMeta = warnPlayer.getItemMeta();
            warnPlayerMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            warnPlayerMeta.setDisplayName(ChatColor.YELLOW + "Warn Player" + ChatColor.RESET);
            warnPlayer.setItemMeta(warnPlayerMeta);

            ItemStack mutePlayer = new ItemStack(Material.NOTE_BLOCK, 1);
            ItemMeta mutePlayerMeta = mutePlayer.getItemMeta();
            mutePlayerMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            mutePlayerMeta.setDisplayName(ChatColor.BLUE + "Mute Player" + ChatColor.RESET);
            mutePlayer.setItemMeta(mutePlayerMeta);

            newInventory.addItem(playerHead);

            if(player.hasPermission("bobmod.gui.history")) {
                newInventory.addItem(viewWarnHistory);
            }
            if(player.hasPermission("bobmod.gui.warn")) {
                newInventory.addItem(warnPlayer);
            }
            if(player.hasPermission("bobmod.gui.mute")) {
                newInventory.addItem(mutePlayer);
            }
            if(player.hasPermission("bobmod.gui.ban")) {
                newInventory.addItem(banPlayer);
            }
        });
        newThread.start();
        player.openInventory(newInventory);
    }

    @EventHandler
    public void menuClickEvent(InventoryClickEvent event)
    {
        Logger logger = Bukkit.getServer().getLogger();
        Inventory clickedInventory = event.getClickedInventory();
        if(!clickedInventory.getItem(0).getItemMeta().getDisplayName().contains(ChatColor.YELLOW + "Moderating: " + ChatColor.RESET))
        {
            // not our menu!
            return;
        }
        SkullMeta targetPlayerHead = (SkullMeta) clickedInventory.getItem(0).getItemMeta();
        OfflinePlayer targetPlayer = (OfflinePlayer) targetPlayerHead.getOwningPlayer();
        Player player = (Player) event.getWhoClicked();
        ItemStack theItem = event.getCurrentItem();
        if(theItem.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Moderating: " + ChatColor.RESET + targetPlayer.getName()))
        {
            this.getMain(player, targetPlayer.getName().toString());
            event.setCancelled(true);
            return;
        }
        if(theItem.getItemMeta().getDisplayName().contains("View History")) {
            logger.info(player.getName() + " is checking moderation history for player: " + targetPlayer.getName());
            new historyMenu().getHistory(player, targetPlayer.getName().toString());
            event.setCancelled(true);
            return;
        }

        if(theItem.getItemMeta().getDisplayName().contains("Ban Player")) {
            new banPlayer().banPlayerGUI(player, targetPlayer);
            event.setCancelled(true);
            return;
        }

        if(theItem.getItemMeta().getDisplayName().contains("Warn Player"))
        {
            new warnPlayer().warnPlayerGUI(player, targetPlayer);
            event.setCancelled(true);
            return;
        }

        if(theItem.getItemMeta().getDisplayName().contains("Mute Player"))
        {
            new mutePlayer().mutePlayerGUI(player, targetPlayer);
            event.setCancelled(true);
            return;
        }

        Configuration theConfig = Bukkit.getPluginManager().getPlugin("BobMod").getConfig();
        for( String BanReason : theConfig.getStringList("banreasons"))
        {
            if(theItem.getItemMeta().getDisplayName().contains("Ban for " + BanReason))
            {
                String banReason = theConfig.getString("banpunishments." + BanReason + ".reason");
                String banTime = theConfig.getString("banpunishments." + BanReason + ".time");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban --sender-uuid=" + player.getUniqueId().toString() + " " + targetPlayer.getName() + " " + banTime + " " + banReason);
                player.closeInventory();
                event.setCancelled(true);
            }

            if(theItem.getItemMeta().getDisplayName().contains("Warn for " + BanReason))
            {
                String banReason = theConfig.getString("banpunishments." + BanReason + ".reason");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "warn --sender-uuid=" + player.getUniqueId().toString() + " " + targetPlayer.getName() + " " + banReason);
                player.closeInventory();
                event.setCancelled(true);
            }

            if(theItem.getItemMeta().getDisplayName().contains("Mute for " + BanReason))
            {
                String banReason = theConfig.getString("mutepunishments." + BanReason + ".reason");
                String banTime = theConfig.getString("mutepunishments." + BanReason + ".time");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mute --sender-uuid=" + player.getUniqueId().toString() + " " + banTime + " " + targetPlayer.getName() + " " + banReason);
                player.closeInventory();
                event.setCancelled(true);
            }

        }

        event.setCancelled(true);
        return;
    }
}
