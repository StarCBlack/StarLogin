package com.github.starcblack.login.commands;

import com.github.starcblack.login.manager.LoginManager;
import com.github.starcblack.login.user.dao.UserDao;
import org.mindrot.jbcrypt.BCrypt;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LoginCommand implements CommandExecutor {

    private final LoginManager loginManager = LoginManager.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        UserDao userDao = new UserDao();

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cEste comando só pode ser executado por jogadores.");
            return true;
        }

        Player player = (Player) sender;
        // Verifica se o jogador está na fila de autenticação
        if (loginManager.isUserAuthenticated(player)) {
            player.sendMessage("§cVocê já está autenticado no servidor.");
            return true;
        }
        // Verifica se o jogador já está registrado
        if (!userDao.isUserRegistered(player.getName())) {
            player.sendMessage("§cVocê ainda não está registrado no servidor. Use /register para se registrar.");
            return true;
        }

        // Verifica se o comando foi usado corretamente
        if (args.length != 1) {
            player.sendMessage("§cUso correto: /login <senha>");
            return true;
        }

        String password = args[0];

        // Verifica se a senha está correta
        if (userDao.checkPassword(player.getName(), password)) {
            // Remova o jogador da fila de autenticação
            loginManager.removeAuthenticationQueue(player);
            player.sendMessage("§aLogin realizado com sucesso!");
        } else {
            player.sendMessage("§cSenha incorreta. Tente novamente.");
        }
        return true;
    }
}
