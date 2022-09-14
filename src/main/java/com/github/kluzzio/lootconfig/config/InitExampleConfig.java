package com.github.kluzzio.lootconfig.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class InitExampleConfig {

    private static final String EXAMPLE_SETTINGS = "loot_config/example_config/settings_and_definitions_config.json";
    public static final String EXAMPLE_MODIFY =  "loot_config/example_config/modify_config.json";
    public static final String EXAMPLE_REPLACE =  "loot_config/example_config/replace_config.json";

    public static void init() {
        createExampleConfig(EXAMPLE_SETTINGS);
        createExampleConfig(EXAMPLE_MODIFY);
        createExampleConfig(EXAMPLE_REPLACE);
    }

    private static void createExampleConfig(String configToCreate) {
        // Create the file in memory
        File configFile = ConfigManager.FABRIC_CONFIG_DIR.resolve(configToCreate).toFile();
        // If it doesn't exist in disk
        if (!configFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            configFile.getParentFile().mkdirs();
            try {
                String configFileText = getExampleText(configToCreate);

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

    private static String getExampleText(String configToCreate) {
        if (configToCreate.equals(EXAMPLE_SETTINGS)) {
            return
                """
                {
                  "LoadPoolsAtRuntime": true,
                  "NameDefinitions": {
                    "Beans": [
                      "minecraft:chests/abandoned_mineshaft",
                      "minecraft:entities/cow",
                      "minecraft:blocks/sand"
                    ],
                    "Rice": [
                      "minecraft:blocks/dirt"
                    ]
                  },
                  "LootPoolDefinitions": {
                    "banana_hammock": {
                      "MinRolls": 2,
                      "MaxRolls": 2,
                      "RollSuccessChance": 1.0,
                      "LootingRollSuccessModifier": 0.2,
                      "MinBonusRolls": 2,
                      "MaxBonusRolls": 2,
                      "Conditions": {
                        "LocationCheck_Biome": {
                          "minecraft:desert": []
                        }
                      },
                      "Entries": {
                        "minecraft:tnt": [1, 17]
                      }
                    },
                    "Rags": {
                      "MinRolls": 2,
                      "MaxRolls": 2,
                      "RollSuccessChance": 1.0,
                      "LootingRollSuccessModifier": 0.2,
                      "MinBonusRolls": 2,
                      "MaxBonusRolls": 2,
                      "Conditions": {
                        "LocationCheck_Biome": {
                          "minecraft:plains": []
                        },
                        "SurvivesExplosion": {},
                        "WeatherCheck": {
                          "RainingTrue": [],
                          "ThunderingFalse": []
                        }
                      },
                      "Entries": {
                        "minecraft:potion(minecraft:healing)": [1, 3],
                        "minecraft:dirt": [1, 17]
                      }
                    }
                  }
                }
                """;
        }
        if (configToCreate.equals(EXAMPLE_MODIFY)) {
            return
                    """
                    {
                      "LootTableIds": {
                        "minecraft:chests/abandoned_mineshaft": {
                          "LootPools": ["banana_hammock"]
                        }
                      },
                      "Names": {
                        "Beans": {
                          "LootPools": ["Rags", "banana_hammock"]
                        }
                      }
                    }
                    """;
        }
        if (configToCreate.equals(EXAMPLE_REPLACE)) {
            return """
                    {
                      "LootTableIds": {
                        "minecraft:chests/abandoned_mineshaft": {
                          "LootPools": ["Rags", "banana_hammock"]
                        }
                      },
                      "Names": {
                        "PlayerKilled": {
                          "LootPools": ["banana_hammock"]
                        }
                      }
                    }
                    """;
        }
        return "";
    }
}
