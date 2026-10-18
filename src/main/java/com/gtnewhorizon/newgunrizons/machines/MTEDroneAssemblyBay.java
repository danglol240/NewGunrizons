package com.gtnewhorizon.newgunrizons.machines;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.isAir;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.ExoticEnergy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.chainAllGlasses;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.io.ByteArrayDataInput;
import com.gtnewhorizon.newgunrizons.blocks.NGBlocks;
import com.gtnewhorizon.newgunrizons.client.render.DroneRenderer;
import com.gtnewhorizon.newgunrizons.items.ItemDrone;
import com.gtnewhorizon.newgunrizons.registry.DroneRecipeMap;
import com.gtnewhorizon.newgunrizons.registry.Drones;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import gregtech.api.casing.Casings;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.ICasingTextureProvider;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
import gregtech.common.render.IMTERenderer;
import io.netty.buffer.ByteBuf;

/**
 * Drone Assembly Complex: a tower on a round apron where every NewGunrizons drone is built, growing with each era.
 * <ul>
 * <li>Base: a 5x5, 3 high hangar (the tower's ground floor, glass windows, empty 3x3 inside) on a round platform of
 * radius 3, all Solid Steel Machine Casings; the controller is in the middle of the hangar's front wall.</li>
 * <li>Each tier adds one storey on top of the tower (corners and the band above of that tier's Assembly Frame, glass
 * between) and one landing ring around it on the ground: that tier's Landing Pads at radius 4 + 2 x tier, with an
 * Assembly Frame beacon at each of the four compass points. Frames and pads are made of the tier's material.</li>
 * <li>The number of consecutive storeys (LV, MV, ... MAX) is the highest drone tier it can build.</li>
 * </ul>
 * While it works, the drone being built lifts off inside the hangar and a drone of every built tier circles its ring.
 */
