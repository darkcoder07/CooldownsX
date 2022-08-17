package com.github.sirblobman.cooldowns.dictionary;

import java.util.EnumMap;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.cooldowns.CooldownPlugin;

abstract class Dictionary<E extends Enum<E>> {
    private final CooldownPlugin plugin;
    private final String fileName;
    private final Class<E> enumClass;
    private final Map<E, String> dictionary;

    public Dictionary(CooldownPlugin plugin, String fileName, Class<E> enumClass) {
        this.plugin = Validate.notNull(plugin, "plugin must not be null!");
        this.fileName = Validate.notEmpty(fileName, "fileName must not be empty!");
        this.enumClass = Validate.notNull(enumClass, "enumClass must not be null!");
        this.dictionary = new EnumMap<>(this.enumClass);
    }

    protected final CooldownPlugin getPlugin() {
        return this.plugin;
    }

    protected final String getFileName() {
        return this.fileName;
    }

    protected final Class<E> getEnumClass() {
        return this.enumClass;
    }

    protected final ConfigurationManager getConfigurationManager() {
        CooldownPlugin plugin = getPlugin();
        return plugin.getConfigurationManager();
    }

    protected final YamlConfiguration getConfiguration() {
        String fileName = getFileName();
        ConfigurationManager configurationManager = getConfigurationManager();
        return configurationManager.get(fileName);
    }

    public String get(E key) {
        String defaultName = key.toString();
        return this.dictionary.getOrDefault(key, defaultName);
    }

    public void set(E key, String value) {
        this.dictionary.put(key, value);
    }

    public void saveConfiguration() {
        YamlConfiguration configuration = getConfiguration();
        Class<E> enumClass = getEnumClass();
        E[] keys = enumClass.getEnumConstants();

        for (E key : keys) {
            String keyName = key.name();
            String keyValue = get(key);
            configuration.set(keyName, keyValue);
        }

        String fileName = getFileName();
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.save(fileName);
    }

    public void reloadConfiguration() {
        this.dictionary.clear();
        YamlConfiguration configuration = getConfiguration();
        Class<E> enumClass = getEnumClass();
        E[] keys = enumClass.getEnumConstants();

        for (E key : keys) {
            String keyName = key.name();
            if (!configuration.isString(keyName)) {
                continue;
            }

            String keyDefaultValue = key.toString();
            String keyValue = configuration.getString(keyName, keyDefaultValue);
            set(key, keyValue);
        }
    }
}