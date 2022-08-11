package eu.midnightdust.customsplashscreen.hook;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import eu.midnightdust.customsplashscreen.CustomSplashScreenClient;
import eu.midnightdust.customsplashscreen.config.CustomSplashScreenConfig;
import eu.midnightdust.customsplashscreen.texture.BlurredConfigTexture;
import eu.midnightdust.customsplashscreen.texture.ConfigTexture;
import eu.midnightdust.customsplashscreen.texture.EmptyTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.BackgroundHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.function.IntSupplier;

import static eu.midnightdust.customsplashscreen.CustomSplashScreenClient.CS_CONFIG;
import static net.minecraft.client.gui.DrawableHelper.drawTexture;
import static net.minecraft.client.gui.DrawableHelper.fill;

/**
 * These hooks should be directly invoked by Minecraft code.
 */
@SuppressWarnings("unused")
public final class SplashScreenHooks {
    @SuppressWarnings("unused")
    public static void onInit(MinecraftClient client, Identifier logo) {
        if (CS_CONFIG.logoStyle == CustomSplashScreenConfig.LogoStyle.Mojang) {
            client.getTextureManager().registerTexture(logo, new BlurredConfigTexture(new Identifier(CS_CONFIG.textures.MojangLogo)));
        }
        else {
            client.getTextureManager().registerTexture(logo, new EmptyTexture(new Identifier("Iqj4fLE5TtRybrFdmk6vK/empty.png")));
        }
        client.getTextureManager().registerTexture(new Identifier(CS_CONFIG.textures.Aspect1to1Logo), new ConfigTexture(new Identifier(CS_CONFIG.textures.Aspect1to1Logo)));
        client.getTextureManager().registerTexture(new Identifier(CS_CONFIG.textures.BackgroundTexture), new ConfigTexture(new Identifier(CS_CONFIG.textures.BackgroundTexture)));

        client.getTextureManager().registerTexture(new Identifier(CS_CONFIG.textures.CustomBarTexture), new ConfigTexture(new Identifier(CS_CONFIG.textures.CustomBarTexture)));
        client.getTextureManager().registerTexture(new Identifier(CS_CONFIG.textures.CustomBarBackgroundTexture), new ConfigTexture(new Identifier(CS_CONFIG.textures.CustomBarBackgroundTexture)));
    }

    private SplashScreenHooks() {}

    private static int getBackgroundColor() {
        if (CS_CONFIG.backgroundImage) {
            return BackgroundHelper.ColorMixer.getArgb(0, 0, 0, 0);
        }
        else {
            return CS_CONFIG.backgroundColor;
        }
    }

    @SuppressWarnings("unused")
    public static IntSupplier backgroundColorSupplier() {
        return SplashScreenHooks::getBackgroundColor;
    }

    @SuppressWarnings("unused")
    public static Identifier getBgTexture() {
        return new Identifier(CS_CONFIG.textures.BackgroundTexture);
    }

    @SuppressWarnings("unused")
    public static boolean shouldRenderBg() {
        return CS_CONFIG.backgroundImage;
    }

