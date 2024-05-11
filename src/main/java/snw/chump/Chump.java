package snw.chump;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.bukkit.Material.RED_DYE;

public final class Chump extends JavaPlugin implements Listener {
    public static final String TARGET = "Murasame_mao";
    private final Set<UUID> detected = new HashSet<>();
    private boolean red;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void interact(PlayerInteractEvent event) {
        switch (event.getAction()) {
            case RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK -> {
                if (event.getHand() == EquipmentSlot.HAND) {
                    final Player player = event.getPlayer();
                    if (!player.getName().equals(TARGET)) {
                        return;
                    }
                    final ItemStack itemInMainHand;
                    itemInMainHand = player.getInventory().getItemInMainHand();
                    final Material type = itemInMainHand.getType();
                    final Material newType;
                    switch (type) {
                        case RED_DYE -> {
                            red = false;
                            newType = Material.LIME_DYE;
                        }
                        case LIME_DYE -> {
                            red = true;
                            newType = RED_DYE;
                        }
                        default -> {
                            return;
                        }
                    }
                    itemInMainHand.setType(newType);
                    player.getInventory().setItemInMainHand(itemInMainHand);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final Location to = event.getTo();
        if (to == null)  {
            return;
        }
        final Location from = event.getFrom();
        if (Double.doubleToLongBits(from.getX()) == Double.doubleToLongBits(to.getX()) &&
            Double.doubleToLongBits(from.getY()) == Double.doubleToLongBits(to.getY()) &&
            Double.doubleToLongBits(from.getZ()) == Double.doubleToLongBits(to.getZ())) {
            return;
        }
        if (player.getScoreboardTags().contains("wanjia")) {
            if (red) {
                final UUID uuid = player.getUniqueId();
                if (!detected.contains(uuid)) {
                    final Player mao = Bukkit.getPlayerExact(TARGET);
                    if (mao != null) {
                        mao.sendMessage(ChatColor.RED +
                                event.getPlayer().getName() + "在移动");
                    }
                    detected.add(uuid);
                    getServer().getScheduler()
                            .runTaskLater(this, () -> {
                                detected.remove(uuid);
                            }, 10L);
                }
            }
        }
    }
}
