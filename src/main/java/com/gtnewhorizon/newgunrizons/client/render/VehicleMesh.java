package com.gtnewhorizon.newgunrizons.client.render;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;

/**
 * A vehicle mesh: a triangulated Wavefront OBJ (v, vt, vn, g, f a/b/c) written by tools/vehicle_designs.py, in Java
 * model space (16 units per block). Every group is compiled into its own display list on first use; a group named
 * "&lt;name&gt;_glow" is drawn full bright right after "&lt;name&gt;".
 */
public final class VehicleMesh {

    private static final Logger LOG = LogManager.getLogger(VehicleMesh.class);

    /** Lightmap coordinates restored after full-bright faces; set by the caller before drawing. */
    private static float lightX = 240.0F, lightY = 240.0F;

    private final Map<String, float[]> groups = new HashMap<>();
    private final Map<String, Integer> lists = new HashMap<>();

    private VehicleMesh() {}

    /** Loads /assets/newgunrizons/models/vehicle/&lt;name&gt;.obj, or returns null when there is none. */
    public static VehicleMesh load(String name) {
        String path = "/assets/" + NewGunrizonsMod.MODID + "/models/vehicle/" + name + ".obj";
        InputStream is = VehicleMesh.class.getResourceAsStream(path);
        if (is == null) return null;
        VehicleMesh mesh = new VehicleMesh();
        List<float[]> v = new ArrayList<>(), vt = new ArrayList<>(), vn = new ArrayList<>();
        Map<String, FloatList> data = new HashMap<>();
        FloatList cur = null;
        try (BufferedReader r = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = r.readLine()) != null) {
                if (line.isEmpty() || line.charAt(0) == '#') continue;
                String[] t = line.trim()
                    .split("\\s+");
                switch (t[0]) {
                    case "v":
                        v.add(new float[] { Float.parseFloat(t[1]), Float.parseFloat(t[2]), Float.parseFloat(t[3]) });
                        break;
                    case "vt":
                        vt.add(new float[] { Float.parseFloat(t[1]), 1.0F - Float.parseFloat(t[2]) });
                        break;
                    case "vn":
                        vn.add(new float[] { Float.parseFloat(t[1]), Float.parseFloat(t[2]), Float.parseFloat(t[3]) });
                        break;
                    case "g":
                    case "o":
                        cur = data.get(t[1]);
                        if (cur == null) data.put(t[1], cur = new FloatList());
                        break;
                    case "f":
                        if (cur == null) data.put("hull", cur = new FloatList());
                        // fan triangulation, in case a face has more than three corners
                        for (int k = 2; k + 1 <= t.length - 1; k++) {
                            corner(cur, t[1], v, vt, vn);
                            corner(cur, t[k], v, vt, vn);
                            corner(cur, t[k + 1], v, vt, vn);
                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            LOG.error("Cannot read vehicle mesh {}", path, e);
            return null;
        }
        for (Map.Entry<String, FloatList> e : data.entrySet()) mesh.groups.put(e.getKey(), e.getValue()
            .toArray());
        return mesh;
    }

    /** One corner "v/vt/vn": x y z u v nx ny nz. */
    private static void corner(FloatList out, String ref, List<float[]> v, List<float[]> vt, List<float[]> vn) {
        String[] p = ref.split("/");
        float[] pos = v.get(Integer.parseInt(p[0]) - 1);
        float[] uv = p.length > 1 && !p[1].isEmpty() ? vt.get(Integer.parseInt(p[1]) - 1) : new float[2];
        float[] n = p.length > 2 ? vn.get(Integer.parseInt(p[2]) - 1) : new float[] { 0, -1, 0 };
        out.add(pos[0], pos[1], pos[2], uv[0], uv[1], n[0], n[1], n[2]);
    }

    public static void setLight(int brightness) {
        lightX = brightness % 65536;
        lightY = brightness / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightX, lightY);
    }

    public boolean has(String group) {
        return groups.containsKey(group) || groups.containsKey(group + "_glow");
    }

    /** Draws a group (in model units) and its full-bright faces. */
    public void render(String group) {
        call(group);
        if (groups.containsKey(group + "_glow")) {
            GL11.glPushAttrib(GL11.GL_LIGHTING_BIT | GL11.GL_ENABLE_BIT);
            GL11.glDisable(GL11.GL_LIGHTING);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            call(group + "_glow");
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightX, lightY);
            GL11.glPopAttrib();
        }
    }

    private void call(String group) {
        Integer id = lists.get(group);
        if (id == null) {
            float[] d = groups.get(group);
            if (d == null) return;
            id = GLAllocation.generateDisplayLists(1);
            GL11.glNewList(id, GL11.GL_COMPILE);
            Tessellator t = Tessellator.instance;
            t.startDrawing(GL11.GL_TRIANGLES);
            for (int i = 0; i < d.length; i += 8) {
                t.setNormal(d[i + 5], d[i + 6], d[i + 7]);
                t.addVertexWithUV(d[i], d[i + 1], d[i + 2], d[i + 3], d[i + 4]);
            }
            t.draw();
            GL11.glEndList();
            lists.put(group, id);
        }
        GL11.glCallList(id);
    }

    private static final class FloatList {

        private float[] a = new float[4096];
        private int n;

        void add(float... f) {
            if (n + f.length > a.length) a = java.util.Arrays.copyOf(a, Math.max(a.length * 2, n + f.length));
            System.arraycopy(f, 0, a, n, f.length);
            n += f.length;
        }

        float[] toArray() {
            return java.util.Arrays.copyOf(a, n);
        }
    }
}
