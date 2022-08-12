package eu.midnightdust.customsplashscreen.f2f;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.objectweb.asm.tree.*;
import xland.mcmodbridge.fa2fomapper.api.Mapping;

import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;

import static org.objectweb.asm.Opcodes.*;

public record AsmSplashScreen(UnaryOperator<Mapping.NodeElement> fieldMapper,
                              UnaryOperator<Mapping.NodeElement> methodMapper)
        implements UnaryOperator<ClassNode> {
    public static final String C_TARGET = "net/minecraft/class_425";
    public static final String C_HOOKS = "eu/midnightdust/customsplashscreen/hook/SplashScreenHooks";
    public static final String C_RENDER_SYSTEM = "com/mojang/blaze3d/systems/RenderSystem";

    @Override
    public ClassNode apply(ClassNode cn) {
        getMethod(cn, "method_18819", "(Lnet/minecraft/class_310;)V").ifPresent(m -> {          // init
            InsnList l = new InsnList();
            l.add(mapMethod(INVOKESTATIC, "eu/midnightdust/customsplashscreen/CustomSplashScreenClient",
                    "init", "()V"));
            l.add(new VarInsnNode(ALOAD, 0));
            l.add(mapField(GETSTATIC, C_TARGET, "field_2483", "Lnet/minecraft/class_2960;"));   // defaultLogo
            l.add(mapMethod(INVOKESTATIC, C_HOOKS, "onInit", "(Lnet/minecraft/class_310;Lnet/minecraft/class_2960;)V"));
            l.add(new InsnNode(RETURN));
            m.instructions.insert(l);
        });
        getMethod(cn, "method_18103", "(Lnet/minecraft/class_4587;IIIIF)V").ifPresent(m -> {    // renderProgressBar
            InsnList l = new InsnList();
            l.add(new VarInsnNode(ALOAD, 1));
            for (int i = 2; i <= 5; i++) l.add(new VarInsnNode(ILOAD, i));
            l.add(new VarInsnNode(ALOAD, 0));
            l.add(mapField(GETFIELD, C_TARGET, "field_17770", "F"));    // progress
            l.add(mapMethod(INVOKESTATIC, C_HOOKS, "afterRenderBar", "(Lnet/minecraft/class_4587;IIIIF)V"));
            m.instructions.forEach(node -> {
                if (node.getOpcode() == RETURN)
                    m.instructions.insertBefore(node, l);
            });
        });
        getMethod(cn, "method_25394", "(Lnet/minecraft/class_4587;IIF)V").ifPresent(m -> {      // Drawable#render
            // 1. Replace ARGB
            // 2. Render our custom background image
            // 3. Render the Logo
            LabelNode disableBlend = new LabelNode();
            MutableBoolean disableBlendLabeled = new MutableBoolean();
            m.instructions.forEach(node -> {
                if (node instanceof FieldInsnNode field &&
                        sameField(field, mapField(GETSTATIC, C_TARGET, "field_25041", "Ljava/util/function/IntSupplier;"))) {
                    m.instructions.set(node, mapMethod(INVOKESTATIC, C_HOOKS, "backgroundColorSupplier", "()Ljava/util/function/IntSupplier;"));
                } else if (node instanceof MethodInsnNode method) {
                    if (sameMethod(method, mapMethod(INVOKESTATIC, C_RENDER_SYSTEM, "disableBlend", "()V")) && !disableBlendLabeled.isTrue()) {
                        InsnList l = new InsnList();
                        l.add(disableBlend);    // label
                        l.add(new VarInsnNode(ALOAD, 0));
                        l.add(mapField(GETFIELD, C_TARGET, "field_18217", "Lnet/minecraft/class_310;"));    // client
                        l.add(new VarInsnNode(ALOAD, 1));   // matrices, net/minecraft/class_4587
                        l.add(new VarInsnNode(FLOAD, 11));  // float s
                        l.add(mapField(GETSTATIC, C_TARGET, "field_2483", "Lnet/minecraft/class_2960;"));   // LOGO
                        l.add(mapMethod(INVOKESTATIC, C_HOOKS, "renderLogo", "(Lnet/minecraft/class_310;Lnet/minecraft/class_4587;FLnet/minecraft/class_2960;)V"));
                        m.instructions.insert(method, l);
                        disableBlendLabeled.setTrue();
                    } else if (sameMethod(method, mapMethod(INVOKESTATIC, C_RENDER_SYSTEM, "setShaderTexture", "(ILnet/minecraft/class_2960;)V"))) {
                        // Insert before it
                        InsnList l = new InsnList();
                        LabelNode yesRenderBg = new LabelNode();
                        l.add(new InsnNode(POP));   // pop the default logo, net/minecraft/class_2960
                        l.add(mapMethod(INVOKESTATIC, C_HOOKS, "shouldRenderBg", "()Z"));
                        l.add(new JumpInsnNode(IFNE, yesRenderBg));
                        // NO
                        l.add(new InsnNode(POP));   // pop the const int 0
                        l.add(new JumpInsnNode(GOTO, disableBlend));
                        // YES
                        l.add(yesRenderBg);
                        l.add(mapMethod(INVOKESTATIC, C_HOOKS, "getBgTexture", "()Lnet/minecraft/class_2960;"));
                        m.instructions.insertBefore(method, l);
                    }
                }
            });
        }); // end Drawable#render
        return cn;
    }

    private Optional<MethodNode> getMethod(ClassNode cn, String name, String desc) {
        Mapping.NodeElement e = methodMapper().apply(Mapping.NodeElement.of(C_TARGET, name, desc));
        return cn.methods.stream()
                .filter(m -> Objects.equals(e.getName(), m.name) &&
                        Objects.equals(e.getDesc().getDescriptor(), m.desc))
                .findAny();
    }

    @SuppressWarnings("all")
    private FieldInsnNode mapField(int opcode, String owner, String name, String desc) {
        Mapping.NodeElement e = fieldMapper().apply(Mapping.NodeElement.of(owner, name, desc));
        return new FieldInsnNode(opcode, e.getOwner().getInternalName(), e.getName(), e.getDesc().getDescriptor());
    }

    @SuppressWarnings("all")
    private MethodInsnNode mapMethod(int opcode, String owner, String name, String desc) {
        Mapping.NodeElement e = methodMapper().apply(Mapping.NodeElement.of(owner, name, desc));
        return new MethodInsnNode(opcode, e.getOwner().getInternalName(), e.getName(), e.getDesc().getDescriptor());
    }

    private boolean sameField(FieldInsnNode n1, FieldInsnNode n2) {
        return  Objects.equals(n1.name, n2.name) &&
                Objects.equals(n1.desc, n2.desc) &&
                Objects.equals(n1.owner, n2.owner) &&
                n1.getOpcode() == n2.getOpcode();
    }

    private boolean sameMethod(MethodInsnNode n1, MethodInsnNode n2) {
        return  Objects.equals(n1.name, n2.name) &&
                Objects.equals(n1.desc, n2.desc) &&
                Objects.equals(n1.owner, n2.owner) &&
                n1.getOpcode() == n2.getOpcode();
    }
}
