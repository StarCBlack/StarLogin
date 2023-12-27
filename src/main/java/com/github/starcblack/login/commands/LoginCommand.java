package com.github.starcblack.login.commands;

import com.github.starcblack.login.manager.LoginManager;
import com.github.starcblack.login.misc.utils.ActionBarAPI;
import com.github.starcblack.login.user.dao.UserDao;
import org.mindrot.jbcrypt.BCrypt;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.swing.*;

public class LoginCommand implements CommandExecutor {

    private final LoginManager loginManager = LoginManager.getInstance();
    private final int MAX_LOGIN_ATTEMPTS =  3;

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
            player.setWalkSpeed(0.2F);
            player.sendMessage("§eLogin realizado com sucesso, obrigado por entrar em nosso servidor!");
            ActionBarAPI.sendActionBar(player, "§aLogin realizado com sucesso , §lPARABÉNS!");
        } else {
            int attempts = loginManager.incrementLoginAttempts(player);
            if (attempts >= MAX_LOGIN_ATTEMPTS) {
                player.kickPlayer("§cSTARLOGIN\n\n§cA senha está incorreta!\n §cVocê excedeu o número máximo de tentativas - (3/3)");
            } else {
                player.sendMessage("§cSenha incorreta. Tentativa (" + attempts + "/" + MAX_LOGIN_ATTEMPTS + "). Tente novamente.");
            }
        }
        return true;
    }
}
