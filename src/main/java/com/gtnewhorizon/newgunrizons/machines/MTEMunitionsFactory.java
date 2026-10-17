package com.gtnewhorizon.newgunrizons.machines;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.isAir;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static gregtech.api.enums.HatchElement.Energy;
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
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.io.ByteArrayDataInput;
import com.gtnewhorizon.newgunrizons.client.render.FactoryAssemblyRenderer;
import com.gtnewhorizon.newgunrizons.registry.MunitionsRecipeMap;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import gregtech.api.GregTechAPI;
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
 * Munitions Factory: the only machine that makes NewGunrizons weapons, components, magazines and ammunition.
 * <p>
 * A 3x3 production line that grows one segment per era, like an Assembly Line:
 * <ul>
 * <li>the head holds the controller, framed by Heat Proof Machine Casings;</li>
 * <li>segment N behind it has a roof of three tier-N Machine Casings (segment 1 = LV, 2 = MV ... 9 = UHV), glass side
 * windows around an empty production lane, and a Heat Proof Machine Casing floor;</li>
 * <li>the number of consecutive segments is the factory's era: it accepts recipes up to that voltage tier;</li>
 * <li>every segment adds 4 parallels.</li>
 * </ul>
 * While running, the product is shown being assembled as it travels along the lane.
 */
