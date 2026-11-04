package net.shirojr.boatism.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.shirojr.boatism.Boatism;
import net.shirojr.boatism.network.ClientReceiver;
import net.shirojr.boatism.registry.BoatismItems;

/**
 * Stage 8 experimental world-space model renderer.
 * Client-side boat NBT is not synchronized by vanilla: this only displays for
 * boats whose installation flag is visible on the client.
 */
@Mod.EventBusSubscriber(modid = Boatism.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class BoatEngineWorldRenderer {
    private static final ResourceLocation TEXTURE =
        new ResourceLocation(Boatism.MOD_ID, "textures/entity/boatengine.png");
    private static ModelPart root;
    private BoatEngineWorldRenderer() {}

    @SubscribeEvent
    public static void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        if (root == null) root = BoatEngineModel.root(mc.getEntityModels().bakeLayer(BoatEngineModel.LAYER));
        PoseStack pose = event.getPoseStack();
        var camera = event.getCamera().getPosition();
        float partial = event.getPartialTick();
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (!(entity instanceof Boat boat)) continue;
            var state = ClientReceiver.get(boat.getId());
            if (state == null || !state.getBoolean("Installed")) continue;
            pose.pushPose();
            double x = Mth.lerp(partial, boat.xOld, boat.getX()) - camera.x;
            double y = Mth.lerp(partial, boat.yOld, boat.getY()) - camera.y;
            double z = Mth.lerp(partial, boat.zOld, boat.getZ()) - camera.z;
            pose.translate(x, y, z);
            pose.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180f - Mth.rotLerp(partial, boat.yRotO, boat.getYRot())));
            // Mirror the original Boatism passenger attachment offset: the
            // engine sits at the stern, rather than rendering at boat center.
            // The world-space renderer uses the opposite local-Z sign from
            // the original passenger attachment transformation.
            float attachmentY = boat.getBbHeight() / 3.0f;
            float attachmentZ = 1.32f;
            if (boat.getVariant() == Boat.Type.BAMBOO) {
                attachmentY = boat.getBbHeight() * 0.7f;
                attachmentZ = 1.2f;
            }
            pose.translate(0.0, attachmentY, attachmentZ);
            // Match the original entity renderer's 1.5x model scale.
            pose.scale(-1.5f, -1.5f, 1.5f);
            pose.translate(0.0, -1.501, 0.0);
            ModelPart rod = root.getChild("rod");
            rod.getChild("propeller").zRot = state.getBoolean("Running") ? (boat.tickCount + partial) * 0.35f : 0f;
            root.render(pose, buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), 0xF000F0, OverlayTexture.NO_OVERLAY);

            // The original Fabric engine displays upgrades as FIXED item
            // models with per-component transforms; mirror those here.
            if (state.getBoolean("Exhaust")) {
                drawUpgrade(mc, pose, buffers, new ItemStack(BoatismItems.COMPONENT_EXHAUST.get()),
                    0.82f, 1.4, 180f, 180f, boat.getId());
            }
            if (state.getBoolean("StrappedCanister") || state.getBoolean("Canister")) {
                drawUpgrade(mc, pose, buffers, new ItemStack(BoatismItems.COMPONENT_CANISTER_STRAPPED.get()),
                    0.48f, 2.3, 90f, 180f, boat.getId());
            }
            if (state.getBoolean("Plates")) {
                drawUpgrade(mc, pose, buffers, new ItemStack(BoatismItems.COMPONENT_PLATES.get()),
                    0.5f, 2.43, 90f, 180f, boat.getId());
            }
            pose.popPose();
        }
        buffers.endBatch();
    }

    private static void drawUpgrade(Minecraft mc, PoseStack pose, MultiBufferSource.BufferSource buffers,
                                    ItemStack stack, float scale, double yOffset,
                                    float xDegrees, float yDegrees, int seed) {
        pose.pushPose();
        pose.scale(scale, scale, scale);
        pose.translate(0.0, yOffset, 0.0);
        pose.mulPose(com.mojang.math.Axis.XP.rotationDegrees(xDegrees));
        pose.mulPose(com.mojang.math.Axis.YP.rotationDegrees(yDegrees));
        mc.getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED,
            0xF000F0, OverlayTexture.NO_OVERLAY, pose, buffers, mc.level, seed);
        pose.popPose();
    }
}
