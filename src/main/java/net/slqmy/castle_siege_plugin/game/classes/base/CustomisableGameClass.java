package net.slqmy.castle_siege_plugin.game.classes.base;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public abstract class CustomisableGameClass extends GameClass {
    private final String className;

    public CustomisableGameClass(String className) {
        super();
        this.className = className.toLowerCase();
    }

    @Override
    public int getPlayerCount(int totalPlayerCount) {
        YamlConfiguration configuration = (YamlConfiguration) plugin.getConfig();

        ConfigurationSection classConfiguration = configuration.getConfigurationSection("classes." + className);
        assert classConfiguration != null;

        boolean isPercentage = classConfiguration.getBoolean("is-percentage");
        double value = classConfiguration.getDouble("value");

        return (int) (isPercentage ? Math.ceil((value / 100D) * totalPlayerCount) : value);
    }
}