public class MTEDroneAssemblyBay extends MTEExtendedPowerMultiBlockBase<MTEDroneAssemblyBay>
    implements ISurvivalConstructable, ICasingTextureProvider, IMTERenderer {

    private static final String BASE = "base";
    private static final String TIER = "tier";
    private static final int MAX_TIERS = 14;
    private static final int MIN_CASINGS = 30;
    private static final int TOWER_HALF = 2;
    /** The tower centre is this far behind the controller. */
    private static final int CENTER_DEPTH = 2;
    private static final Casings CASING = Casings.SolidSteelMachineCasing;

    private int casingAmount;
    private int tiers;

    // client-side render state
    private int renderTiers;
    private int renderItemId = -1;

    /** Shape grid built in tower-centre coordinates (x right, y up, z back). */
    private static final class Grid {

        private final List<int[]> cells = new ArrayList<>();
        private final List<Character> chars = new ArrayList<>();

        void set(int x, int y, int z, char c) {
            for (int i = 0; i < cells.size(); i++) {
                int[] p = cells.get(i);
                if (p[0] == x && p[1] == y && p[2] == z) {
                    chars.set(i, c);
                    return;
                }
            }
            cells.add(new int[] { x, y, z });
            chars.add(c);
        }

        private int min(int axis) {
            int m = Integer.MAX_VALUE;
            for (int[] p : cells) m = Math.min(m, p[axis]);
            return Math.min(m, axis == 2 ? -CENTER_DEPTH : 0);
        }

        private int max(int axis) {
            int m = Integer.MIN_VALUE;
            for (int[] p : cells) m = Math.max(m, p[axis]);
            return Math.max(m, 0);
        }

        /** StructureLib shape: [depth from the front][row from the top] = string from left to right. */
        String[][] shape() {
            int x0 = min(0), x1 = max(0), y0 = min(1), y1 = max(1), z0 = min(2), z1 = max(2);
            char[][][] g = new char[z1 - z0 + 1][y1 - y0 + 1][x1 - x0 + 1];
            for (char[][] slice : g) for (char[] row : slice) java.util.Arrays.fill(row, ' ');
            for (int i = 0; i < cells.size(); i++) {
                int[] p = cells.get(i);
                g[p[2] - z0][y1 - p[1]][p[0] - x0] = chars.get(i);
            }
            String[][] out = new String[g.length][];
            for (int z = 0; z < g.length; z++) {
                out[z] = new String[g[z].length];
                for (int y = 0; y < g[z].length; y++) out[z][y] = new String(g[z][y]);
            }
            return out;
        }

        /** Grid position of the controller, which sits at (0, 0, -CENTER_DEPTH). */
        int[] offset() {
            return new int[] { -min(0), max(1), -CENTER_DEPTH - min(2) };
        }
    }

    private static boolean onPerimeter(int x, int z) {
        return Math.max(Math.abs(x), Math.abs(z)) == TOWER_HALF;
    }

    private static boolean corner(int x, int z) {
        return Math.abs(x) == TOWER_HALF && Math.abs(z) == TOWER_HALF;
    }

    /** Structure key of the landing pads of a tier's ring (generated shapes, so any char works). */
    private static char padChar(int tier) {
        return (char) (0x100 + tier);
    }

    static int ringRadius(int tier) {
        return 4 + 2 * tier;
    }

    private static Grid baseGrid() {
        Grid g = new Grid();
        for (int x = -3; x <= 3; x++) for (int z = -3; z <= 3; z++) {
            if (x * x + z * z <= 12) g.set(x, -1, z, 'C'); // round platform
        }
        for (int y = 0; y <= 2; y++)
            for (int x = -TOWER_HALF; x <= TOWER_HALF; x++) for (int z = -TOWER_HALF; z <= TOWER_HALF; z++) {
                if (!onPerimeter(x, z)) g.set(x, y, z, '-'); // hangar
                else if (y == 1 && !corner(x, z)) g.set(x, y, z, 'G'); // windows
                else g.set(x, y, z, 'C');
            }
        g.set(0, 0, -CENTER_DEPTH, '~');
        return g;
    }

    private static Grid tierGrid(int tier) {
        Grid g = new Grid();
        char t = MTEMunitionsFactory.segmentChar(tier);
        int y = 3 + 2 * (tier - 1);
        for (int x = -TOWER_HALF; x <= TOWER_HALF; x++) for (int z = -TOWER_HALF; z <= TOWER_HALF; z++) {
            if (!onPerimeter(x, z)) continue;
            g.set(x, y, z, corner(x, z) ? t : 'G'); // storey: tier pillars, glass between
            g.set(x, y + 1, z, t); // band
        }
        int r = ringRadius(tier);
        char pad = padChar(tier);
        for (int x = -r - 1; x <= r + 1; x++) for (int z = -r - 1; z <= r + 1; z++) {
            if (Math.round(Math.sqrt(x * x + z * z)) == r) g.set(x, -1, z, pad);
        }
        g.set(r, -1, 0, t);
        g.set(-r, -1, 0, t);
        g.set(0, -1, r, t);
        g.set(0, -1, -r, t);
        return g;
    }

    private static final int[][] OFFSETS = new int[MAX_TIERS + 1][];
    private static final IStructureDefinition<MTEDroneAssemblyBay> STRUCTURE = buildStructure();

    private static IStructureDefinition<MTEDroneAssemblyBay> buildStructure() {
        StructureDefinition.Builder<MTEDroneAssemblyBay> b = StructureDefinition.<MTEDroneAssemblyBay>builder();
        Grid base = baseGrid();
        b.addShape(BASE, base.shape());
        OFFSETS[0] = base.offset();
        for (int tier = 1; tier <= MAX_TIERS; tier++) {
            Grid g = tierGrid(tier);
            b.addShape(TIER + tier, g.shape());
            OFFSETS[tier] = g.offset();
            b.addElement(MTEMunitionsFactory.segmentChar(tier), MTEMunitionsFactory.frameOrCasing(tier));
            b.addElement(padChar(tier), ofBlock(NGBlocks.LandingPad, tier - 1));
        }
        return b.addElement('G', chainAllGlasses())
            .addElement('-', isAir())
            .addElement(
                'C',
                buildHatchAdder(MTEDroneAssemblyBay.class)
                    .atLeast(InputBus, InputHatch, OutputBus, Maintenance, Energy.or(ExoticEnergy))
                    .casingIndex(CASING.textureId)
                    .hint(1)
                    .buildAndChain(onElementPass(m -> ++m.casingAmount, CASING.asElement())))
            .build();
    }

    public MTEDroneAssemblyBay(int id, String name, String nameRegional) {
        super(id, name, nameRegional);
    }

    public MTEDroneAssemblyBay(String name) {
        super(name);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new MTEDroneAssemblyBay(this.mName);
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType("Drone Assembly Complex")
            .addInfo("Builds every NewGunrizons combat drone, from the LV Scout to the MAX Sentinel")
            .addInfo("A tower on a round platform that grows with every era:")
            .addInfo("each tier adds one storey to the tower and one landing ring around it")
            .addInfo("The number of consecutive storeys (LV, MV, ...) is the highest drone tier it builds")
            .addInfo("Drones leave with an empty battery: charge them in a Battery Buffer of their tier")
            .addSeparator()
            .addInfo("Base: 5x5x3 hangar of Solid Steel Machine Casings, glass windows in the middle row,")
            .addInfo("empty 3x3 inside, on a round Solid Steel platform of radius 3; controller front middle")
            .addInfo("Storey N: 5x5 ring on top of the tower, tier-N Assembly Frame corners and glass, then")
            .addInfo("a full band of tier-N Assembly Frames (the tier's Machine Casing also works)")
            .addInfo("Ring N: tier-N Landing Pads on the ground at radius 4 + 2N around the tower centre,")
            .addInfo("with a tier-N Assembly Frame at north, east, south and west")
            .addInfo("Use a Hologram Projector: the stack size is the number of tiers to show")
            .beginVariableStructureBlock(7, 65, 4, 31, 7, 65, false)
            .addController("Middle of the hangar's front wall")
            .addCasingInfoMin("Solid Steel Machine Casing", MIN_CASINGS, false)
            .addCasing("Assembly Frame of each storey's tier", "24 per tier", true)
            .addOtherStructurePart("Landing Pad of each ring's tier", "The landing rings")
            .addOtherStructurePart("Any glass", "Hangar and storey windows")
            .addEnergyHatch("1+ (multi-amp and laser hatches allowed)", "Any Solid Steel Machine Casing", 1)
            .addMaintenanceHatch("1", "Any Solid Steel Machine Casing", 1)
            .addInputBus("1+", "Any Solid Steel Machine Casing", 1)
            .addInputHatch("0+", "Any Solid Steel Machine Casing", 1)
            .addOutputBus("1+", "Any Solid Steel Machine Casing", 1)
            .addStructureAuthors(EnumChatFormatting.GOLD + "NewGunrizons")
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<MTEDroneAssemblyBay> getStructureDefinition() {
        return STRUCTURE;
    }

    private boolean check(String piece, int[] o, List<StructureError> errors) {
        return checkPiece(piece, o[0], o[1], o[2], errors);
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        int[] o = OFFSETS[0];
        buildPiece(BASE, stackSize, hintsOnly, o[0], o[1], o[2]);
        int count = GTStructureChannels.STRUCTURE_LENGTH.getValueClamped(stackSize, 1, MAX_TIERS);
        for (int i = 1; i <= count; i++) {
            o = OFFSETS[i];
            buildPiece(TIER + i, stackSize, hintsOnly, o[0], o[1], o[2]);
        }
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        int[] o = OFFSETS[0];
        int built = survivalBuildPiece(BASE, stackSize, o[0], o[1], o[2], elementBudget, env, false, true);
        if (built >= 0) return built;
        int count = GTStructureChannels.STRUCTURE_LENGTH.getValueClamped(stackSize, 1, MAX_TIERS);
        for (int i = 1; i <= count; i++) {
            o = OFFSETS[i];
            built = survivalBuildPiece(TIER + i, stackSize, o[0], o[1], o[2], elementBudget, env, false, true);
            if (built >= 0) return built;
        }
        return -1;
    }

    @Override
    public void checkMachine(IGregTechTileEntity baseMetaTileEntity, ItemStack stack, List<StructureError> errors) {
        casingAmount = 0;
        tiers = 0;
        if (!check(BASE, OFFSETS[0], errors)) return;
        List<StructureError> tierErrors = new ArrayList<>();
        for (int i = 1; i <= MAX_TIERS; i++) {
            tierErrors.clear();
            if (!check(TIER + i, OFFSETS[i], tierErrors)) break;
            tiers = i;
        }
        if (tiers == 0) {
            errors.addAll(tierErrors);
            return;
        }
        checkCasingMin(errors, casingAmount, MIN_CASINGS);
        checkExoticAndNormalEnergyHatches(errors);
        checkHasMaintenanceHatch(errors);
        checkHasInputBus(errors);
        checkHasOutputBus(errors);
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @Nonnull
            @Override
            protected CheckRecipeResult validateRecipe(@Nonnull GTRecipe recipe) {
                int recipeTier = GTUtility.getTier(recipe.mEUt);
                if (recipeTier > tiers) return CheckRecipeResultRegistry.insufficientMachineTier(recipeTier);
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }
        };
    }

    @Override
    public String[] getInfoData() {
        String[] base = super.getInfoData();
        String[] info = new String[base.length + 1];
        System.arraycopy(base, 0, info, 0, base.length);
        info[base.length] = "Tower tier: " + EnumChatFormatting.YELLOW
            + (tiers >= 1 ? GTValues.VN[tiers] : "none")
            + EnumChatFormatting.RESET
            + " ("
            + tiers
            + " storeys)";
        return info;
    }

    @Override
    public void saveNBTData(NBTTagCompound nbt) {
        super.saveNBTData(nbt);
        nbt.setInteger("tiers", tiers);
    }

    @Override
    public void loadNBTData(NBTTagCompound nbt) {
        super.loadNBTData(nbt);
        tiers = nbt.getInteger("tiers");
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity baseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean active, boolean redstoneLevel) {
        return Textures.BlockIcons.createTextureWithCasing(
            this,
            side,
            facing,
            active,
            Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE,
            Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_GLOW,
            Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE,
            Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE_GLOW);
    }

    @Override
    public ITexture getCasingTexture() {
        return CASING.getCasingTexture();
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return DroneRecipeMap.DRONES;
    }

    @Override
    public boolean supportsVoidProtection() {
        return true;
    }

    @Override
    public boolean supportsInputSeparation() {
        return true;
    }

    // ---------------- scene ----------------

    @Override
    public void onPostTick(IGregTechTileEntity baseMetaTileEntity, long tick) {
        super.onPostTick(baseMetaTileEntity, tick);
        if (baseMetaTileEntity.isServerSide() && tick % 40 == 0 && mMachine) {
            sendRenderDataToClient(this);
        }
    }

    @Override
    public void encodeRenderData(ByteBuf buffer) {
        ItemStack product = mOutputItems != null && mOutputItems.length > 0 ? mOutputItems[0] : null;
        buffer.writeInt(tiers);
        buffer.writeInt(product != null && product.getItem() != null ? Item.getIdFromItem(product.getItem()) : -1);
    }

    @Override
    public void decodeRenderData(ByteArrayDataInput buffer) {
        renderTiers = buffer.readInt();
        renderItemId = buffer.readInt();
    }

    @Override
    public void renderTESR(double x, double y, double z, float partialTicks) {
        IGregTechTileEntity tile = getBaseMetaTileEntity();
        if (tile == null || !tile.isActive()) return;
        World world = tile.getWorld();
        if (world == null) return;
        ForgeDirection back = tile.getFrontFacing()
            .getOpposite();
        double cx = x + 0.5D + back.offsetX * CENTER_DEPTH;
        double cz = z + 0.5D + back.offsetZ * CENTER_DEPTH;
        double t = (world.getTotalWorldTime() % 100000L) + partialTicks;

        // the drone being built lifts off inside the hangar
        Item product = renderItemId >= 0 ? Item.getItemById(renderItemId) : null;
        if (product instanceof ItemDrone) {
            double cycle = (t % 200.0D) / 200.0D;
            double lift = Math.min(1.0D, cycle * 2.5D);
            DroneRenderer.render(
                (ItemDrone) product,
                cx,
                y + 0.2D + lift * 0.6D,
                cz,
                (float) (t * 1.5D % 360.0D),
                0.0F,
                0.0F,
                (float) (t * 40.0D * lift),
                (float) lift);
        }

        // a drone of every built tier circles its landing ring
        List<ItemDrone> drones = Drones.all();
        for (int k = 1; k <= Math.min(renderTiers, drones.size()); k++) {
            double r = ringRadius(k);
            double a = t * 0.6D / r + k * 1.3D;
            float yaw = (float) Math.toDegrees(a);
            DroneRenderer.render(
                drones.get(k - 1),
                cx + Math.cos(a) * r,
                y + 1.5D + (k % 3) * 0.7D + Math.sin(t * 0.08D + k) * 0.15D,
                cz + Math.sin(a) * r,
                yaw,
                12.0F,
                0.0F,
                (float) (t * 47.0D),
                1.0F);
        }
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox(int x, int y, int z) {
        int r = ringRadius(MAX_TIERS) + CENTER_DEPTH + 2;
        return AxisAlignedBB.getBoundingBox(x - r, y - 2, z - r, x + r + 1, y + 4, z + r + 1);
    }
}
