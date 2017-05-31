package io.nanocode.hugs;

import io.nanocode.hugs.commands.HugCommand;
import net.minecraft.server.v1_11_R1.EnumParticle;
import net.minecraft.server.v1_11_R1.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin implements Listener {

    public int cooldown;
    public int item_id;

    public List<String> cooldowns = new ArrayList<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        cooldown = getConfig().getInt("cooldown");
        item_id = getConfig().getInt("item-id");

        getServer().getPluginManager().registerEvents(this, this);
        registerCommands();
    }

    @Override
    public void onDisable() {

    }

    public void registerCommands() {
        getCommand("huggun").setExecutor(new HugCommand(this));
    }

    @EventHandler
    public void onEntityInteractEvent(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (p.getInventory().getItemInMainHand().getType() == Material.getMaterial(item_id) && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if (cooldowns.contains(p.getUniqueId().toString())) {
                p.sendMessage(ChatColor.RED + "[" + ChatColor.BOLD + "!" + ChatColor.RESET + ChatColor.RED.toString() + "] " + "You must wait " + cooldown + " seconds before firing the hug gun!");
            } else {
                p.launchProjectile(Snowball.class);
                p.launchProjectile(Snowball.class);

                if (cooldown != 0) {
                    cooldowns.add(p.getUniqueId().toString());

                    getServer().getScheduler().runTaskLater(this, new Runnable() {
                        @Override
                        public void run() {
                            cooldowns.remove(p.getUniqueId().toString());
                        }
                    }, cooldown * 20);
                }
            }
        }
    }

    @EventHandler
    public void onProjectileHitEvent(ProjectileHitEvent event) {
        Projectile p = event.getEntity();

        if (p instanceof Snowball) {
            PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.HEART, true, p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ(), 1, 1, 1, 0, 5);

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (p.getLocation().distanceSquared(player.getLocation()) <= 30 * 30) {
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Snowball) {
            event.setCancelled(true);
        }
    }
}
