package net.azisaba.azipluginmessaging.api.yaml;

import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;

public interface YamlMember {
    @NotNull
    Yaml getYaml();

    /**
     * Returns the raw data of this object.
     * @return the raw data
     */
    @NotNull
    Object getRawData();

    /**
     * Save the data to the file.
     * @param file the file
     * @throws IOException if an I/O error occurs
     */
    default void save(@NotNull File file) throws IOException {
        YamlConfiguration.saveTo(file, getYaml(), this);
    }

    /**
     * Save the data to the file.
     * @param path the path to the file
     * @throws IOException if an I/O error occurs
     */
    default void save(@NotNull String path) throws IOException {
        YamlConfiguration.saveTo(path, getYaml(), this);
    }

    /**
     * Dumps the data as (human-readable) String.
     * @return dumped data
     */
    @NotNull
    default String dump() {
        return YamlConfiguration.dump(this);
    }
}
