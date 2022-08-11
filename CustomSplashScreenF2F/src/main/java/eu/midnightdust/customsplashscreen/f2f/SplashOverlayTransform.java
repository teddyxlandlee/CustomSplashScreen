package eu.midnightdust.customsplashscreen.f2f;

import cpw.mods.modlauncher.api.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import xland.mcmodbridge.fa2fomapper.SupportedPlatform;
import xland.mcmodbridge.fa2fomapper.api.Mapping;
import xland.mcmodbridge.fa2fomapper.api.tiny.TinyUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class SplashOverlayTransform implements ITransformationService {
    private Mapping mapping;
    private String type;

    @Override
    public @NotNull String name() {
        return "fa2fomapper-customsplashscreen-splashoverlay-teddyxlandlee";
    }

    @Override
    public void initialize(IEnvironment environment) {
        String s = CustomSplashScreenCtxProvider.getMappingString();
        mapping = TinyUtils.read(new BufferedReader(new StringReader(s)), "base",
                SupportedPlatform.current().getId());
        type = mapping.mapClass(AsmSplashScreen.C_TARGET);
    }

    @Override
    public void beginScanning(IEnvironment environment) {

    }

    @Override
    public void onLoad(IEnvironment env, Set<String> otherServices) {

    }

    @Override
    @SuppressWarnings("rawtypes")
    public @NotNull List<ITransformer> transformers() {
        return Collections.singletonList(new ITransformer<ClassNode>() {
            @Override
            public @NotNull ClassNode transform(ClassNode input, ITransformerVotingContext context) {
                AsmSplashScreen scr = new AsmSplashScreen(mapping::mapField, mapping::mapMethod);
                var o = scr.apply(input);
                if ("true".equals(System.getProperty("fa2fomapper.export"))) {
                    new Thread(() -> {
                        ClassWriter cw = new ClassWriter(1);
                        o.accept(cw);
                        var p = Paths.get(".fa2fomapper", o.name + ".class");
                        try {
                            Files.createDirectories(p.getParent());
                            try (var os = Files.newOutputStream(p)) {
                                os.write(cw.toByteArray());
                            }
                        } catch (IOException e) {
                            LOGGER.fatal("Can't dump class {}", o.name, e);
                        }
                    }, "SPLASH-OVERLAY-TRANS-2").start();
                }
                return o;
            }

            @Override
            public @NotNull TransformerVoteResult castVote(ITransformerVotingContext context) {
                return TransformerVoteResult.YES;
            }

            @Override
            public @NotNull Set<Target> targets() {
                return Collections.singleton(Target.targetClass(type));
            }

            private static final Logger LOGGER = LogManager.getLogger();
        });
    }
}
