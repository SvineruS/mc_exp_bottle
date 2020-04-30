package ua.svinerus.XpBottleStorage;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public final class Main extends JavaPlugin implements Listener {

    String SIGNATURE = "XP STORAGE";
    int XP_NUMBER = getConfig().getInt("xp_number");;

    ItemStack XP_PUT_IN_ITEM = new ItemStack(Material.GLASS_BOTTLE, 1);
    ItemStack XP_STORAGE_ITEM = get_item();


    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
    }



    @EventHandler
    public void onThrow(ExpBottleEvent e) {
        ItemStack item = e.getEntity().getItem();
        if (item.isSimilar(XP_STORAGE_ITEM))
            e.setExperience(XP_NUMBER);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getHand() == EquipmentSlot.OFF_HAND) return;  // only right hand
        ItemStack item = e.getItem();

        if (item == null || !item.isSimilar(XP_PUT_IN_ITEM)) return;  // only bottles
        if (e.getClickedBlock() != null && e.getClickedBlock().getRelative(e.getBlockFace()).getType() == Material.WATER) return;  // if click water ignore

        Player player = e.getPlayer();

        if (player.isSneaking()) {
            while (player.getInventory().containsAtLeast(XP_PUT_IN_ITEM, 1) && player.getTotalExperience() >= XP_NUMBER)
                xp_to_bottle(player);

        } else {
            if (player.getTotalExperience() < XP_NUMBER)
                player.sendMessage(String.format(
                        ChatColor.translateAlternateColorCodes('&', getConfig().getString("text_not_enough_exp")),
                        player.getTotalExperience(), XP_NUMBER - player.getTotalExperience()
                ));
            else
                xp_to_bottle(player);
        }

        e.setCancelled(true);
    }



    void xp_to_bottle(Player player) {
        player.getInventory().removeItem(XP_PUT_IN_ITEM);
        player.getInventory().addItem(XP_STORAGE_ITEM);
        player.giveExp(-XP_NUMBER);
    }
    

    ItemStack get_item() {
        ItemStack item = new ItemStack(Material.EXPERIENCE_BOTTLE, 1);
        item.setLore(Arrays.asList(SIGNATURE,
                String.format(ChatColor.translateAlternateColorCodes('&', getConfig().getString("text_lore")), XP_NUMBER)));
        return item;
    }
}
