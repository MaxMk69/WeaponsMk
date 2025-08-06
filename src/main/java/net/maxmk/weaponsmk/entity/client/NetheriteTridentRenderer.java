package net.maxmk.weaponsmk.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.maxmk.weaponsmk.WeaponsMk;
import net.maxmk.weaponsmk.entity.custom.NetheriteTridentEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class NetheriteTridentRenderer extends EntityRenderer<NetheriteTridentEntity> {
        private final NetheriteTridentModel model;

    public NetheriteTridentRenderer(EntityRendererProvider.Context pContext) {
            super(pContext);
            this.model = new NetheriteTridentModel(pContext.bakeLayer(NetheriteTridentModel.LAYER_LOCATION));
        }

    @Override
    public ResourceLocation getTextureLocation(NetheriteTridentEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(WeaponsMk.MOD_ID, "textures/entity/netherite_trident.png");
    }

    public void render(NetheriteTridentEntity pEntity, float pEntityYaw, float pPartialTicks,
                       PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
            pPoseStack.pushPose();
            pPoseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.yRotO, pEntity.getYRot()) - 90.0F));
            pPoseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot()) + 90.0F));
            VertexConsumer vertexconsumer = ItemRenderer.getFoilBufferDirect(pBuffer,
                    this.model.renderType(this.getTextureLocation(pEntity)), false, pEntity.isFoil());
            this.model.renderToBuffer(pPoseStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY);
            pPoseStack.popPose();
            super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
        }
}
