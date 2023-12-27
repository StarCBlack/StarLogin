package com.github.starcblack.login.manager;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
public class LoginManager {

    @Getter
    public static LoginManager instance = new LoginManager();

    private final Set<Player> authenticationQueue = new HashSet<>();

    private final Map<Player, Integer> loginAttempts = new HashMap<>();


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

    public int incrementLoginAttempts(Player player) {
        int attempts = loginAttempts.getOrDefault(player, 0)  + 1;
        loginAttempts.put(player, attempts);
        return attempts;

    }

}
