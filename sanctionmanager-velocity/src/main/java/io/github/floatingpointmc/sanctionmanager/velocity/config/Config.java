package io.github.floatingpointmc.sanctionmanager.velocity.config;

import org.slf4j.Logger;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class Config {
    private final Path dataDirectory;
    private final Logger logger;
    private Map<String, Object> data;

    public Config(Path dataDirectory, Logger logger) {
        this.dataDirectory = dataDirectory;
        this.logger = logger;
    }

    public void saveDefaultConfig() {
        saveResource("config.yml", false);
    }

    public void saveDefaultMessages() {
        saveResource("messages.yml", false);
    }

    public void reloadConfig() {
        Path file = dataDirectory.resolve("config.yml");
        if (!Files.exists(file)) {
            data = new HashMap<>();
            return;
        }
        try (InputStream in = Files.newInputStream(file)) {
            Yaml yaml = new Yaml();
            data = yaml.load(in);
            if (data == null) data = new HashMap<>();
        } catch (IOException e) {
            logger.error("Failed to load config.yml", e);
            data = new HashMap<>();
        }
    }

    private void ensureLoaded() {
        if (data == null) {
            reloadConfig();
        }
    }

    public String getString(String path) {
        return getString(path, null);
    }

    @SuppressWarnings("unchecked")
    public String getString(String path, String def) {
        ensureLoaded();
        Object value = getNested(data, path);
        return value != null ? String.valueOf(value) : def;
    }

    @SuppressWarnings("unchecked")
    public int getInt(String path, int def) {
        ensureLoaded();
        Object value = getNested(data, path);
        return value instanceof Number ? ((Number) value).intValue() : def;
    }

    @SuppressWarnings("unchecked")
    public List<String> getStringList(String path) {
        ensureLoaded();
        Object value = getNested(data, path);
        if (value instanceof List) {
            List<String> result = new ArrayList<>();
            for (Object item : (List<?>) value) {
                result.add(item != null ? String.valueOf(item) : "");
            }
            return result;
        }
        return Collections.emptyList();
    }

    private Object getNested(Map<String, Object> map, String path) {
        String[] keys = path.split("\\.");
        Object current = map;
        for (String key : keys) {
            if (current instanceof Map) {
                current = ((Map<String, Object>) current).get(key);
                if (current == null) return null;
            } else {
                return null;
            }
        }
        return current;
    }

    public void saveResource(String resourcePath, boolean replace) {
        if (resourcePath == null || resourcePath.isEmpty()) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found");
        }

        try {
            Path outFile = dataDirectory.resolve(resourcePath);
            if (!Files.exists(outFile.getParent())) {
                Files.createDirectories(outFile.getParent());
            }
            if (!Files.exists(outFile) || replace) {
                Files.copy(in, outFile, StandardCopyOption.REPLACE_EXISTING);
            } else {
                logger.warn("Could not save {} because it already exists.", outFile.getFileName());
            }
            in.close();
        } catch (IOException e) {
            logger.error("Could not save {}", resourcePath, e);
        }
    }
}