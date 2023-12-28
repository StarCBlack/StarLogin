package com.github.starcblack.login.commands;

import com.github.starcblack.login.StarLogin;
import com.github.starcblack.login.manager.LoginManager;
import com.github.starcblack.login.misc.utils.ActionBarAPI;
import com.github.starcblack.login.user.dao.UserDao;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class LoginCommand implements CommandExecutor {

    private final LoginManager loginManager = LoginManager.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        UserDao userDao = new UserDao();

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', StarLogin.getInstance().getConfig().getString("messages.global.playerOnly")));
            return true;
        }

        Player player = (Player) sender;

        // Verifica se o jogador está na fila de autenticação
        if (loginManager.isUserAuthenticated(player)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', StarLogin.getInstance().getConfig().getString("messages.commands.loginCommand.alreadyAuthenticated")));
            return true;
        }

        // Verifica se o jogador já está registrado
        if (!userDao.isUserRegistered(player.getName())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', StarLogin.getInstance().getConfig().getString("messages.commands.loginCommand.notRegistered")));
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', StarLogin.getInstance().getConfig().getString("messages.commands.loginCommand.incorrectUsage")));
            return true;
        }

        String password = args[0];

        // Verifica se a senha está correta
        if (userDao.checkPassword(player.getName(), password)) {
            loginManager.removeAuthenticationQueue(player);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', StarLogin.getInstance().getConfig().getString("messages.commands.loginCommand.successfulLogin")));
            ActionBarAPI.sendActionBar(player, ChatColor.translateAlternateColorCodes('&', StarLogin.getInstance().getConfig().getString("messages.commands.loginCommand.successfulLogin")));
        } else {
            int attempts = loginManager.incrementLoginAttempts(player);
            int MAX_LOGIN_ATTEMPTS = 3;
            if (attempts >= MAX_LOGIN_ATTEMPTS) {
                player.kickPlayer(ChatColor.translateAlternateColorCodes('&', StarLogin.getInstance().getConfig().getString("messages.commands.loginCommand.maxAttemptsExceeded")));
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        StarLogin.getInstance().getConfig().getString("messages.commands.loginCommand.incorrectPassword")
                                .replace("%ATTEMPS%", String.valueOf(attempts))
                                .replace("%MAX_LOGIN_ATTEMPTS%", String.valueOf(MAX_LOGIN_ATTEMPTS))));
            }
            }
        return true;
    }
}
