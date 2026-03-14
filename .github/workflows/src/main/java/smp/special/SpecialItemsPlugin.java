package smp.special;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class SpecialItemsPlugin extends JavaPlugin implements Listener {

private NamespacedKey specialKey;  
private NamespacedKey ownerKey;  

Random random = new Random();  

Material[] items = {  
        Material.IRON_SWORD,  
        Material.IRON_AXE,  
        Material.BOW,  
        Material.CROSSBOW,  
        Material.IRON_HELMET,  
        Material.IRON_CHESTPLATE,  
        Material.IRON_LEGGINGS,  
        Material.IRON_BOOTS,  
        Material.IRON_PICKAXE,  
        Material.IRON_SHOVEL  
};  

@Override  
public void onEnable() {  

    specialKey = new NamespacedKey(this, "special_item");  
    ownerKey = new NamespacedKey(this, "weapon_owner");  

    Bukkit.getPluginManager().registerEvents(this, this);  
}  

@EventHandler  
public void onFirstJoin(PlayerJoinEvent e) {  

    Player p = e.getPlayer();  

    if (!p.hasPlayedBefore()) {  

        Material mat = items[random.nextInt(items.length)];  
        ItemStack item = new ItemStack(mat);  

        ItemMeta meta = item.getItemMeta();  

        meta.setDisplayName("§a" + p.getName() + "'s Weapon");  

        PersistentDataContainer data = meta.getPersistentDataContainer();  

        data.set(specialKey, PersistentDataType.INTEGER, 1);  
        data.set(ownerKey, PersistentDataType.STRING, p.getUniqueId().toString());  

        item.setItemMeta(meta);  

        item.addUnsafeEnchantment(Enchantment.UNBREAKING, 255);  

        if (mat == Material.BOW || mat == Material.CROSSBOW)  
            item.addUnsafeEnchantment(Enchantment.POWER, 3);  

        else if (mat.toString().contains("HELMET") ||  
                mat.toString().contains("CHESTPLATE") ||  
                mat.toString().contains("LEGGINGS") ||  
                mat.toString().contains("BOOTS"))  

            item.addUnsafeEnchantment(Enchantment.PROTECTION, 3);  

        else  
            item.addUnsafeEnchantment(Enchantment.SHARPNESS, 3);  

        p.getInventory().addItem(item);  

        p.sendMessage("§aYou received your Special Item!");  
    }  
}  

@EventHandler  
public void onKill(EntityDeathEvent e) {  

    if (!(e.getEntity().getKiller() instanceof Player)) return;  

    Player killer = e.getEntity().getKiller();  

    for (ItemStack item : killer.getInventory()) {  

        if (item == null) continue;  

        ItemMeta meta = item.getItemMeta();  

        if (meta == null) continue;  

        if (!meta.getPersistentDataContainer().has(specialKey, PersistentDataType.INTEGER))  
            continue;  

        if (item.containsEnchantment(Enchantment.SHARPNESS)) {  

            int level = item.getEnchantmentLevel(Enchantment.SHARPNESS);  

            if (level < 10)  
                item.addUnsafeEnchantment(Enchantment.SHARPNESS, level + 1);  
        }  

        if (item.containsEnchantment(Enchantment.POWER)) {  

            int level = item.getEnchantmentLevel(Enchantment.POWER);  

            if (level < 10)  
                item.addUnsafeEnchantment(Enchantment.POWER, level + 1);  
        }  

        killer.sendMessage("§aYour weapon grew stronger!");  
    }  
}  

@EventHandler  
public void onDeath(PlayerDeathEvent e) {  

    Player p = e.getEntity();  

    for (ItemStack item : p.getInventory()) {  

        if (item == null) continue;  

        ItemMeta meta = item.getItemMeta();  

        if (meta == null) continue;  

        if (!meta.getPersistentDataContainer().has(specialKey, PersistentDataType.INTEGER))  
            continue;  

        e.getDrops().remove(item);  

        if (item.containsEnchantment(Enchantment.SHARPNESS)) {  

            int level = item.getEnchantmentLevel(Enchantment.SHARPNESS);  

            if (level > 1)  
                item.addUnsafeEnchantment(Enchantment.SHARPNESS, level - 1);  
        }  

        if (item.containsEnchantment(Enchantment.POWER)) {  

            int level = item.getEnchantmentLevel(Enchantment.POWER);  

            if (level > 1)  
                item.addUnsafeEnchantment(Enchantment.POWER, level - 1);  
        }  

        Bukkit.getScheduler().runTaskLater(this, () -> {  
            p.getInventory().addItem(item);  
        }, 1L);  

        p.sendMessage("§cYour weapon lost power!");  
    }  
}

                            }
