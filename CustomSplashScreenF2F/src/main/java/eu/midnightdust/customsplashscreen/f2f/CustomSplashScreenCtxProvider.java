package eu.midnightdust.customsplashscreen.f2f;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xland.mcmodbridge.fa2fomapper.api.SimpleMappingContextProvider;

import java.io.BufferedReader;
import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CustomSplashScreenCtxProvider extends SimpleMappingContextProvider {
    private static final Logger LOGGER = LogManager.getLogger();

    static String getMappingString() {
        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            Class<?> clazz = Class.forName(CustomSplashScreenCtxProvider.class.getPackage().getName() +
                    ".$Mapping");
            return (String) lookup.findStaticGetter(clazz, "instance", String.class).invoke();
        } catch (Throwable t) {
            LOGGER.fatal("Can't find the mapping provider class");
            return "v1\tbase";
        }
    }

    @Override
    protected BufferedReader mappingReader() {
        return new BufferedReader(new StringReader(getMappingString()));
    }

    CustomSplashScreenCtxProvider() {
        super(REMAPPED_CLASSES.stream()
                .map(c -> "eu.midnightdust.customsplashscreen." + c)
                .map(c -> c.replace('.', '/'))
                .collect(Collectors.toSet())
        );
    }

    public static final Collection<String> REMAPPED_CLASSES = List.of(
            "CustomSplashScreenClient",
            "hook.SplashScreenHooks",
            "texture.BlurredConfigTexture",
            "texture.ConfigTexture",
            "texture.EmptyTexture"
    );
}
