package eu.midnightdust.customsplashscreen.mixin;

import eu.midnightdust.customsplashscreen.f2f.AsmSplashScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import xland.mcmodbridge.fa2fomapper.api.Mapping;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class FabricCustomSplashScreenMixinPlugin implements IMixinConfigPlugin {
    private AsmSplashScreen engine;

    @Override
    public void onLoad(String mixinPackage) {
        var res = FabricLoader.getInstance().getMappingResolver();
        engine = new AsmSplashScreen(e -> mapField(res, e),
                e -> mapMethod(res, e));
    }

    private Mapping.NodeElement mapField(MappingResolver res, Mapping.NodeElement element) {
        Type owner = mapType(res, element.getOwner());
        String name = res.mapFieldName("intermediary", owner.getClassName(), element.getName(), element.getDesc().getDescriptor());
        Type desc = mapType(res, element.getDesc());
        return Mapping.NodeElement.of(owner.getInternalName(), name, desc.getDescriptor());
    }

    private Mapping.NodeElement mapMethod(MappingResolver res, Mapping.NodeElement element) {
        Type owner = mapType(res, element.getOwner());
        String name = res.mapMethodName("intermediary", owner.getClassName(), element.getName(), element.getDesc().getDescriptor());
        Type desc = mapMethodType(res, element.getDesc());
        return Mapping.NodeElement.of(owner.getInternalName(), name, desc.getDescriptor());
    }

    private Type mapType(MappingResolver res, Type type) {
        switch (type.getSort()) {
            case Type.ARRAY:
                final int dimensions = type.getDimensions();
                Type elementType = type.getElementType();
                elementType = mapType(res, elementType);

                String sb = "[".repeat(Math.max(0, dimensions)) +
                        elementType.getDescriptor();
                return Type.getType(sb);

            case Type.OBJECT:
                var n = res.mapClassName("intermediary", type.getClassName());
                return Type.getObjectType(n.replace('.', '/'));
            default:
                return type;
        }
    }

    private Type mapMethodType(MappingResolver res, Type type) {
        Type returnType = type.getReturnType();
        returnType = mapType(res, returnType);
        final Type[] types = Arrays.stream(type.getArgumentTypes())
                .map(t -> this.mapType(res, t))
                .toArray(Type[]::new);
        return Type.getMethodType(returnType, types);
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {return null;}

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        if (mixinClassName.endsWith(".SplashOverlayAsmWrapper")) {
            engine.apply(targetClass);
        }
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