public class MTEMunitionsFactory extends MTEExtendedPowerMultiBlockBase<MTEMunitionsFactory>
    implements ISurvivalConstructable, ICasingTextureProvider, IMTERenderer {

    private static final String HEAD = "head";
    private static final String SEGMENT = "segment";
    private static final int OFFSET_X = 1;
    private static final int OFFSET_Y = 1;
    /** Machine Casing meta in sBlockCasings1 equals its voltage tier: 1 = LV ... 9 = UHV. */
    private static final int MAX_SEGMENTS = 9;
    private static final int PARALLEL_PER_SEGMENT = 4;
    private static final int MIN_CASINGS = 5;
    private static final Casings CASING = Casings.HeatProofMachineCasing;

    private int casingAmount;
    private int segments;

    // client-side render state, synced from the server by render-data packets
    private int renderSegments;
    private int renderItemId = -1;
    private int renderItemMeta;

    private static final IStructureDefinition<MTEMunitionsFactory> STRUCTURE = buildStructure();

    private static IStructureDefinition<MTEMunitionsFactory> buildStructure() {
        StructureDefinition.Builder<MTEMunitionsFactory> b = StructureDefinition.<MTEMunitionsFactory>builder()
            // rows top to bottom; the controller row is the production lane
            .addShape(HEAD, new String[][] { { "CCC", "C~C", "CCC" } })
            .addElement('W', chainAllGlasses())
            .addElement('-', isAir())
            .addElement(
                'C',
                buildHatchAdder(MTEMunitionsFactory.class)
                    .atLeast(InputBus, InputHatch, OutputBus, Maintenance, Energy)
                    .casingIndex(CASING.textureId)
                    .hint(1)
                    .buildAndChain(onElementPass(m -> ++m.casingAmount, CASING.asElement())));
        // segment N: roof of tier-N Machine Casings ('1' = LV ... '9' = UHV)
        for (int tier = 1; tier <= MAX_SEGMENTS; tier++) {
            char c = (char) ('0' + tier);
            String roof = new String(new char[] { c, c, c });
            b.addShape(SEGMENT + tier, new String[][] { { roof, "W-W", "CCC" } })
                .addElement(c, ofBlock(GregTechAPI.sBlockCasings1, tier));
        }
        return b.build();
    }

    public MTEMunitionsFactory(int id, String name, String nameRegional) {
        super(id, name, nameRegional);
    }

    public MTEMunitionsFactory(String name) {
        super(name);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new MTEMunitionsFactory(this.mName);
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType("Munitions Factory")
            .addInfo("Manufactures every NewGunrizons weapon, part, magazine and round")
            .addInfo("Weapons and ammo cannot be made anywhere else")
            .addSeparator()
            .addInfo("A production line that grows one segment per era:")
            .addInfo("segment 1 is roofed with LV Machine Casings, segment 2 with MV, ... segment 9 with UHV")
            .addInfo("The number of segments is the factory's era; it accepts recipes up to that tier")
            .addInfo("Each segment adds " + PARALLEL_PER_SEGMENT + " parallels")
            .addInfo("Runs recipes at double speed")
            .addInfo("While running, the product is assembled along the lane behind the glass")
            .beginVariableStructureBlock(3, 3, 3, 3, 2, 1 + MAX_SEGMENTS, false)
            .addController("Front center")
            .addCasingInfoMin("Heat Proof Machine Casing", MIN_CASINGS, false)
            .addCasing("3 per segment", "Machine Casing of the segment's era (LV, MV, HV, ...), as the roof", true)
            .addOtherStructurePart("Any glass", "2 per segment, side windows of the production lane")
            .addOtherStructurePart("Air", "1 per segment, the production lane")
            .addEnergyHatch("1+", "Any Heat Proof Machine Casing", 1)
            .addMaintenanceHatch("1", "Any Heat Proof Machine Casing", 1)
            .addInputBus("1+", "Any Heat Proof Machine Casing", 1)
            .addInputHatch("0+", "Any Heat Proof Machine Casing", 1)
            .addOutputBus("1+", "Any Heat Proof Machine Casing", 1)
            .addStructureAuthors(EnumChatFormatting.GOLD + "NewGunrizons")
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<MTEMunitionsFactory> getStructureDefinition() {
        return STRUCTURE;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(HEAD, stackSize, hintsOnly, OFFSET_X, OFFSET_Y, 0);
        int count = GTStructureChannels.STRUCTURE_LENGTH.getValueClamped(stackSize, 1, MAX_SEGMENTS);
        for (int i = 1; i <= count; i++) {
            buildPiece(SEGMENT + i, stackSize, hintsOnly, OFFSET_X, OFFSET_Y, -i);
        }
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        int built = survivalBuildPiece(HEAD, stackSize, OFFSET_X, OFFSET_Y, 0, elementBudget, env, false, true);
        if (built >= 0) return built;
        int count = GTStructureChannels.STRUCTURE_LENGTH.getValueClamped(stackSize, 1, MAX_SEGMENTS);
        for (int i = 1; i <= count; i++) {
            built = survivalBuildPiece(SEGMENT + i, stackSize, OFFSET_X, OFFSET_Y, -i, elementBudget, env, false, true);
            if (built >= 0) return built;
        }
        return -1;
    }

    @Override
    public void checkMachine(IGregTechTileEntity baseMetaTileEntity, ItemStack stack, List<StructureError> errors) {
        casingAmount = 0;
        segments = 0;
        if (!checkPiece(HEAD, OFFSET_X, OFFSET_Y, 0, errors)) return;

        // Count consecutive segments: segment i must carry tier-i Machine Casings.
        List<StructureError> segmentErrors = new ArrayList<>();
        for (int i = 1; i <= MAX_SEGMENTS; i++) {
            segmentErrors.clear();
            if (!checkPiece(SEGMENT + i, OFFSET_X, OFFSET_Y, -i, segmentErrors)) break;
            segments = i;
        }
        if (segments == 0) {
            errors.addAll(segmentErrors);
            return;
        }
        checkCasingMin(errors, casingAmount, MIN_CASINGS);
        checkHasEnergyHatch(errors);
        checkHasMaintenanceHatch(errors);
        checkHasInputBus(errors);
        checkHasOutputBus(errors);
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
        return MunitionsRecipeMap.MUNITIONS;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @Nonnull
            @Override
            protected CheckRecipeResult validateRecipe(@Nonnull GTRecipe recipe) {
                int recipeTier = GTUtility.getTier(recipe.mEUt);
                if (recipeTier > segments) {
                    return CheckRecipeResultRegistry.insufficientMachineTier(recipeTier);
                }
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }
        }.setSpeedBonus(1.0D / 2.0D)
            .setMaxParallelSupplier(this::getTrueParallel);
    }

    @Override
    public int getMaxParallelRecipes() {
        return PARALLEL_PER_SEGMENT * Math.max(1, segments);
    }

    @Override
    public String[] getInfoData() {
        String[] base = super.getInfoData();
        String[] info = new String[base.length + 2];
        System.arraycopy(base, 0, info, 0, base.length);
        String era = segments >= 1 ? GTValues.VN[segments] : "none";
        info[base.length] = "Era: " + EnumChatFormatting.YELLOW + era + EnumChatFormatting.RESET;
        info[base.length + 1] = "Segments: " + EnumChatFormatting.YELLOW
            + segments
            + EnumChatFormatting.RESET
            + " ("
            + getMaxParallelRecipes()
            + " parallels)";
        return info;
    }

    @Override
    public void saveNBTData(NBTTagCompound nbt) {
        super.saveNBTData(nbt);
        nbt.setInteger("segments", segments);
    }

    @Override
    public void loadNBTData(NBTTagCompound nbt) {
        super.loadNBTData(nbt);
        segments = nbt.getInteger("segments");
    }

    // ---------------- assembly scene ----------------

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
        buffer.writeInt(segments);
        buffer.writeInt(product != null && product.getItem() != null ? Item.getIdFromItem(product.getItem()) : -1);
        buffer.writeInt(product != null ? product.getItemDamage() : 0);
    }

    @Override
    public void decodeRenderData(ByteArrayDataInput buffer) {
        renderSegments = buffer.readInt();
        renderItemId = buffer.readInt();
        renderItemMeta = buffer.readInt();
    }

    @Override
    public void renderTESR(double x, double y, double z, float partialTicks) {
        if (renderItemId < 0 || renderSegments <= 0) return;
        Item item = Item.getItemById(renderItemId);
        if (item == null) return;
        FactoryAssemblyRenderer.render(
            getBaseMetaTileEntity(),
            new ItemStack(item, 1, renderItemMeta),
            renderSegments,
            x,
            y,
            z,
            partialTicks);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox(int x, int y, int z) {
        int r = MAX_SEGMENTS + 2;
        return AxisAlignedBB.getBoundingBox(x - r, y - r, z - r, x + r + 1, y + r + 1, z + r + 1);
    }

    @Override
    public boolean supportsInputSeparation() {
        return true;
    }

    @Override
    public boolean supportsVoidProtection() {
        return true;
    }

    @Override
    public boolean supportsBatchMode() {
        return true;
    }
}
