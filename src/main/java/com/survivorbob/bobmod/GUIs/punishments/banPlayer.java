package com.survivorbob.bobmod.GUIs.punishments;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class banPlayer {
    public void banPlayerGUI(Player player, OfflinePlayer targetPlayer)
    {
        Inventory banPlayerInv = Bukkit.createInventory(null, 18, ChatColor.RED + "Ban " + targetPlayer.getName().toString());
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta playerHeadMeta = (SkullMeta) playerHead.getItemMeta();
        playerHeadMeta.setDisplayName(ChatColor.YELLOW+"Moderating: " + ChatColor.RESET + targetPlayer.getName());
        playerHeadMeta.setOwningPlayer(Bukkit.getOfflinePlayer(targetPlayer.getUniqueId()));
        playerHead.setItemMeta(playerHeadMeta);
        banPlayerInv.addItem(playerHead);
        Configuration theConfig = Bukkit.getPluginManager().getPlugin("BobMod").getConfig();
        List<String> BanReasons = theConfig.getStringList("banreasons");
        for(String BanReason : BanReasons)
        {
            if(theConfig.get("banpunishments." + BanReason) != null) {
                ItemStack item = new ItemStack(Material.DIAMOND_AXE, 1);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.RED + "Ban for " + BanReason);
                List<String> lore = new ArrayList<String>();
                lore.add(ChatColor.YELLOW + "Description: " + ChatColor.RESET + theConfig.getString("banpunishments." + BanReason + ".desc"));
                lore.add(ChatColor.YELLOW + "Duration: " + ChatColor.RESET + theConfig.getString("banpunishments." + BanReason + ".time"));
                meta.setLore(lore);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                item.setItemMeta(meta);
                banPlayerInv.addItem(item);
            }
        }
        player.openInventory(banPlayerInv);
    }
}
