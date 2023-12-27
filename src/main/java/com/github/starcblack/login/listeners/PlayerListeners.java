package com.github.starcblack.login.listeners;

import com.github.starcblack.login.StarLogin;
import com.github.starcblack.login.manager.LoginManager;
import com.github.starcblack.login.misc.timer.LoginTimer;
import com.github.starcblack.login.user.dao.UserDao;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;

public class PlayerListeners implements Listener {

    private final LoginManager loginManager = LoginManager.getInstance();

    private final String MESSAGE = "§cPara executar essa ação, você deve estar logado no servidor!";
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Adiciona o jogador à fila de autenticação ao entrar
        loginManager.addAuthenticationQueue(player);

        LoginTimer loginTimer = new LoginTimer(loginManager, player, 60);
        loginTimer.start(StarLogin.getInstance());

        UserDao userDao = new UserDao();
        if (!userDao.isUserRegistered(player.getName())) {
            player.sendMessage("§aDigite o comando /register <senha> para se registrar no servidor");
            player.sendTitle("§c§lSTARLOGIN", "§7UTILIZE: /register <senha>");
        } else {
            player.sendMessage("§aDigite o comando /login <senha> para se logar no servidor");
            player.sendTitle("§c§lSTARLOGIN", "§7UTILIZE: /login <senha>");

        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        // Remove o jogador da fila de autenticação ao desconectar
        loginManager.removeAuthenticationQueue(player);
        //CaptchaManager.getCaptchaMap().remove(player.getUniqueId());
        if(loginManager.getAuthenticationQueue().isEmpty()) {
            System.out.println("Não existe nenhum usuário em processo de auteticação!");
        }
    }
    @EventHandler
    public void onPlayerPlaceBlockEvent(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (loginManager.getAuthenticationQueue().contains(player)) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlayerBreakBlockEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (loginManager.getAuthenticationQueue().contains(player)) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlayerDamageEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (loginManager.getAuthenticationQueue().contains(player)) {
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (loginManager.getAuthenticationQueue().contains(player)) {
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            if (loginManager.getAuthenticationQueue().contains(player)) {
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (loginManager.getAuthenticationQueue().contains(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (loginManager.getAuthenticationQueue().contains(player)) {
            event.setCancelled(true);
            player.sendMessage(MESSAGE);
        }
    }

    @EventHandler
    public void onCommandProcess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage().toLowerCase().trim();
        if(loginManager.getAuthenticationQueue().contains(player) && !message.startsWith("/login") && !message.startsWith("/register")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (loginManager.getAuthenticationQueue().contains(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (!loginManager.isUserAuthenticated(player)) {
            Location from = e.getFrom();
            Location to = e.getTo();
            if (from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ()) {
                e.setTo(from);
            }
        }
    }
    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent e) {
        Player player = e.getPlayer();

        if (loginManager.getAuthenticationQueue().contains(player)) {
            e.setCancelled(true);
            player.setAllowFlight(false);
            player.setFlying(false);
        }
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (loginManager.getAuthenticationQueue().contains(player)) {
            event.setCancelled(true);

        }

    }
}
