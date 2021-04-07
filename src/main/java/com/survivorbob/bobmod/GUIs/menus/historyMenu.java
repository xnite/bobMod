package com.survivorbob.bobmod.GUIs.menus;

import com.survivorbob.bobmod.GUIs.history.banHistory;
import com.survivorbob.bobmod.GUIs.history.muteHistory;
import com.survivorbob.bobmod.GUIs.history.warnHistory;
import litebans.api.Database;
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class historyMenu implements Listener {
    public void getHistory(Player player, String targetPlayerName)
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

            ItemStack viewWarnHistory = new ItemStack(Material.WRITTEN_BOOK, 1);
            ItemMeta viewWarnHistoryMeta = viewWarnHistory.getItemMeta();
            viewWarnHistoryMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            viewWarnHistoryMeta.setDisplayName(ChatColor.YELLOW + "View Warning History" + ChatColor.RESET);
            viewWarnHistory.setItemMeta(viewWarnHistoryMeta);


            ItemStack viewBanHistory = new ItemStack(Material.IRON_AXE, 1);
            ItemMeta viewBanHistoryMeta = viewBanHistory.getItemMeta();
            viewBanHistoryMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            viewBanHistoryMeta.setDisplayName(ChatColor.RED + "View Ban History" + ChatColor.RESET);
            viewBanHistory.setItemMeta(viewBanHistoryMeta);

            ItemStack viewMuteHistory = new ItemStack(Material.WRITTEN_BOOK, 1);
            ItemMeta viewMuteHistoryMeta = viewMuteHistory.getItemMeta();
            viewMuteHistoryMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            viewMuteHistoryMeta.setDisplayName(ChatColor.BLUE + "View Mute History" + ChatColor.RESET);
            viewMuteHistory.setItemMeta(viewMuteHistoryMeta);

            newInventory.addItem(playerHead);
            newInventory.addItem(viewWarnHistory);
            newInventory.addItem(viewBanHistory);
            newInventory.addItem(viewMuteHistory);
        });
        newThread.start();
        player.openInventory(newInventory);
    }

    @EventHandler
    public void menuClickEvent(InventoryClickEvent event)
    {
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
            new mainMenu().getMain(player, targetPlayer.getName().toString());
            event.setCancelled(true);
            return;
        }
        if(theItem.getItemMeta().getDisplayName().contains("View Ban History")) {
            new banHistory().getBans(player, targetPlayer);
            event.setCancelled(true);
            return;
        }

        if(theItem.getItemMeta().getDisplayName().contains("View Warning History")) {
            new warnHistory().getWarns(player, targetPlayer);
            event.setCancelled(true);
            return;
        }
        if(theItem.getItemMeta().getDisplayName().contains("View Mute History")) {
            new muteHistory().getMutes(player, targetPlayer);
            event.setCancelled(true);
            return;
        }
        event.setCancelled(true);
        return;
    }
}
