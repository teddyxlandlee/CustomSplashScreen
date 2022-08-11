package eu.midnightdust.customsplashscreen.f2f;

import cpw.mods.modlauncher.api.*;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;
import xland.mcmodbridge.fa2fomapper.SupportedPlatform;
import xland.mcmodbridge.fa2fomapper.api.Mapping;
import xland.mcmodbridge.fa2fomapper.api.tiny.TinyUtils;

import java.io.BufferedReader;
import java.io.StringReader;
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
                return scr.apply(input);
            }

            @Override
            public @NotNull TransformerVoteResult castVote(ITransformerVotingContext context) {
                return TransformerVoteResult.YES;
            }

            @Override
            public @NotNull Set<Target> targets() {
                return Collections.singleton(Target.targetClass(type));
            }
        });
    }
}
