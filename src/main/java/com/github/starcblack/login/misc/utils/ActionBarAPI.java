package com.github.starcblack.login.misc.utils;

import com.github.starcblack.login.StarLogin;
import com.google.gson.JsonObject;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ActionBarAPI {

    /**
     * Envia uma mensagem para a ActionBar de um jogador.
     *
     * @param player  Jogador para o qual enviar a mensagem.
     * @param message Mensagem a ser exibida na ActionBar.
     */
    public static void sendActionBar(Player player, String message) {
        JsonObject json = new JsonObject();
        json.addProperty("text", message);

        PacketPlayOutChat packet = new PacketPlayOutChat(ChatSerializer.a(json.toString()), (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    /**
     * Envia uma mensagem para a ActionBar de um jogador por um período de tempo.
     *
     * @param player   Jogador para o qual enviar a mensagem.
     * @param message  Mensagem a ser exibida na ActionBar.
     * @param duration Tempo de exibição em segundos.
     */
    public static void sendActionBarWithDuration(Player player, String message, int duration) {
        sendActionBar(player, message);

        // Programa uma tarefa para limpar a ActionBar após o tempo especificado
        player.getServer().getScheduler().runTaskLater(StarLogin.getInstance(), () -> sendActionBar(player, ""), duration * 20L);
    }

    /**
     * Envia uma mensagem para a ActionBar de todos os jogadores online de forma assíncrona.
     *
     * @param message Mensagem a ser exibida na ActionBar.
     */
    public static void sendActionBarToAllPlayers(String message) {
        Bukkit.getScheduler().runTaskAsynchronously(StarLogin.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                sendActionBar(player, message);
            }
        });
    }

    /**
     * Envia uma mensagem para a ActionBar de todos os jogadores online por um período de tempo de forma assíncrona.
     *
     * @param message  Mensagem a ser exibida na ActionBar.
     * @param duration Tempo de exibição em segundos.
     */
    public static void sendActionBarToAllPlayersWithDuration(String message, int duration) {
        sendActionBarToAllPlayers(message);

        Bukkit.getScheduler().runTaskLaterAsynchronously(StarLogin.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                sendActionBar(player, "");
            }
        }, duration * 20L);
    }
}
