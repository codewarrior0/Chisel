package team.chisel.common.util;

import java.util.List;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import team.chisel.api.render.IBlockRenderContext;
import team.chisel.api.render.IBlockRenderType;
import team.chisel.api.render.IChiselTexture;

import com.google.common.collect.Lists;

public class CombinedChiselTexture implements IChiselTexture {

    private class RenderType implements IBlockRenderType {

        @Override
        public IChiselTexture makeTexture(TextureAtlasSprite... sprites) {
            // TODO not sure this is right?
            return CombinedChiselTexture.this;
        }

        @Override
        public IBlockRenderContext getBlockRenderContext(IBlockAccess world, BlockPos pos) {
            return null; // TODO
        }
    }
    
    private List<IChiselTexture> texs;
    private IBlockRenderType type = new RenderType();

    public CombinedChiselTexture(List<IChiselTexture> texs){
        this.texs = texs;
    }

    public CombinedChiselTexture(){
        this.texs = Lists.newArrayList();
    }

    public void addTexture(IChiselTexture loc){
        if (!texs.contains(loc)){
            texs.add(loc);
        }
    }

    public boolean removeTexture(IChiselTexture loc){
        return texs.remove(loc);
    }

    @Override
    public List<BakedQuad> getSideQuads(EnumFacing side, IBlockRenderContext context) {
        List<BakedQuad> ret = Lists.newArrayList();
        texs.forEach(t -> ret.addAll(t.getSideQuads(side, context)));
        return ret;
    }

    @Override
    public IBlockRenderType getBlockRenderType() {
        return type;
    }

    @Override
    public TextureAtlasSprite getParticle() {
        return texs.get(0).getParticle();
    }
}
