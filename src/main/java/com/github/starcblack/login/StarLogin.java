package com.github.starcblack.login;

import com.github.starcblack.login.commands.ChangePasswordCommand;
import com.github.starcblack.login.commands.LoginCommand;
import com.github.starcblack.login.commands.RegisterCommand;
import com.github.starcblack.login.misc.database.DatabaseConnector;
import com.github.starcblack.login.listeners.PlayerListeners;
import com.github.starcblack.login.user.dao.UserDao;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class StarLogin extends JavaPlugin {

    @Getter
    public static StarLogin instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        getServer().getScheduler().runTaskAsynchronously(this, this::loadMySQL);
        getServer().getPluginManager().registerEvents(new PlayerListeners(), this);

        getCommand("changepassword").setExecutor(new ChangePasswordCommand());
        getCommand("login").setExecutor(new LoginCommand());
        getCommand("register").setExecutor(new RegisterCommand());

        getLogger().info("§a[StarLogin] - Carregado com sucesso!");
    }

    @Override
    public void onDisable() {
        if (isEnabled()) {
            getServer().getScheduler().runTaskAsynchronously(this, this::closeConnection);
            getLogger().info("§a[StarLogin] - Desligado com sucesso!");
        }
    }

    private void loadMySQL() {
        UserDao userDao = new UserDao();
        try {
            DatabaseConnector.initialize(instance);
            userDao.createTableIfNotExists();
            getLogger().info("§a[StarLogin] - O banco de dados foi iniciado com sucesso!");
        } catch (Exception e) {
            getLogger().severe("§c[StarLogin] - Não foi possível criar o banco de dados: " + e.getMessage());
        }
    }

    private void closeConnection() {
        try {
            DatabaseConnector.closeConnection();
            getLogger().info("§a[StarLogin] - Conexão com o banco de dados fechada com sucesso!");
        } catch (SQLException e) {
            getLogger().severe("§c[StarLogin] - Erro ao fechar a conexão com o banco de dados: " + e.getMessage());
        }
    }
}
