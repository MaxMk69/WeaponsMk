package net.maxmk.weaponsmk.entity.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.maxmk.weaponsmk.WeaponsMk;
import net.maxmk.weaponsmk.entity.client.model.FrostTridentModel;
import net.maxmk.weaponsmk.entity.client.model.HolyTridentModel;
import net.maxmk.weaponsmk.entity.custom.FrostTridentEntity;
import net.maxmk.weaponsmk.entity.custom.HolyTridentEntity;
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
public class HolyTridentRenderer extends EntityRenderer<HolyTridentEntity> {
    private HolyTridentModel model;

    public HolyTridentRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new HolyTridentModel(pContext.bakeLayer(HolyTridentModel.LAYER_LOCATION));
    }

    @Override
    public void render(HolyTridentEntity pEntity, float entityYaw, float partialTicks, PoseStack poseStack,
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
    public ResourceLocation getTextureLocation(HolyTridentEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(WeaponsMk.MOD_ID, "textures/item/holy_trident_3d.png");
    }
}
