package com.bilicraft.bilicraftbookupdate;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class BilicraftBookUpdate extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void playerJoinedGame(PlayerJoinEvent event) {
        showUpdate(event.getPlayer(),false);
    }

    private void showUpdate(Player player, boolean force) {
        ItemStack itemStack = getConfig().getItemStack("update");
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return;
        }
        if (!getConfig().getBoolean("record." + player.getUniqueId() + ".readed") || force) {
            getConfig().set("record." + player.getUniqueId() + ".readed", true);
            saveConfig();
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.openBook(itemStack);
                }
            }.runTaskLater(this, 10);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "该命令只能由玩家执行");
            return true;
        }
        Player player = (Player) sender;
        if(label.equalsIgnoreCase("publishbookupdate")){
            if (!sender.hasPermission("bilibookupdate.admin")) {
                sender.sendMessage(ChatColor.RED + "权限不足");
                return true;
            }
            ItemStack stack = player.getInventory().getItemInMainHand();
            if (stack.getType() == Material.AIR) {
                getConfig().set("update", null);
            } else {
                if (stack.getType() != Material.WRITTEN_BOOK) {
                    sender.sendMessage(ChatColor.YELLOW + "无效物品，请手持成书来发布更新，或空手取消发布更新");
                    return true;
                } else {
                    getConfig().set("update", stack);
                }
            }
            getConfig().set("record", null);
            saveConfig();
            sender.sendMessage(ChatColor.GREEN + "更新已发布!");
            Bukkit.getOnlinePlayers().forEach(p->showUpdate(p,false));
            return true;
        }
        if(label.equalsIgnoreCase("changelog")){
            showUpdate(player,true);
            return true;
        }
        return false;
    }

}
