package net.shirojr.boatism.client;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

/**
 * Forge/Mojmap reconstruction of the original Fabric BoatEngineEntityModel geometry.
 * Source: reference/fabric-decompiled/.../BoatEngineEntityModel.java (MIT).
 * Geometry only: entity binding, rendering, animation and sync are not yet implemented.
 */
public final class BoatEngineModel {
    public static final ModelLayerLocation LAYER =
        new ModelLayerLocation(new ResourceLocation("boatism", "boat_engine"), "main");

    private BoatEngineModel() {}

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition base = mesh.getRoot();
        PartDefinition root = base.addOrReplaceChild("root", CubeListBuilder.create(),
            PartPose.offset(0.0F, 23.0F, -5.0F));
        root.addOrReplaceChild("top", CubeListBuilder.create()
            .texOffs(0, 0).addBox(-3, -7, -4, 6, 3, 9, CubeDeformation.NONE)
            .texOffs(11, 16).addBox(-1, -4, -4, 2, 1, 8, CubeDeformation.NONE)
            .texOffs(0, 14).addBox(-2, -6, -5, 4, 4, 5, CubeDeformation.NONE)
            .texOffs(24, 18).addBox(-2, -9, 5, 1, 4, 1, CubeDeformation.NONE),
            PartPose.offset(0, 2, 5));
        PartDefinition rod = root.addOrReplaceChild("rod", CubeListBuilder.create()
            .texOffs(0, 0).addBox(-1, -2, -5, 2, 7, 2, CubeDeformation.NONE),
            PartPose.offset(0, 2, 5));
        PartDefinition propeller = rod.addOrReplaceChild("propeller", CubeListBuilder.create()
            .texOffs(0, 10).addBox(0, -3, -1, 0, 6, 2, CubeDeformation.NONE),
            PartPose.offsetAndRotation(0, 3, -2, 0, 0, 0.7854F));
        propeller.addOrReplaceChild("cube_r1", CubeListBuilder.create()
            .texOffs(0, 10).addBox(0, -3, 0, 0, 6, 2, CubeDeformation.NONE),
            PartPose.offsetAndRotation(0, 0, -1, 0, 0, 1.5708F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    public static ModelPart root(ModelPart baked) {
        return baked.getChild("root");
    }
}
