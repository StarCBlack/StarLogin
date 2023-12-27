package com.github.starcblack.login.user.dao;

import com.github.starcblack.login.misc.database.DatabaseConnector;
import com.github.starcblack.login.user.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    public void createTableIfNotExists() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "name VARCHAR(255) PRIMARY KEY," +
                "password VARCHAR(255) NOT NULL" +
                ");";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(createTableSQL)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveUser(User user) {
        String checkExistingUserSQL = "SELECT COUNT(*) FROM users WHERE name = ?";
        String insertUserSQL = "INSERT INTO users (name, password) VALUES (?, ?)";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement checkStatement = connection.prepareStatement(checkExistingUserSQL)) {
            checkStatement.setString(1, user.getName());
            try (ResultSet resultSet = checkStatement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    System.out.println("Usuário com nome semelhante já existe. Não foi salvo.");
                    return;
                }
            }

            try (PreparedStatement insertStatement = connection.prepareStatement(insertUserSQL)) {
                insertStatement.setString(1, user.getName());
                insertStatement.setString(2, user.getPassword());

                insertStatement.executeUpdate();
                System.out.println("Usuário salvo com sucesso!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User getUser(String userName) {
        String sql = "SELECT * FROM users WHERE name = ?";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, userName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String name = resultSet.getString("name");
                    String password = resultSet.getString("password");
                    return new User(name, password);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    public boolean isUserRegistered(String userName) {
        String sql = "SELECT COUNT(*) FROM users WHERE name = ?";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, userName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public void updatePassword(String userName, String newPassword) {
        String updatePasswordSQL = "UPDATE users SET password = ? WHERE name = ?";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement updateStatement = connection.prepareStatement(updatePasswordSQL)) {

            updateStatement.setString(1, newPassword);
            updateStatement.setString(2, userName);

            int rowsAffected = updateStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Senha do usuário atualizada com sucesso!");
            } else {
                System.out.println("Usuário não encontrado. A senha não foi atualizada.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public boolean checkPassword(String userName, String password) {
        String hashedPassword = getPasswordHash(userName);

        if (hashedPassword == null) {
            // Usuário não encontrado, ou erro ao recuperar a senha
            return false;
        }

        // Use BCrypt para verificar se as senhas correspondem
        return BCrypt.checkpw(password, hashedPassword);
    }

    // Método auxiliar para recuperar a senha hash do banco de dados
    private String getPasswordHash(String userName) {
        String sql = "SELECT password FROM users WHERE name = ?";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, userName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Retorna a senha hash armazenada no banco de dados
                    return resultSet.getString("password");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


}
