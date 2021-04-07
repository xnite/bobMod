package com.survivorbob.bobmod.GUIs.history;

import litebans.api.Database;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class warnHistory {
    public void getWarns(Player player, OfflinePlayer targetPlayer)
    {
        Inventory theHistoryInv = Bukkit.createInventory(null, 45, "Warning History for " + targetPlayer.getName().toString());
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta playerHeadMeta = (SkullMeta) playerHead.getItemMeta();
        playerHeadMeta.setDisplayName(ChatColor.YELLOW+"Moderating: " + ChatColor.RESET + targetPlayer.getName());
        playerHeadMeta.setOwningPlayer(Bukkit.getOfflinePlayer(targetPlayer.getUniqueId()));
        playerHead.setItemMeta(playerHeadMeta);
        theHistoryInv.addItem(playerHead);

        Thread newThread = new Thread(() -> {
            String uuid = targetPlayer.getUniqueId().toString();
            String query = "SELECT * FROM {warnings} WHERE uuid=? ORDER BY `id` DESC LIMIT 42";
            try (PreparedStatement st = Database.get().prepareStatement(query)) {
                st.setString(1, uuid);
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        String reason = rs.getString("reason");
                        String warnedByUuid = rs.getString("banned_by_uuid");
                        long time = rs.getLong("time");
                        long until = rs.getLong("until");
                        long id = rs.getLong("id");
                        ItemStack newItem = new ItemStack(Material.BOOK, 1);
                        ItemMeta newMeta = newItem.getItemMeta();
                        newMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        List<String> theLore = new ArrayList<String>();
                        newMeta.setDisplayName(ChatColor.YELLOW + "Warning ID: " + ChatColor.RESET + id);
                        theLore.add(ChatColor.YELLOW + "Reason: " + ChatColor.RESET + reason);
                        newMeta.setLore(theLore);
                        newItem.setItemMeta(newMeta);
                        theHistoryInv.addItem(newItem);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return;
        });
        newThread.start();
        player.openInventory(theHistoryInv);
    }
}
