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

public class banHistory implements Listener {
    public void getBans(Player player, OfflinePlayer targetPlayer)
    {
        Inventory theHistoryInv = Bukkit.createInventory(null, 45, "Ban History for " + targetPlayer.getName().toString());
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta playerHeadMeta = (SkullMeta) playerHead.getItemMeta();
        playerHeadMeta.setDisplayName(ChatColor.YELLOW+"Moderating: " + ChatColor.RESET + targetPlayer.getName());
        playerHeadMeta.setOwningPlayer(Bukkit.getOfflinePlayer(targetPlayer.getUniqueId()));
        playerHead.setItemMeta(playerHeadMeta);
        theHistoryInv.addItem(playerHead);

        Thread newThread = new Thread(() -> {
            String uuid = targetPlayer.getUniqueId().toString();
            String query = "SELECT * FROM {bans} WHERE uuid=? ORDER BY `id` DESC LIMIT 42";
            try (PreparedStatement st = Database.get().prepareStatement(query)) {
                st.setString(1, uuid);
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        String reason = rs.getString("reason");
                        String bannedByUuid = rs.getString("banned_by_uuid");
                        long time = rs.getLong("time");
                        long until = rs.getLong("until");
                        long id = rs.getLong("id");
                        boolean active = rs.getBoolean("active");
                        ItemStack newItem = new ItemStack(Material.COAL, 1);
                        ItemMeta newMeta = newItem.getItemMeta();
                        newMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        List<String> theLore = new ArrayList<String>();
                        newMeta.setDisplayName(ChatColor.YELLOW + "Ban ID: " + ChatColor.RESET + id);
                        if(active) {
                            theLore.add(ChatColor.RED + "Currently active ban");
                        } else {
                            theLore.add(ChatColor.GREEN+"Ban is inactive");
                        }

                        for( String line : reason.split("\n") ) {
                            if (line.length() >= 70) {
                                theLore.add(ChatColor.YELLOW + "Reason: " + ChatColor.RESET + line.substring(0, 69)); //cap at 70 chars as to not run off the screen.
                            } else {
                                theLore.add(ChatColor.YELLOW + "Reason: " + ChatColor.RESET + line);
                            }
                        }
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
