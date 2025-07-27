package fr.rudy.dialogue;

import fr.rudy.dialogue.command.DialogueCommand;
import fr.rudy.databaseapi.DatabaseAPI;
import fr.rudy.dialogue.manager.DialogueProgressManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Main extends JavaPlugin {

    private static Main instance;
    private DialogueProgressManager dialogueProgressManager;
    private Connection database;

    public static Main get() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        Plugin plugin = Bukkit.getPluginManager().getPlugin("DatabaseAPI");

        if (plugin instanceof DatabaseAPI dbPlugin && plugin.isEnabled()) {
            database = dbPlugin.getDatabaseManager().getConnection();

            try (Statement stmt = database.createStatement()) {
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS dialogues (" +
                        "uuid TEXT NOT NULL, " +
                        "npc TEXT NOT NULL, " +
                        "step INT NOT NULL, " +
                        "PRIMARY KEY (uuid, npc))"
                );
            } catch (SQLException e) {
                e.printStackTrace();
            }

            dialogueProgressManager = new DialogueProgressManager(database);
        } else {
            getLogger().severe("❌ DatabaseAPI introuvable !");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        getCommand("dialogue").setExecutor(new DialogueCommand());

        getLogger().info("✅ Dialogue activé !");
    }

    public DialogueProgressManager getDialogueProgressManager() {
        return dialogueProgressManager;
    }

    public Connection getDatabase() {
        return database;
    }
}
