package net.maxmk.weaponsmk.entity.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.maxmk.weaponsmk.WeaponsMk;
import net.maxmk.weaponsmk.entity.custom.NetheriteTridentEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class NetheriteTridentModel extends EntityModel<NetheriteTridentEntity> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(WeaponsMk.MOD_ID, "netherite_trident"), "main");
    private final ModelPart netheriteTrident;

    public NetheriteTridentModel(ModelPart root) {
        this.netheriteTrident = root.getChild("netherite_trident");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition netheriteTrident = partdefinition.addOrReplaceChild("netherite_trident", CubeListBuilder.create(), PartPose.ZERO);

        PartDefinition pole = netheriteTrident.addOrReplaceChild("pole",
                CubeListBuilder.create().texOffs(0, 6).addBox(-0.5F, 2.0F, -0.5F, 1.0F, 25.0F, 1.0F), PartPose.ZERO);

        PartDefinition base = netheriteTrident.addOrReplaceChild("base",
                CubeListBuilder.create().texOffs(4, 0).addBox(-1.5F, 0.0F, -0.5F, 3.0F, 2.0F, 1.0F), PartPose.ZERO);

        PartDefinition left_spike = netheriteTrident.addOrReplaceChild("left_spike",
                CubeListBuilder.create().texOffs(4, 3).addBox(-2.5F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F), PartPose.ZERO);

        PartDefinition middle_spike = netheriteTrident.addOrReplaceChild("middle_spike",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -4.0F, -0.5F, 1.0F, 4.0F, 1.0F), PartPose.ZERO);

        PartDefinition right_spike = netheriteTrident.addOrReplaceChild("right_spike",
                CubeListBuilder.create().texOffs(4, 3).mirror().addBox(1.5F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F), PartPose.ZERO);

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(NetheriteTridentEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        netheriteTrident.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
