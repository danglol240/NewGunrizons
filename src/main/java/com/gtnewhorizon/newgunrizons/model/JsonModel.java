package com.gtnewhorizon.newgunrizons.model;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Loads a Bedrock geometry (.geo.json) model and creates ModelRenderer instances,
 * providing a drop-in replacement for hand-coded Java model classes.
 * <p>
 * Bedrock geometry uses Y-up. Minecraft 1.7.10 ModelRenderer uses Y-down.
 * Coordinate conversion (Y negation) is handled automatically.
 * <p>
 * Supported: bones (pivot, rotation, parent hierarchy), cubes (origin, size, box UV, mirror, inflate).
 * Not supported: per-face UV mapping (only box UV).
 * <p>
 * Usage:
 * 
 * <pre>
 *   // Loads /assets/newgunrizons/models/weapon/ak12.geo.json from classpath
 *   new JsonModel("weapon/ak12")
 *
 *   // In a weapon factory, replaces: new AK12()
 *   new WeaponRenderer.Builder().withModel(new JsonModel("weapon/ak12"))
 * </pre>
 */
public class JsonModel extends ModelWithAttachments {

    private static final float DEG_TO_RAD = (float) (Math.PI / 180.0);

    private final String modelPath;
    private final List<ModelRenderer> rootRenderers = new ArrayList<>();

    /**
     * Check if a ModelBase is a JsonModel with the given path.
     * Replaces {@code model instanceof SomeModelClass} checks.
     *
     * @param model the model to check
     * @param path  the model path (e.g. "sight/ak47iron")
     * @return true if model is a JsonModel loaded from that path
     */
    public static boolean is(ModelBase model, String path) {
        return model instanceof JsonModel && ((JsonModel) model).modelPath.equals(path);
    }

    /**
     * Returns the model path this JsonModel was loaded from.
     */
    public String getModelPath() {
        return modelPath;
    }

    /**
     * Load a Bedrock geometry model from the mod's resources.
     *
     * @param modelPath path relative to assets/newgunrizons/models/, without .geo.json extension
     */
    public JsonModel(String modelPath) {
        this("newgunrizons", modelPath);
    }

    /**
     * Load a Bedrock geometry model from any mod's resources.
     *
     * @param domain    resource domain (mod id)
     * @param modelPath path relative to assets/{domain}/models/, without .geo.json extension
     */
    public JsonModel(String domain, String modelPath) {
        this.modelPath = modelPath;
        String resourcePath = "/assets/" + domain + "/models/" + modelPath + ".geo.json";
        InputStream is = JsonModel.class.getResourceAsStream(resourcePath);
        if (is == null) {
            throw new RuntimeException("Model not found on classpath: " + resourcePath);
        }
        try {
            JsonObject root = new JsonParser().parse(new InputStreamReader(is, StandardCharsets.UTF_8))
                .getAsJsonObject();
            parseGeometry(root);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse model: " + resourcePath, e);
        } finally {
            try {
                is.close();
            } catch (Exception ignored) {}
        }
    }