    @SuppressWarnings("unused")
    public static void renderLogo(MinecraftClient client, MatrixStack matrices, int s /*FLOAD_11*/,
                                  Identifier defaultLogo) {
        int m = client.getWindow().getScaledWidth() >> 1;
        int u = client.getWindow().getScaledHeight() >> 1;
        double d = Math.min((double)client.getWindow().getScaledWidth() * 0.75,
                (double)client.getWindow().getScaledHeight() * 0.25);
        int v = (int)(d * .5);
        double e = d * 4;
        int w = (int)(e * .5);

        // Render the Logo
        RenderSystem.setShaderTexture(0, CS_CONFIG.logoStyle == CustomSplashScreenConfig.LogoStyle.Aspect1to1 ? new Identifier(CS_CONFIG.textures.Aspect1to1Logo) : defaultLogo);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        if (CS_CONFIG.logoStyle == CustomSplashScreenConfig.LogoStyle.Aspect1to1) {
            RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, s);
            drawTexture(matrices, m - (w / 2), v, w, w, 0, 0, 512, 512, 512, 512);
        } else if (CS_CONFIG.logoStyle == CustomSplashScreenConfig.LogoStyle.Mojang) {
            RenderSystem.blendFunc(770, 1);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, s);
            drawTexture(matrices, m - w, u - v, w, (int)d, -0.0625F, 0.0F, 120, 60, 120, 120);
            drawTexture(matrices, m, u - v, w, (int)d, 0.0625F, 60.0F, 120, 60, 120, 120);
        }

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    @SuppressWarnings("unused")
    public static void afterRenderBar(MatrixStack matrices, int x1, int y1, int x2, int y2,
                                      float progress) {
        int i = MathHelper.ceil((float)(x2 - x1 - 2) * progress);

        // Bossbar Progress Bar
        if (CustomSplashScreenClient.CS_CONFIG.progressBarType == CustomSplashScreenConfig.ProgressBarType.BossBar) {
            RenderSystem.setShaderTexture(0, new Identifier(CS_CONFIG.textures.BossBarTexture));
            RenderSystem.setShader(GameRenderer::getPositionTexShader);

            int overlay = 0;

            if (CustomSplashScreenClient.CS_CONFIG.bossBarType == CustomSplashScreenConfig.BossBarType.NOTCHED_6) {overlay = 93;}
            else if (CustomSplashScreenClient.CS_CONFIG.bossBarType == CustomSplashScreenConfig.BossBarType.NOTCHED_10) {overlay = 105;}
            else if (CustomSplashScreenClient.CS_CONFIG.bossBarType == CustomSplashScreenConfig.BossBarType.NOTCHED_12) {overlay = 117;}
            else if (CustomSplashScreenClient.CS_CONFIG.bossBarType == CustomSplashScreenConfig.BossBarType.NOTCHED_20) {overlay = 129;}

            int bbWidth = (int) ((x2 - x1+1) * 1.4f);
            int bbHeight = (y2 - y1) * 30;
            drawTexture(matrices, x1, y1 + 1, 0, 0, 0, x2 - x1, (int) ((y2-y1) / 1.4f), bbWidth, bbHeight);
            drawTexture(matrices, x1, y1 + 1, 0, 0, 5f, i, (int) ((y2 - y1) / 1.4f), bbWidth, bbHeight);

            RenderSystem.enableBlend();
            RenderSystem.blendEquation(32774);
            RenderSystem.blendFunc(770, 1);
            if (overlay != 0) {
                drawTexture(matrices, x1, y1 + 1, 0, 0, overlay, x2 - x1, (int) ((y2 - y1) / 1.4f), bbWidth, bbHeight);
            }
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
        }

        // Custom Progress Bar
        if (CustomSplashScreenClient.CS_CONFIG.progressBarType == CustomSplashScreenConfig.ProgressBarType.Custom) {
            int customWidth = CustomSplashScreenClient.CS_CONFIG.customProgressBarMode == CustomSplashScreenConfig.ProgressBarMode.Linear ? x2 - x1 : i;
            if (CS_CONFIG.customProgressBarBackground) {
                RenderSystem.setShaderTexture(0, new Identifier(CS_CONFIG.textures.CustomBarBackgroundTexture));
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                drawTexture(matrices, x1, y1, 0, 0, 6, x2 - x1, y2 - y1, 10, x2-x1);
            }
            RenderSystem.setShaderTexture(0, new Identifier(CS_CONFIG.textures.CustomBarTexture));
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            drawTexture(matrices, x1, y1, 0, 0, 6, i, y2 - y1, customWidth, 10);
        }

        // Vanilla / With Color progress bar
        if (CustomSplashScreenClient.CS_CONFIG.progressBarType == CustomSplashScreenConfig.ProgressBarType.Vanilla) {
            int k = CustomSplashScreenClient.CS_CONFIG.progressBarColor | 255 << 24;
            int kk = CustomSplashScreenClient.CS_CONFIG.progressFrameColor | 255 << 24;
            fill(matrices, x1 + 2, y1 + 2, x1 + i, y2 - 2, k);
            fill(matrices, x1 + 1, y1, x2 - 1, y1 + 1, kk);
            fill(matrices, x1 + 1, y2, x2 - 1, y2 - 1, kk);
            fill(matrices, x1, y1, x1 + 1, y2, kk);
            fill(matrices, x2, y1, x2 - 1, y2, kk);
        }
    }

}
