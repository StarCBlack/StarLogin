package com.github.starcblack.login.commands;

import com.github.starcblack.login.StarLogin;
import com.github.starcblack.login.manager.LoginManager;
import com.github.starcblack.login.user.dao.UserDao;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.mindrot.jbcrypt.BCrypt;

public class ChangePasswordCommand implements CommandExecutor {

    private final UserDao userDao = new UserDao();
    private final LoginManager loginManager = new LoginManager();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', StarLogin.getInstance().getConfig().getString("messages.global.playerOnly")));
            return true;
        }
        Player player = (Player) sender;

        // Verifica se o jogador está registrado
        if (!userDao.isUserRegistered(player.getName())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', StarLogin.getInstance().getConfig().getString("messages.commands.changePasswordCommand.notRegistered")));
            return true;
        }
        // Verifica se o jogador está fora da fila de login
        if (loginManager.getAuthenticationQueue().contains(player)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', StarLogin.getInstance().getConfig().getString("messages.commands.changePasswordCommand.notAuthenticated")));
            return true;
        }
        // Verifica se o comando foi usado corretamente
        if (args.length != 1) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', StarLogin.getInstance().getConfig().getString("messages.commands.changePasswordCommand.incorrectUsage")));
            return true;
        }

        String newPassword = args[0];
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

        // Atualiza a senha no banco de dados
        userDao.updatePassword(player.getName(), hashedPassword);

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', StarLogin.getInstance().getConfig().getString("messages.commands.changePasswordCommand.passwordChanged")));

        return true;
    }
}
