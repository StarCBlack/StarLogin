package com.github.starcblack.login.commands;

import com.github.starcblack.login.StarLogin;
import com.github.starcblack.login.manager.LoginManager;
import com.github.starcblack.login.misc.utils.ActionBarAPI;
import com.github.starcblack.login.user.User;
import com.github.starcblack.login.user.dao.UserDao;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mindrot.jbcrypt.BCrypt;

public class RegisterCommand implements CommandExecutor {

    private final LoginManager loginManager = LoginManager.getInstance();
    private final UserDao userDao = new UserDao();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(StarLogin.getInstance().getConfig().getString("messages.global.playerOnly"));
            return true;
        }

        Player player = (Player) sender;

        // Verifica se o jogador já está registrado
        if (userDao.isUserRegistered(player.getName())) {
            player.sendMessage(StarLogin.getInstance().getConfig().getString("messages.commands.registerCommand.alreadyRegistered"));
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(StarLogin.getInstance().getConfig().getString("messages.commands.registerCommand.incorrectUsage"));
            return true;
        }

        String password = args[0];
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        // Registra o jogador
        User user = new User(player.getName(), hashedPassword);
        userDao.saveUser(user);

        // Remova o jogador da fila de autenticação
        loginManager.removeAuthenticationQueue(player);

        player.setWalkSpeed(0.2F);
        player.sendMessage(StarLogin.getInstance().getConfig().getString("messages.commands.registerCommand.successfulRegistration"));

        ActionBarAPI.sendActionBar(player, StarLogin.getInstance().getConfig().getString("messages.commands.registerCommand.successfulRegistrationActionBar"));

        return true;
    }
}
