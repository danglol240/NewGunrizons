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
import com.gtnewhorizon.newgunrizons.client.render.VehicleRenderer;
import com.gtnewhorizon.newgunrizons.items.ItemVehicle;
import com.gtnewhorizon.newgunrizons.registry.VehicleRecipeMap;
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
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
import gregtech.common.render.IMTERenderer;
import io.netty.buffer.ByteBuf;

/**
 * Vehicle Assembly Hangar: builds every NewGunrizons vehicle from the LV jeep to the MAX Genesis Ark.
 * <p>
 * A hangar hall 9 wide and 5 high that grows two blocks longer per era: the front wall holds the controller and a
 * glass gallery, each bay behind it is roofed with that era's Assembly Frames and floored with that era's Landing
 * Pads, and a back wall closes the hall. The number of consecutive bays (LV, MV, ...) is the highest vehicle tier it
 * builds. While it works, the vehicle being built stands in the hall.
 */
public class MTEVehicleHangar extends MTEExtendedPowerMultiBlockBase<MTEVehicleHangar>
    implements ISurvivalConstructable, ICasingTextureProvider, IMTERenderer {

    private static final String HEAD = "head";
    private static final String BAY = "bay";
    private static final String BACK = "back";
    private static final int OFFSET_X = 4;
    private static final int OFFSET_Y = 4;
    private static final int BAY_DEPTH = 2;
    private static final int MAX_BAYS = 14;
    private static final int MIN_CASINGS = 40;
    private static final Casings CASING = Casings.SolidSteelMachineCasing;

    private int casingAmount;
    private int bays;

    private int renderBays;
    private int renderItemId = -1;

    private static final IStructureDefinition<MTEVehicleHangar> STRUCTURE = buildStructure();

    private static char padChar(int tier) {
        return (char) (0x200 + tier);
    }

    private static IStructureDefinition<MTEVehicleHangar> buildStructure() {
        StructureDefinition.Builder<MTEVehicleHangar> b = StructureDefinition.<MTEVehicleHangar>builder()
            .addShape(HEAD, new String[][] { { "CCCCCCCCC", "CGGGGGGGC", "CGGGGGGGC", "CGGGGGGGC", "CCCC~CCCC" } })
            .addShape(BACK, new String[][] { { "CCCCCCCCC", "CCCCCCCCC", "CCCCCCCCC", "CCCCCCCCC", "CCCCCCCCC" } });
        for (int tier = 1; tier <= MAX_BAYS; tier++) {
            char f = MTEMunitionsFactory.segmentChar(tier);
            char p = padChar(tier);
            String roof = repeat(f, 9);
            String floor = "C" + repeat(p, 7) + "C";
            String[] slice = { roof, "C-------C", "C-------C", "C-------C", floor };
            b.addShape(BAY + tier, new String[][] { slice, slice })
                .addElement(f, MTEMunitionsFactory.frameOrCasing(tier))
                .addElement(p, ofBlock(NGBlocks.LandingPad, tier - 1));
        }
        return b.addElement('G', chainAllGlasses())
            .addElement('-', isAir())
            .addElement(
                'C',
                buildHatchAdder(MTEVehicleHangar.class)
                    .atLeast(InputBus, InputHatch, OutputBus, Maintenance, Energy.or(ExoticEnergy))
                    .casingIndex(CASING.textureId)
                    .hint(1)
                    .buildAndChain(onElementPass(m -> ++m.casingAmount, CASING.asElement())))
            .build();
    }

    private static String repeat(char c, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(c);
        return sb.toString();
    }

    private static int bayOffset(int tier) {
        return -(1 + BAY_DEPTH * (tier - 1));
    }

    public MTEVehicleHangar(int id, String name, String nameRegional) {
        super(id, name, nameRegional);
    }

    public MTEVehicleHangar(String name) {
        super(name);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new MTEVehicleHangar(this.mName);
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType("Vehicle Assembly Hangar")
            .addInfo("Builds every NewGunrizons vehicle, from the LV Field Jeep to the MAX Genesis Ark")
            .addInfo("A hangar hall that grows two blocks longer with every era:")
            .addInfo("bay 1 is roofed with LV Assembly Frames and floored with LV Landing Pads, bay 2 with MV ...")
            .addInfo("The number of consecutive bays is the highest vehicle tier it builds")
            .addInfo("Vehicle parts (chassis, armour, engine, controls, wheels...) are made in an Assembler,")
            .addInfo("from LuV on in an Assembly Line")
            .addInfo("Vehicles leave with an empty tank: fuel or charge them before driving")
            .addSeparator()
            .addInfo("Front wall 9x5: Solid Steel Machine Casings, 7x3 glass gallery, controller bottom middle")
            .addInfo("Bay N (2 deep): roof of 9 tier-N Assembly Frames (or that tier's Machine Casing),")
            .addInfo("Solid Steel side walls, empty 7x3 hall, floor of 7 tier-N Landing Pads")
            .addInfo("Back wall 9x5 of Solid Steel Machine Casings right behind the last bay")
            .addInfo("Use a Hologram Projector: the stack size is the number of bays to show")
            .beginVariableStructureBlock(9, 9, 5, 5, 4, 2 + BAY_DEPTH * MAX_BAYS, false)
            .addController("Front wall, bottom middle")
            .addCasingInfoMin("Solid Steel Machine Casing", MIN_CASINGS, false)
            .addCasing("Assembly Frame of each bay's tier", "9 per bay slice", true)
            .addOtherStructurePart("Landing Pad of each bay's tier", "Bay floors")
            .addOtherStructurePart("Any glass", "Front gallery")
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
    public IStructureDefinition<MTEVehicleHangar> getStructureDefinition() {
        return STRUCTURE;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(HEAD, stackSize, hintsOnly, OFFSET_X, OFFSET_Y, 0);
        int count = GTStructureChannels.STRUCTURE_LENGTH.getValueClamped(stackSize, 1, MAX_BAYS);
        for (int i = 1; i <= count; i++) buildPiece(BAY + i, stackSize, hintsOnly, OFFSET_X, OFFSET_Y, bayOffset(i));
        buildPiece(BACK, stackSize, hintsOnly, OFFSET_X, OFFSET_Y, bayOffset(count + 1));
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        // no 'already formed' shortcut: the hall is valid from its first bay on (see the Drone Assembly Complex)
        int built = survivalBuildPiece(HEAD, stackSize, OFFSET_X, OFFSET_Y, 0, elementBudget, env, false, true);
        if (built >= 0) return built;
        int count = GTStructureChannels.STRUCTURE_LENGTH.getValueClamped(stackSize, 1, MAX_BAYS);
        for (int i = 1; i <= count; i++) {
            built = survivalBuildPiece(
                BAY + i,
                stackSize,
                OFFSET_X,
                OFFSET_Y,
                bayOffset(i),
                elementBudget,
                env,
                false,
                true);
            if (built >= 0) return built;
        }
        return survivalBuildPiece(
            BACK,
            stackSize,
            OFFSET_X,
            OFFSET_Y,
            bayOffset(count + 1),
            elementBudget,
            env,
            false,
            true);
    }

    @Override
    public void checkMachine(IGregTechTileEntity baseMetaTileEntity, ItemStack stack, List<StructureError> errors) {
        casingAmount = 0;
        bays = 0;
        if (!checkPiece(HEAD, OFFSET_X, OFFSET_Y, 0, errors)) return;
        List<StructureError> bayErrors = new ArrayList<>();
        for (int i = 1; i <= MAX_BAYS; i++) {
            bayErrors.clear();
            if (!checkPiece(BAY + i, OFFSET_X, OFFSET_Y, bayOffset(i), bayErrors)) break;
            bays = i;
        }
        if (bays == 0) {
            errors.addAll(bayErrors);
            return;
        }
        if (!checkPiece(BACK, OFFSET_X, OFFSET_Y, bayOffset(bays + 1), errors)) {
            bays = 0;
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
                int era = recipe.mSpecialValue;
                if (era > bays) return CheckRecipeResultRegistry.insufficientMachineTier(era);
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }
        };
    }

    @Override
    public String[] getInfoData() {
        String[] base = super.getInfoData();
        String[] info = new String[base.length + 1];
        System.arraycopy(base, 0, info, 0, base.length);
        info[base.length] = "Hangar era: " + EnumChatFormatting.YELLOW
            + (bays >= 1 ? GTValues.VN[bays] : "none")
            + EnumChatFormatting.RESET
            + " ("
            + bays
            + " bays)";
        return info;
    }

    @Override
    public void saveNBTData(NBTTagCompound nbt) {
        super.saveNBTData(nbt);
        nbt.setInteger("bays", bays);
    }

    @Override
    public void loadNBTData(NBTTagCompound nbt) {
        super.loadNBTData(nbt);
        bays = nbt.getInteger("bays");
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
        return VehicleRecipeMap.VEHICLES;
    }

    @Override
    public boolean supportsVoidProtection() {
        return true;
    }

    @Override
    public boolean supportsInputSeparation() {
        return true;
    }

    // ---------------- scene: the vehicle being built stands in the hall ----------------

    @Override
    public void onPostTick(IGregTechTileEntity baseMetaTileEntity, long tick) {
        super.onPostTick(baseMetaTileEntity, tick);
        if (baseMetaTileEntity.isServerSide() && tick % 40 == 0 && mMachine) sendRenderDataToClient(this);
    }

    @Override
    public void encodeRenderData(ByteBuf buffer) {
        ItemStack product = mOutputItems != null && mOutputItems.length > 0 ? mOutputItems[0] : null;
        buffer.writeInt(bays);
        buffer.writeInt(product != null && product.getItem() != null ? Item.getIdFromItem(product.getItem()) : -1);
    }

    @Override
    public void decodeRenderData(ByteArrayDataInput buffer) {
        renderBays = buffer.readInt();
        renderItemId = buffer.readInt();
    }

    @Override
    public void renderTESR(double x, double y, double z, float partialTicks) {
        IGregTechTileEntity tile = getBaseMetaTileEntity();
        if (tile == null || !tile.isActive() || renderBays <= 0) return;
        Item product = renderItemId >= 0 ? Item.getItemById(renderItemId) : null;
        if (!(product instanceof ItemVehicle)) return;
        World world = tile.getWorld();
        if (world == null) return;
        ForgeDirection back = tile.getFrontFacing()
            .getOpposite();
        double depth = 1.0D + BAY_DEPTH * renderBays / 2.0D;
        double cx = x + 0.5D + back.offsetX * depth;
        double cz = z + 0.5D + back.offsetZ * depth;
        float yaw = (float) Math.toDegrees(Math.atan2(-back.offsetX, back.offsetZ)) + 180.0F;
        double t = (world.getTotalWorldTime() % 100000L) + partialTicks;
        VehicleRenderer.renderStatic(
            (ItemVehicle) product,
            cx,
            y + 1.0D,
            cz,
            yaw + (float) Math.sin(t * 0.01D) * 10.0F,
            7.0F,
            3.0F,
            BAY_DEPTH * renderBays);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox(int x, int y, int z) {
        int r = BAY_DEPTH * MAX_BAYS + 6;
        return AxisAlignedBB.getBoundingBox(x - r, y - 2, z - r, x + r + 1, y + 6, z + r + 1);
    }
}
