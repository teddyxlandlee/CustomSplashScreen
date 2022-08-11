package eu.midnightdust.customsplashscreen.texture;

import eu.midnightdust.customsplashscreen.hook.FabricConfigDir;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class BlurredConfigTexture extends ResourceTexture {
    // Load textures from the config directory //

    public BlurredConfigTexture(Identifier location) {
        super(location);
    }

    protected TextureData loadTextureData(ResourceManager resourceManager) {
        try {
            InputStream input = Files.newInputStream(FabricConfigDir.getConfigDir().resolve("customsplashscreen")
                    .resolve(location.toString().replace("minecraft:", "").replace(':', '/')));
            TextureData texture;

            try {
                texture = new TextureData(new TextureResourceMetadata(true, true), NativeImage.read(input));
            } finally {
                input.close();
            }

            return texture;
        } catch (IOException var18) {
            return new TextureData(var18);
        }
    }

}
