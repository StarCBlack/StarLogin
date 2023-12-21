package com.github.starcblack.login.listeners;

import com.github.starcblack.login.StarLogin;
import com.github.starcblack.login.manager.LoginManager;
import com.github.starcblack.login.user.User;
import com.github.starcblack.login.user.dao.UserDao;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.permissions.PermissionAttachment;

public class PlayerListeners implements Listener {

    private final LoginManager loginManager = LoginManager.getInstance();
    private final String MESSAGE = "§cPara executar essa ação, você deve estar logado no servidor!";
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Adiciona o jogador à fila de autenticação ao entrar
        loginManager.addAuthenticationQueue(player);

        UserDao userDao = new UserDao();
        if (!userDao.isUserRegistered(player.getName())) {
            player.sendMessage("§aDigite o comando /register <senha> para se registrar no servidor");
        } else {
            player.sendMessage("§aDigite o comando /login <senha> para se logar no servidor");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        // Remove o jogador da fila de autenticação ao desconectar
        loginManager.removeAuthenticationQueue(player);
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



    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (loginManager.getAuthenticationQueue().contains(player)
                && (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getZ() != e.getTo().getZ())) {
            e.setCancelled(true);
        }
    }
}
