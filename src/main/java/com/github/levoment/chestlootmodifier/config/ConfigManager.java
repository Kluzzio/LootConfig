package com.github.levoment.chestlootmodifier.config;

import com.github.levoment.chestlootmodifier.ChestLootModifierMod;
import com.github.levoment.chestlootmodifier.config.configobjects.ConfigurationObject;
import com.github.levoment.chestlootmodifier.config.configobjects.SettingsConfigurationObject;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {

    public static final Path FABRIC_CONFIG_DIR = FabricLoader.getInstance().getConfigDir();
    public static SettingsConfigurationObject SETTINGS_CONFIG;
    public static boolean SUCCESSFULLY_LOADED_SETTINGS;
    public static ConfigurationObject MODIFY_CONFIG;
    public static ConfigurationObject REPLACE_CONFIG;
    public static boolean SUCCESSFULLY_LOADED_MODIFY;
    public static boolean SUCCESSFULLY_LOADED_REPLACE;

    public static void interpretConfigFile(String configInQuestion) {
        //Make config if it doesn't exist
        createConfigFile(configInQuestion);
        //Read config and store into appropriate configuration object
        readConfigFile(configInQuestion);
    }

    public static void createConfigFile(String configToCreate) {
        // Create the file in memory
        File configFile = FABRIC_CONFIG_DIR.resolve(configToCreate).toFile();
        // If it doesn't exist in disk
        if (!configFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            configFile.getParentFile().mkdirs();
            try {
                String configFileText = getConfigFileText(configToCreate);

                FileWriter configFileWriter = new FileWriter(configFile);

                configFileWriter.write(configFileText);
                configFileWriter.close();

                //noinspection ResultOfMethodCallIgnored
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void readConfigFile(String configToRead) {
        // Create the variable to contain the config file in memory
        File configFile = FABRIC_CONFIG_DIR.resolve(configToRead).toFile();
        if (configFile.exists()) {
            // Create the Gson instance
            try {
                // Create a reader for the configuration file
                Reader configReader = Files.newBufferedReader(configFile.toPath());
                // Create an object from the config file
                if (configToRead.equals(ChestLootModifierMod.SETTINGS_CONFIG_FILE)) {
                    testSuccessfulSettingsConfig(configReader, configFile);
                } else testSuccessfulConfig(configReader, configFile, configToRead);
            } catch (IOException e) {
                ChestLootModifierMod.LOGGER.error("[Chest Loot Modifier Mod] IO error when reading configuration file:");
                e.printStackTrace();
                System.exit(-1);
            } catch (JsonSyntaxException jsonSyntaxException) {
                ChestLootModifierMod.LOGGER.error("[Chest Loot Modifier Mod] JSON syntax error when reading configuration file (" + configFile.getName() + "):");
                jsonSyntaxException.printStackTrace();
                System.exit(-1);
            } catch (JsonParseException jsonParseException) {
                ChestLootModifierMod.LOGGER.error("[Chest Loot Modifier Mod] JSON parse error when reading configuration file (" + configFile.getName() + "):");
                jsonParseException.printStackTrace();
                System.exit(-1);
            }
        } else ChestLootModifierMod.LOGGER.error("No configuration file found for Chest Loot Modifier Mod in: " + configFile.getPath());
    }

    public static String getConfigFileText(String configToCreate) {
        return configToCreate.equals(ChestLootModifierMod.SETTINGS_CONFIG_FILE) ?
                //Settings Default
                """
                {
                  "LoadPoolsAtRuntime": false,
                  "NameDefinitions": {
                  },
                  "LootPoolDefinitions": {
                  }
                }
                """ :
                //Modify/Replace Default
                """
                {
                  "LootTableIds": {
                  },
                  "Names": {
                  }
                }
                """;
    }

    public static void testSuccessfulConfig(Reader configReader, File configFile, String configToRead) {
        if (configToRead.equals(ChestLootModifierMod.MODIFY_CONFIG_FILE)) {
            MODIFY_CONFIG = new Gson().fromJson(configReader, ConfigurationObject.class);
            SUCCESSFULLY_LOADED_MODIFY = testSuccessfulLoad(MODIFY_CONFIG, configFile);
        } else if (configToRead.equals(ChestLootModifierMod.REPLACE_CONFIG_FILE)) {
            REPLACE_CONFIG = new Gson().fromJson(configReader, ConfigurationObject.class);
            SUCCESSFULLY_LOADED_REPLACE = testSuccessfulLoad(REPLACE_CONFIG, configFile);
        }
    }

    private static boolean testSuccessfulLoad(ConfigurationObject configurationObject, File configFile) {
        if (configurationObject.getLootTableIds() == null) {
            ChestLootModifierMod.LOGGER.error(ChestLootModifierMod.MOD_NAME_LOG_ID + " LootTableIds does not exist or is malformed in the configuration file: " + configFile.getName());
            return false;
        } else if (configurationObject.getNames() == null) {
            ChestLootModifierMod.LOGGER.error(ChestLootModifierMod.MOD_NAME_LOG_ID + " Names does not exist or is malformed in the configuration file: " + configFile.getName());
            return false;
        }
        return true;
    }

    public static void testSuccessfulSettingsConfig(Reader configReader, File configFile) {
        SETTINGS_CONFIG = new Gson().fromJson(configReader, SettingsConfigurationObject.class);
        if (SETTINGS_CONFIG.getNameDefinitions() == null) {
            ChestLootModifierMod.LOGGER.error(ChestLootModifierMod.MOD_NAME_LOG_ID + " NameDefinitions does not exist or is malformed in the configuration file: " + configFile.getName());
        } else if (SETTINGS_CONFIG.getLootPoolDefinitions() == null) {
            ChestLootModifierMod.LOGGER.error(ChestLootModifierMod.MOD_NAME_LOG_ID + " LootPoolDefinitions does not exist or is malformed in the configuration file: " + configFile.getName());
        } else {
            SUCCESSFULLY_LOADED_SETTINGS = true;
        }
    }

}
