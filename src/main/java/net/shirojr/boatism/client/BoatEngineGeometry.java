package net.shirojr.boatism.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.shirojr.boatism.Boatism;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

/**
 * Renders the same exact cuboids and per-face UV mapping as base_engine.json.
 *
 * Unlike a baked ModelPart, this preserves independently mapped Blockbench
 * faces (including the new exhaust pipe and lowered propeller). The propeller
 * group remains animated; all other JSON elements stay perfectly static.
 */
public final class BoatEngineGeometry {
    private static final ResourceLocation JSON_MODEL =
        new ResourceLocation(Boatism.MOD_ID, "models/item/base_engine.json");

    private static ResourceManager loadedManager;
    private static BoatEngineGeometry loaded;
    private final List<Quad> quads;
    private final SmokeOutlet smokeOutlet;

    private record Quad(float[][] positions, float[][] uv, float[] normal, boolean propeller) {}
    public record SmokeOutlet(float x, float y, float z) {}

    private BoatEngineGeometry(List<Quad> quads, SmokeOutlet smokeOutlet) {
        this.quads = quads;
        this.smokeOutlet = smokeOutlet;
    }

    /** Exact highest point of the element named 'smoke particle here'. */
    public SmokeOutlet smokeOutlet() {
        return smokeOutlet;
    }

    public static BoatEngineGeometry get() {
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        if (loaded == null || manager != loadedManager) {
            loaded = load(manager);
            loadedManager = manager;
        }
        return loaded;
    }

    private static float[] vector(JsonArray coordinates) {
        return new float[] {coordinates.get(0).getAsFloat(), coordinates.get(1).getAsFloat(),
            coordinates.get(2).getAsFloat()};
    }

    private static void collectPropellerIndices(JsonElement node, Set<Integer> indices) {
        if (node.isJsonArray()) {
            for (JsonElement child : node.getAsJsonArray()) collectPropellerIndices(child, indices);
        } else if (node.isJsonObject()) {
            JsonObject group = node.getAsJsonObject();
            if ("propeller".equals(group.has("name") ? group.get("name").getAsString() : "")) {
                for (JsonElement child : group.getAsJsonArray("children")) {
                    if (child.isJsonPrimitive()) indices.add(child.getAsInt());
                }
            } else if (group.has("children")) {
                collectPropellerIndices(group.get("children"), indices);
            }
        }
    }

    private static BoatEngineGeometry load(ResourceManager manager) {
        try (var stream = manager.getResource(JSON_MODEL).orElseThrow().open();
             var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            JsonObject model = com.google.gson.JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray elementList = model.getAsJsonArray("elements");
            JsonArray dimensions = model.getAsJsonArray("texture_size");
            float uvWidth = dimensions.get(0).getAsFloat();
            float uvHeight = dimensions.get(1).getAsFloat();
            Set<Integer> propellerIndices = new HashSet<>();
            if (model.has("groups")) collectPropellerIndices(model.get("groups"), propellerIndices);

            List<Quad> faces = new ArrayList<>();
            String[] directions = {"north", "south", "west", "east", "up", "down"};
            float[][] normals = {{0, 0, -1}, {0, 0, 1}, {-1, 0, 0}, {1, 0, 0},
                {0, 1, 0}, {0, -1, 0}};
            SmokeOutlet outlet = null;

            for (int elementIndex = 0; elementIndex < elementList.size(); elementIndex++) {
                JsonObject element = elementList.get(elementIndex).getAsJsonObject();
                float[] from = vector(element.getAsJsonArray("from"));
                float[] to = vector(element.getAsJsonArray("to"));
                float x0 = from[0], y0 = from[1], z0 = from[2];
                float x1 = to[0], y1 = to[1], z1 = to[2];

                float[][] corners = {
                    {x0, y0, z0}, {x1, y0, z0}, {x1, y1, z0}, {x0, y1, z0},
                    {x0, y0, z1}, {x1, y0, z1}, {x1, y1, z1}, {x0, y1, z1}
                };
                int[][] facesByCorner = {
                    {0, 3, 2, 1}, // north
                    {5, 6, 7, 4}, // south
                    {4, 7, 3, 0}, // west
                    {1, 2, 6, 5}, // east
                    {3, 7, 6, 2}, // up
                    {4, 0, 1, 5}  // down
                };
                JsonObject rotation = element.has("rotation")
                    ? element.getAsJsonObject("rotation") : null;
                if (element.has("name")
                    && "smoke particle here".equals(element.get("name").getAsString())) {
                    float[] highest = rotateElement(new float[] {
                        (x0 + x1) / 2f, y1, (z0 + z1) / 2f
                    }, rotation);
                    outlet = new SmokeOutlet(highest[0], highest[1], highest[2]);
                }
                JsonObject texFaces = element.getAsJsonObject("faces");
                for (int faceId = 0; faceId < directions.length; faceId++) {
                    JsonObject face = texFaces.getAsJsonObject(directions[faceId]);
                    if (face == null) continue;
                    JsonArray uv = face.getAsJsonArray("uv");
                    float u0 = uv.get(0).getAsFloat() / uvWidth;
                    float v0 = uv.get(1).getAsFloat() / uvHeight;
                    float u1 = uv.get(2).getAsFloat() / uvWidth;
                    float v1 = uv.get(3).getAsFloat() / uvHeight;
                    float[][] faceUvs = {{u0, v1}, {u0, v0}, {u1, v0}, {u1, v1}};
                    int uvRotations = face.has("rotation") ? face.get("rotation").getAsInt() / 90 : 0;
                    float[][] positions = new float[4][3];
                    for (int i = 0; i < 4; i++) {
                        float[] point = corners[facesByCorner[faceId][i]];
                        positions[i] = rotateElement(point, rotation);
                    }
                    float[][] orderedUv = new float[4][2];
                    for (int i = 0; i < 4; i++) {
                        orderedUv[i] = faceUvs[Math.floorMod(i - uvRotations, 4)];
                    }
                    faces.add(new Quad(positions, orderedUv, normals[faceId],
                        propellerIndices.contains(elementIndex)));
                }
            }
            return new BoatEngineGeometry(faces, outlet);
        } catch (IOException e) {
            throw new IllegalStateException("Could not load custom Boatism engine model: " + JSON_MODEL, e);
        }
    }

