package net.maxmk.weaponsmk.entity.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.maxmk.weaponsmk.WeaponsMk;
import net.maxmk.weaponsmk.entity.client.model.UnifiedTridentModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UnifiedTridentRenderer<T extends Entity> extends EntityRenderer<T> {
    private final UnifiedTridentModel<T> model;
    private final String textureName;

    public UnifiedTridentRenderer(EntityRendererProvider.Context pContext, String textureName) {
        super(pContext);
        this.model = new UnifiedTridentModel<>(pContext.bakeLayer(UnifiedTridentModel.LAYER_LOCATION));
        this.textureName = textureName;
    }

    @Override
    public void render(T pEntity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, pEntity.yRotO, pEntity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, pEntity.xRotO, pEntity.getXRot()) + 90.0F));
        
        // Check if entity has isFoil method (for trident entities)
        boolean isFoil = false;
        if (pEntity instanceof net.maxmk.weaponsmk.entity.custom.SoundTridentEntity) {
            isFoil = ((net.maxmk.weaponsmk.entity.custom.SoundTridentEntity) pEntity).isFoil();
        } else if (pEntity instanceof net.maxmk.weaponsmk.entity.custom.FrostTridentEntity) {
            isFoil = ((net.maxmk.weaponsmk.entity.custom.FrostTridentEntity) pEntity).isFoil();
        } else if (pEntity instanceof net.maxmk.weaponsmk.entity.custom.NatureTridentEntity) {
            isFoil = ((net.maxmk.weaponsmk.entity.custom.NatureTridentEntity) pEntity).isFoil();
        } else if (pEntity instanceof net.maxmk.weaponsmk.entity.custom.NetherTridentEntity) {
            isFoil = ((net.maxmk.weaponsmk.entity.custom.NetherTridentEntity) pEntity).isFoil();
        } else if (pEntity instanceof net.maxmk.weaponsmk.entity.custom.ShadowTridentEntity) {
            isFoil = ((net.maxmk.weaponsmk.entity.custom.ShadowTridentEntity) pEntity).isFoil();
        } else if (pEntity instanceof net.maxmk.weaponsmk.entity.custom.NetheriteTridentEntity) {
            isFoil = ((net.maxmk.weaponsmk.entity.custom.NetheriteTridentEntity) pEntity).isFoil();
        } else if (pEntity instanceof net.maxmk.weaponsmk.entity.custom.HolyTridentEntity) {
            isFoil = ((net.maxmk.weaponsmk.entity.custom.HolyTridentEntity) pEntity).isFoil();
        } else if (pEntity instanceof net.maxmk.weaponsmk.entity.custom.WindTridentEntity) {
            isFoil = ((net.maxmk.weaponsmk.entity.custom.WindTridentEntity) pEntity).isFoil();
        } else if (pEntity instanceof net.maxmk.weaponsmk.entity.custom.EndTridentEntity) {
            isFoil = ((net.maxmk.weaponsmk.entity.custom.EndTridentEntity) pEntity).isFoil();
        }
        
        VertexConsumer vertexconsumer = ItemRenderer.getFoilBufferDirect(buffer, this.model.renderType(this.getTextureLocation(pEntity)), false, isFoil);
        this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(pEntity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return ResourceLocation.fromNamespaceAndPath(WeaponsMk.MOD_ID, "textures/item/" + textureName + "_3d.png");
    }
}
