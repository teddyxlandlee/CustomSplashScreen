package eu.midnightdust.customsplashscreen.hook;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public final class FabricConfigDir {
    public static Path getConfigDir() { return FabricLoader.getInstance().getConfigDir(); }
}
