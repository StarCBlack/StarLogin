package com.github.starcblack.login.manager;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

@Getter
public class LoginManager {

    @Getter
    public static LoginManager instance = new LoginManager();

    private final Set<Player> authenticationQueue = new HashSet<>();

    private void LoginCommand() {
    }

    public boolean isUserAuthenticated(Player player) {
        return !authenticationQueue.contains(player);
    }

    public void addAuthenticationQueue(Player player) {
        authenticationQueue.add(player);
    }

    public void removeAuthenticationQueue(Player player) {
        authenticationQueue.remove(player);
    }

}
