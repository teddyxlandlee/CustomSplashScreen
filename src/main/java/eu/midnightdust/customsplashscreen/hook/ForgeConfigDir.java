package eu.midnightdust.customsplashscreen.hook;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.file.Path;

/**
 * @deprecated this class should only be accessed when classes
 * are transformed into Forge status.
 */
@Deprecated
public class ForgeConfigDir {
    private static volatile Path configDir;

    @SuppressWarnings("unused")
    public static Path getConfigDir() {
        if (configDir == null) {
            synchronized (ForgeConfigDir.class) {
                if (configDir == null) {
                    try {
                        Class<?> clazz = Class.forName("net.minecraftforge.fml.loading.FMLPaths");
                        var getter = MethodHandles.publicLookup().findStaticGetter(clazz,
                                "CONFIGDIR", clazz);
                        var get = MethodHandles.publicLookup().findVirtual(clazz, "get",
                                MethodType.methodType(Path.class));
                        configDir = (Path) get.invoke(getter.invoke());
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return configDir;
    }
}
