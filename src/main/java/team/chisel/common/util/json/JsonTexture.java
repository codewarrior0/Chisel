package team.chisel.common.util.json;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import team.chisel.api.render.IBlockRenderType;
import team.chisel.api.render.IChiselTexture;
import team.chisel.common.init.TextureTypeRegistry;
import team.chisel.common.util.CombinedChiselTexture;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

/**
 * Raw version of IChiselTexture
 */
public class JsonTexture extends JsonObjectBase<IChiselTexture, TextureMap> {

    /**
     * The String for the type of texture
     */
    private String type;

    /**
     * The Actual path to the different png textures Is an Array because some texture types need more than one For example in CTM the first one is the plain block texture and the second is the special
     * ctm png
     */
    private String[] textures;

    /**
     * If this is the type COMBINED then these are the identifiers of the child textures
     */
    private String[] children;

    @Override
    public IChiselTexture create(TextureMap map) {

        if (isCombined()) {
            Preconditions.checkNotNull(textures, "COMBINED texture type can not have any textures!");
            Preconditions.checkNotNull(children, "COMBINED texture type must have children textures!");

            List<IChiselTexture> combined = Lists.newArrayList();
            IResourceManager man = Minecraft.getMinecraft().getResourceManager();
            for (String child : children) {
                ResourceLocation loc = new ResourceLocation(child);
                try {
                    IResource res = man.getResource(loc);
                    combined.add(new Gson().fromJson(new InputStreamReader(res.getInputStream()), getClass()).create(map));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return new CombinedChiselTexture(combined);
        } else {
            Preconditions.checkNotNull(textures, "Texture must have at least one texture!");
            Preconditions.checkArgument(children != null, "Non Combined texture cannot have children!");
            Preconditions.checkArgument(TextureTypeRegistry.isValid(this.type), "Texture Type " + this.type + " is not valid");

            TextureAtlasSprite[] sprites = new TextureAtlasSprite[this.textures.length];
            for (int i = 0; i < this.textures.length; i++) {
                String tex = this.textures[i];
                sprites[i] = map.registerSprite(new ResourceLocation(tex));
            }

            IBlockRenderType type = TextureTypeRegistry.getType(this.type);
            IChiselTexture tex = type.makeTexture(sprites);
            return tex;
        }
    }

    public boolean isCombined() {
        return this.type.equalsIgnoreCase("COMBINED");
    }
}
