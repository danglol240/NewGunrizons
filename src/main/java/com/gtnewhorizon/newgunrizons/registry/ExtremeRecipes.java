package com.gtnewhorizon.newgunrizons.registry;

import java.lang.reflect.Method;

import net.minecraft.item.ItemStack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Loader;

/**
 * 9x9 recipes in Avaritia's Extreme Crafting Table for the UEV-MAX technology modules. Avaritia is reached through
 * reflection so it stays an optional dependency: without it (or if its API changes) nothing is registered here and
 * the modules are made in the Munitions Factory instead.
 */
final class ExtremeRecipes {

    private static final Logger LOG = LogManager.getLogger(ExtremeRecipes.class);
    private static final String MANAGER = "fox.spiteful.avaritia.crafting.ExtremeCraftingManager";

    /**
     * Rows of the module pattern: P plates, C circuits, F field generators, E emitters, S sensors, M the previous
     * module, K the module's key ingredient, X its superconductor or exotic plate.
     */
    private static final String[] PATTERN = { "C  PPP  C", "  F X F  ", " P E S P ", "P   M   P", "PX  K  XP",
        "P   M   P", " P S E P ", "  F X F  ", "C  PPP  C" };

    private static Object manager;
    private static Method addShaped;
    private static boolean looked;

    private ExtremeRecipes() {}

    private static boolean available() {
        if (looked) return addShaped != null;
        looked = true;
        if (!Loader.isModLoaded("Avaritia")) return false;
        try {
            Class<?> cls = Class.forName(MANAGER);
            manager = cls.getMethod("getInstance")
                .invoke(null);
            try {
                addShaped = cls.getMethod("addExtremeShapedOreRecipe", ItemStack.class, Object[].class);
            } catch (NoSuchMethodException e) {
                addShaped = cls.getMethod("addRecipe", ItemStack.class, Object[].class);
            }
        } catch (Throwable t) {
            LOG.warn("Avaritia found but its Extreme Crafting API is not usable: {}", t.toString());
            addShaped = null;
        }
        return addShaped != null;
    }

    /** Registers the module's 9x9 recipe; false when the Extreme Crafting Table is not available. */
    static boolean addModule(HighTierRecipes.EndgameModule m) {
        if (!available()) return false;
        Object[] recipe = new Object[PATTERN.length + 16];
        System.arraycopy(PATTERN, 0, recipe, 0, PATTERN.length);
        int i = PATTERN.length;
        recipe[i++] = 'P';
        recipe[i++] = single(m.plate);
        recipe[i++] = 'C';
        recipe[i++] = single(m.circuit);
        recipe[i++] = 'F';
        recipe[i++] = single(m.field);
        recipe[i++] = 'E';
        recipe[i++] = single(m.emitter);
        recipe[i++] = 'S';
        recipe[i++] = single(m.sensor);
        recipe[i++] = 'M';
        recipe[i++] = single(m.previous);
        recipe[i++] = 'K';
        recipe[i++] = single(m.key);
        recipe[i++] = 'X';
        recipe[i] = single(m.extra);
        try {
            addShaped.invoke(manager, new ItemStack(m.output), recipe);
            return true;
        } catch (Throwable t) {
            LOG.warn("Could not add Extreme Crafting recipe for {}: {}", m.output, t.toString());
            return false;
        }
    }

    private static ItemStack single(ItemStack stack) {
        ItemStack c = stack.copy();
        c.stackSize = 1;
        return c;
    }
}
