package eu.midnightdust.customsplashscreen;

import eu.midnightdust.customsplashscreen.config.CustomSplashScreenConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class CustomSplashScreenClient {
    private CustomSplashScreenClient() {}
    private static final CustomSplashScreenClient INSTANCE = new CustomSplashScreenClient();
    public static CustomSplashScreenClient getInstance() { return INSTANCE; }

    public static CustomSplashScreenConfig CS_CONFIG;
    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("customsplashscreen");
    private static final Path BackgroundTexture = CONFIG_PATH.resolve("background.png");
    private static final Path MojangTexture = CONFIG_PATH.resolve("mojangstudios.png");
    private static final Path MojankTexture = CONFIG_PATH.resolve("mojank.png");
    private static final Path ProgressBarTexture = CONFIG_PATH.resolve("progressbar.png");
    private static final Path ProgressBarBackgroundTexture = CONFIG_PATH.resolve("progressbar_background.png");

    public static void init() {
        getInstance().onInitializeClient();
    }

    //@Override
    public void onInitializeClient() {
        AutoConfig.register(CustomSplashScreenConfig.class, JanksonConfigSerializer::new);
        CS_CONFIG = AutoConfig.getConfigHolder(CustomSplashScreenConfig.class).getConfig();

        if (!Files.exists(CONFIG_PATH)) { // Run when config directory is not existing //
            try {
                Files.createDirectories(CONFIG_PATH); // Create our custom config directory //
            } catch (IOException e) {
                throw new RuntimeException("Can't create config dir", e);
            }

            // Open Input Streams for copying the default textures to the config directory //
            final ClassLoader cl = Thread.currentThread().getContextClassLoader();
            InputStream background = readResource(cl, "Iqj4fLE5TtRybrFdmk6vK/background.png");
            InputStream mojangstudios = readResource(cl, "Iqj4fLE5TtRybrFdmk6vK/mojangstudios.png");
            InputStream mojank = readResource(cl, "Iqj4fLE5TtRybrFdmk6vK/mojank.png");
            InputStream progressbar = readResource(cl, "Iqj4fLE5TtRybrFdmk6vK/progressbar.png");
            InputStream progressbarBG = readResource(cl, "Iqj4fLE5TtRybrFdmk6vK/progressbar_background.png");
            try {
                // Copy the default textures into the config directory //
                Files.copy(background,BackgroundTexture,StandardCopyOption.REPLACE_EXISTING);
                Files.copy(mojangstudios,MojangTexture,StandardCopyOption.REPLACE_EXISTING);
                Files.copy(mojank,MojankTexture,StandardCopyOption.REPLACE_EXISTING);
                Files.copy(progressbar,ProgressBarTexture,StandardCopyOption.REPLACE_EXISTING);
                Files.copy(progressbarBG,ProgressBarBackgroundTexture,StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException("Failed to copy files", e);
            }
        }
    }

    private static InputStream readResource(ClassLoader cl, String path) {
        return Objects.requireNonNull(cl.getResourceAsStream(path), path);
    }
}