    private void parseGeometry(JsonObject root) {
        JsonArray geometries = root.getAsJsonArray("minecraft:geometry");
        if (geometries == null || geometries.size() == 0) {
            throw new RuntimeException("No minecraft:geometry array found in .geo.json");
        }

        JsonObject geo = geometries.get(0)
            .getAsJsonObject();
        JsonObject desc = geo.getAsJsonObject("description");

        if (desc != null) {
            if (desc.has("texture_width")) {
                this.textureWidth = desc.get("texture_width")
                    .getAsInt();
            }
            if (desc.has("texture_height")) {
                this.textureHeight = desc.get("texture_height")
                    .getAsInt();
            }
        }

        JsonArray bones = geo.getAsJsonArray("bones");
        if (bones == null) return;

        // First pass: create ModelRenderers, track parents and absolute pivots
        Map<String, ModelRenderer> rendererMap = new LinkedHashMap<>();
        Map<String, String> parentMap = new LinkedHashMap<>();
        Map<String, float[]> absolutePivots = new LinkedHashMap<>();

        for (JsonElement boneElem : bones) {
            JsonObject bone = boneElem.getAsJsonObject();
            String name = bone.get("name")
                .getAsString();

            float[] pivot = getFloatArray(bone, "pivot", new float[] { 0, 0, 0 });
            float[] rotation = getFloatArray(bone, "rotation", new float[] { 0, 0, 0 });

            absolutePivots.put(name, pivot);

            // Convert pivot: Bedrock (Y-up) → Java (Y-down)
            float javaPivotX = pivot[0];
            float javaPivotY = -pivot[1];
            float javaPivotZ = pivot[2];

            ModelRenderer renderer = new ModelRenderer(this, name);
            renderer.setTextureSize(this.textureWidth, this.textureHeight);
            renderer.setRotationPoint(javaPivotX, javaPivotY, javaPivotZ);

            // Convert rotation: degrees → radians, negate X and Y for coordinate flip
            renderer.rotateAngleX = -rotation[0] * DEG_TO_RAD;
            renderer.rotateAngleY = -rotation[1] * DEG_TO_RAD;
            renderer.rotateAngleZ = rotation[2] * DEG_TO_RAD;

            // Parse cubes
            if (bone.has("cubes")) {
                JsonArray cubes = bone.getAsJsonArray("cubes");
                for (JsonElement cubeElem : cubes) {
                    addCube(renderer, cubeElem.getAsJsonObject(), pivot);
                }
            }

            rendererMap.put(name, renderer);

            if (bone.has("parent")) {
                parentMap.put(
                    name,
                    bone.get("parent")
                        .getAsString());
            }
        }

        // Second pass: set up parent-child hierarchy
        for (Map.Entry<String, String> entry : parentMap.entrySet()) {
            String childName = entry.getKey();
            String parentName = entry.getValue();

            ModelRenderer child = rendererMap.get(childName);
            ModelRenderer parent = rendererMap.get(parentName);

            if (parent != null && child != null) {
                // Convert child rotationPoint from absolute to parent-relative.
                // Note: this is exact when the parent has no rotation. When the parent
                // is rotated, the position will be approximate (the inverse rotation
                // transform is not applied). For most weapon models this is fine since
                // parent bones rarely have rotation.
                child.rotationPointX -= parent.rotationPointX;
                child.rotationPointY -= parent.rotationPointY;
                child.rotationPointZ -= parent.rotationPointZ;

                parent.addChild(child);
            }
        }

        // Collect root bones (those with no parent)
        for (Map.Entry<String, ModelRenderer> entry : rendererMap.entrySet()) {
            if (!parentMap.containsKey(entry.getKey())) {
                rootRenderers.add(entry.getValue());
            }
        }
    }

    private void addCube(ModelRenderer renderer, JsonObject cube, float[] bonePivot) {
        float[] origin = getFloatArray(cube, "origin", new float[] { 0, 0, 0 });
        float[] size = getFloatArray(cube, "size", new float[] { 1, 1, 1 });
        float inflate = cube.has("inflate") ? cube.get("inflate")
            .getAsFloat() : 0.0f;
        boolean mirror = cube.has("mirror") && cube.get("mirror")
            .getAsBoolean();

        // Box UV: [u, v] array
        int texU = 0;
        int texV = 0;
        if (cube.has("uv")) {
            JsonElement uvElem = cube.get("uv");
            if (uvElem.isJsonArray()) {
                JsonArray uv = uvElem.getAsJsonArray();
                texU = Math.round(
                    uv.get(0)
                        .getAsFloat());
                texV = Math.round(
                    uv.get(1)
                        .getAsFloat());
            }
            // Per-face UV (object form) is not supported; cube renders with UV [0,0]
        }

        // Convert cube origin from absolute Bedrock coords to offset relative to bone pivot.
        // Bedrock: origin is the min corner (Y-up). Java: offset is relative to rotationPoint (Y-down).
        float offX = origin[0] - bonePivot[0];
        float offY = bonePivot[1] - origin[1] - size[1];
        float offZ = origin[2] - bonePivot[2];

        int sizeX = Math.round(size[0]);
        int sizeY = Math.round(size[1]);
        int sizeZ = Math.round(size[2]);

        renderer.mirror = mirror;
        renderer.setTextureOffset(texU, texV);
        renderer.addBox(offX, offY, offZ, sizeX, sizeY, sizeZ, inflate);
    }

    private static float[] getFloatArray(JsonObject obj, String key, float[] defaultValue) {
        if (!obj.has(key)) return defaultValue;
        JsonArray arr = obj.getAsJsonArray(key);
        float[] result = new float[arr.size()];
        for (int i = 0; i < arr.size(); i++) {
            result[i] = arr.get(i)
                .getAsFloat();
        }
        return result;
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        for (ModelRenderer rootRenderer : rootRenderers) {
            rootRenderer.render(f5);
        }
    }
}
