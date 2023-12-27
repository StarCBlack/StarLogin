package com.github.starcblack.login.misc.timer;

import com.github.starcblack.login.manager.LoginManager;
import com.github.starcblack.login.misc.utils.ActionBarAPI;
import com.github.starcblack.login.user.dao.UserDao;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class LoginTimer {

    private final int LOGIN_TIMEOUT;

    private final BukkitRunnable task;
    private final UserDao userDao;

    public LoginTimer(LoginManager loginManager, Player player, int loginTimeout) {
        LOGIN_TIMEOUT = loginTimeout;
        this.userDao = new UserDao();

        this.task = new BukkitRunnable() {
            private int secondsLeft = LOGIN_TIMEOUT;

            @Override
            public void run() {
                if (!loginManager.isUserAuthenticated(player)) {
                    if (userDao.isUserRegistered(player.getName())) {
                        ActionBarAPI.sendActionBar(player, "§c§lLOGIN: §cVocê tem §7[" + secondsLeft + "s] §cpara se logar!");
                    } else {
                        ActionBarAPI.sendActionBar(player, "§c§lREGISTRO: §cVocê tem §7[" + secondsLeft + "s] §cpara se registrar!");
                    }

                    secondsLeft--;

                    if (secondsLeft < 0) {
                        player.kickPlayer("§cSTARLOGIN\n\n§cTempo de login expirado. Por favor, tente novamente.");
                        loginManager.removeAuthenticationQueue(player);
                        cancel(); // Cancela a tarefa quando o tempo expira
                    }
                } else {
                    cancel(); // Cancela a tarefa se o jogador for autenticado antes do tempo expirar
                }
            }
        };
    }

    public void start(JavaPlugin plugin) {
        task.runTaskTimer(plugin, 0L, 20L); // Executa a tarefa a cada segundo (20 ticks)
    }

    public void cancel() {
        task.cancel();
    }
}
