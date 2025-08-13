package net.maxmk.weaponsmk.entity.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.maxmk.weaponsmk.WeaponsMk;
import net.maxmk.weaponsmk.entity.custom.FrostTridentEntity;
import net.maxmk.weaponsmk.entity.custom.HolyTridentEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

public class HolyTridentModel extends EntityModel<HolyTridentEntity> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(WeaponsMk.MOD_ID, "holy_trident"), "main");
    private final ModelPart holyTrident;

    public HolyTridentModel(ModelPart root) {
        this.holyTrident = root.getChild("holy_trident");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition holyTrident = partdefinition.addOrReplaceChild("holy_trident", CubeListBuilder.create(), PartPose.ZERO);

        PartDefinition pole = holyTrident.addOrReplaceChild("pole",
                CubeListBuilder.create().texOffs(0, 6).addBox(-0.5F, 2.0F, -0.5F, 1.0F, 25.0F, 1.0F), PartPose.ZERO);

        PartDefinition base = holyTrident.addOrReplaceChild("base",
                CubeListBuilder.create().texOffs(4, 0).addBox(-1.5F, 0.0F, -0.5F, 3.0F, 2.0F, 1.0F), PartPose.ZERO);

        PartDefinition left_spike = holyTrident.addOrReplaceChild("left_spike",
                CubeListBuilder.create().texOffs(4, 3).addBox(-2.5F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F), PartPose.ZERO);

        PartDefinition middle_spike = holyTrident.addOrReplaceChild("middle_spike",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -4.0F, -0.5F, 1.0F, 4.0F, 1.0F), PartPose.ZERO);

        PartDefinition right_spike = holyTrident.addOrReplaceChild("right_spike",
                CubeListBuilder.create().texOffs(4, 3).mirror().addBox(1.5F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F), PartPose.ZERO);

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(HolyTridentEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        holyTrident.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
