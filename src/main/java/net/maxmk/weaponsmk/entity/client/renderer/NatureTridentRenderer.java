package net.maxmk.weaponsmk.entity.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.maxmk.weaponsmk.WeaponsMk;
import net.maxmk.weaponsmk.entity.client.model.NatureTridentModel;
import net.maxmk.weaponsmk.entity.client.model.NetherTridentModel;
import net.maxmk.weaponsmk.entity.custom.NatureTridentEntity;
import net.maxmk.weaponsmk.entity.custom.NetherTridentEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NatureTridentRenderer extends EntityRenderer<NatureTridentEntity> {
    private NatureTridentModel model;

    public NatureTridentRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new NatureTridentModel(pContext.bakeLayer(NatureTridentModel.LAYER_LOCATION));
    }

    @Override
    public void render(NatureTridentEntity pEntity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, pEntity.yRotO, pEntity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, pEntity.xRotO, pEntity.getXRot()) + 90.0F));
        VertexConsumer vertexconsumer = ItemRenderer.getFoilBufferDirect(buffer, this.model.renderType(this.getTextureLocation(pEntity)), false, pEntity.isFoil());
        this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(pEntity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(NatureTridentEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(WeaponsMk.MOD_ID, "textures/item/nature_trident_3d.png");
    }
}