    private static float[] rotateElement(float[] point, JsonObject rotation) {
        float x = point[0], y = point[1], z = point[2];
        if (rotation == null) return new float[] {x, y, z};
        float degrees = rotation.get("angle").getAsFloat();
        if (degrees == 0) return new float[] {x, y, z};
        float[] origin = vector(rotation.getAsJsonArray("origin"));
        float radians = (float) Math.toRadians(degrees);
        float sin = (float) Math.sin(radians);
        float cos = (float) Math.cos(radians);
        x -= origin[0]; y -= origin[1]; z -= origin[2];
        switch (rotation.get("axis").getAsString()) {
            case "x" -> {float ny = y*cos-z*sin; z = y*sin+z*cos; y = ny;}
            case "y" -> {float nx = x*cos+z*sin; z = -x*sin+z*cos; x = nx;}
            case "z" -> {float nx = x*cos-y*sin; y = x*sin+y*cos; x = nx;}
            default -> throw new IllegalArgumentException("Unexpected axis in Boatism model");
        }
        return new float[] {x+origin[0], y+origin[1], z+origin[2]};
    }

    public void render(PoseStack pose, VertexConsumer consumer, float propellerAngle) {
        Matrix4f transform = pose.last().pose();
        Matrix3f normals = pose.last().normal();
        float spinSin = (float) Math.sin(propellerAngle);
        float spinCos = (float) Math.cos(propellerAngle);

        for (Quad quad : quads) {
            for (int i = 0; i < 4; i++) {
                float[] p = quad.positions()[i];
                float x = p[0], y = p[1], z = p[2];
                if (quad.propeller()) {
                    // User's lowered propeller group pivot, in model pixels.
                    float centerX = 8f, centerY = -4f;
                    float dx = x - centerX, dy = y - centerY;
                    x = centerX + dx * spinCos - dy * spinSin;
                    y = centerY + dx * spinSin + dy * spinCos;
                }
                // Item-model cuboids are in 0..16 pixel coordinates. Centre
                // horizontally, but keep y=0 as the boat transom/waterline.
                x = (x - 8f) / 16f;
                y /= 16f;
                z = (z - 8f) / 16f;
                consumer.vertex(transform, x, y, z)
                    .color(255, 255, 255, 255)
                    .uv(quad.uv()[i][0], quad.uv()[i][1])
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(0xF000F0)
                    .normal(normals, quad.normal()[0], quad.normal()[1], quad.normal()[2])
                    .endVertex();
            }
        }
    }
}
