package com.github.pinont.singularitylib.api.storage;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bson.Document;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.github.pinont.singularitylib.plugin.CorePlugin.sendConsoleMessage;

/**
 * Thin MongoDB client for the Singularity framework (Phase 2).
 *
 * <p>Configuration section (e.g. from config.yml):
 * <pre>{@code
 * database:
 *   host: localhost
 *   port: 27017
 *   databaseName: minecraft
 *   username: ""   # optional
 *   password: ""   # optional
 * }</pre>
 *
 * <p>Sync operations only; pair with the async {@link Database} facade if you
 * need off-thread execution patterns.
 */
public class Mongo implements AutoCloseable {

    private final MongoClient client;
    private final com.mongodb.client.MongoDatabase database;

    public Mongo(Plugin plugin, ConfigurationSection cfg) {
        String host = cfg.getString("host", "localhost");
        int port = cfg.getInt("port", 27017);
        String name = cfg.getString("databaseName", "minecraft");
        String user = cfg.getString("username", "");
        String pass = cfg.getString("password", "");

        String uri;
        if (user != null && !user.isEmpty()) {
            String encUser = URLEncoder.encode(user, StandardCharsets.UTF_8);
            String encPass = URLEncoder.encode(pass == null ? "" : pass, StandardCharsets.UTF_8);
            uri = "mongodb://" + encUser + ":" + encPass + "@" + host + ":" + port + "/?authSource=admin";
        } else {
            uri = "mongodb://" + host + ":" + port;
        }
        sendConsoleMessage(Component.text("[DB] Connecting MongoDB: " + host + ":" + port + "/" + name, NamedTextColor.YELLOW));
        this.client = MongoClients.create(uri);
        this.database = client.getDatabase(name);
    }

    private MongoCollection<Document> coll(String collection) {
        return database.getCollection(collection);
    }

    /**
     * Finds the first document matching the filter.
     */
    public Document findFirst(String collection, Document filter) {
        return coll(collection).find(filter == null ? new Document() : filter).first();
    }

    /**
     * Inserts a document.
     */
    public void insert(String collection, Document doc) {
        coll(collection).insertOne(doc);
    }

    /**
     * Upserts: $set the update fields on the first match, inserting if absent.
     */
    public void upsert(String collection, Document filter, Document update) {
        coll(collection).findOneAndUpdate(
                filter,
                new Document("$set", update),
                new FindOneAndUpdateOptions().upsert(true)
        );
    }

    /**
     * Deletes all documents matching the filter.
     */
    public void delete(String collection, Document filter) {
        coll(collection).deleteMany(filter);
    }

    /**
     * Counts matching documents.
     */
    public long count(String collection, Document filter) {
        return coll(collection).countDocuments(filter == null ? new Document() : filter);
    }

    @Override
    public void close() {
        client.close();
    }
}