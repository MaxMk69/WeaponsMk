package net.maxmk.weaponsmk.entity.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.maxmk.weaponsmk.WeaponsMk;
import net.maxmk.weaponsmk.entity.custom.NetherTridentEntity;
import net.maxmk.weaponsmk.entity.custom.NetheriteTridentEntity;
import net.maxmk.weaponsmk.item.custom.NetherTrident;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class NetherTridentModel extends EntityModel<NetherTridentEntity> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(WeaponsMk.MOD_ID, "nether_trident"), "main");
    private final ModelPart netherTrident;

    public NetherTridentModel(ModelPart root) {
        this.netherTrident = root.getChild("nether_trident");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition netheriteTrident = partdefinition.addOrReplaceChild("nether_trident", CubeListBuilder.create(), PartPose.ZERO);

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
    public void setupAnim(NetherTridentEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        netherTrident.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
