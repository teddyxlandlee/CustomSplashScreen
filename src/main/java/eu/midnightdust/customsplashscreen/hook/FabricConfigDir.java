package eu.midnightdust.customsplashscreen.hook;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public interface FabricConfigDir {
    static Path getConfigDir() { return FabricLoader.getInstance().getConfigDir(); }
}
