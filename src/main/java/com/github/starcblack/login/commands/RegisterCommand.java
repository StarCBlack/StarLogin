package com.github.starcblack.login.commands;

import com.github.starcblack.login.manager.LoginManager;
import com.github.starcblack.login.user.User;
import com.github.starcblack.login.user.dao.UserDao;
import org.apache.commons.codec.digest.DigestUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mindrot.jbcrypt.BCrypt;

public class RegisterCommand implements CommandExecutor {

    private final LoginManager loginManager = LoginManager.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        UserDao userDao = new UserDao();
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cEste comando só pode ser executado por jogadores.");
            return true;
        }

        Player player = (Player) sender;

        // Verifica se o jogador já está registrado
        if (userDao.isUserRegistered(player.getName())) {
            player.sendMessage("§cVocê já está registrado no servidor.");
            return true;
        }

        // Verifica se o comando foi usado corretamente
        if (args.length != 1) {
            player.sendMessage("§cUso correto: /register <senha>");
            return true;
        }

        String password = args[0];
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        // Registra o jogador
        User user = new User(player.getName(), hashedPassword);
        userDao.saveUser(user);

        // Remova o jogador da fila de autenticação
        loginManager.removeAuthenticationQueue(player);

        player.sendMessage("§aRegistro realizado com sucesso!");
        return true;
    }
}
