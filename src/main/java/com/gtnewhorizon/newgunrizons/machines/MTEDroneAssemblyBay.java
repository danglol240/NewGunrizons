package com.gtnewhorizon.newgunrizons.machines;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.isAir;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.ExoticEnergy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.chainAllGlasses;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.io.ByteArrayDataInput;
import com.gtnewhorizon.newgunrizons.client.render.DroneRenderer;
import com.gtnewhorizon.newgunrizons.items.ItemDrone;
import com.gtnewhorizon.newgunrizons.registry.DroneRecipeMap;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import gregtech.api.casing.Casings;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.ICasingTextureProvider;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.render.IMTERenderer;
import io.netty.buffer.ByteBuf;

/**
 * Drone Assembly Bay: a small hangar where every NewGunrizons drone is built.
 * <ul>
 * <li>5 wide, 3 tall, 5 deep; the controller sits in the middle of the front wall;</li>
 * <li>Solid Steel Machine Casings for the walls, any glass for the side windows and the skylight;</li>
 * <li>a 3x3 landing pad of Steel Gear Box Casings under a 3x3 empty bay;</li>
 * <li>the energy hatch decides which drones it can build (LV hatch: LV drones, and so on).</li>
 * </ul>
 * While it works, the drone being built hovers over the pad with its rotors spinning up.
 */
public class MTEDroneAssemblyBay extends MTEExtendedPowerMultiBlockBase<MTEDroneAssemblyBay>
    implements ISurvivalConstructable, ICasingTextureProvider, IMTERenderer {

    private static final String MAIN = "main";
    private static final int OFFSET_X = 2;
    private static final int OFFSET_Y = 1;
    private static final int OFFSET_Z = 0;
    private static final int MIN_CASINGS = 20;
    private static final Casings CASING = Casings.SolidSteelMachineCasing;
    private static final Casings PAD = Casings.SteelGearBoxCasing;

    private int casingAmount;

    // client-side render state
    private int renderItemId = -1;

    private static final IStructureDefinition<MTEDroneAssemblyBay> STRUCTURE = StructureDefinition
        .<MTEDroneAssemblyBay>builder()
        // depth slices front to back; rows top to bottom
        .addShape(
            MAIN,
            new String[][] { { "CCCCC", "CC~CC", "CCCCC" }, { "CGGGC", "G---G", "CPPPC" },
                { "CGGGC", "G---G", "CPPPC" }, { "CGGGC", "G---G", "CPPPC" }, { "CCCCC", "CCCCC", "CCCCC" } })
        .addElement('G', chainAllGlasses())
        .addElement('-', isAir())
        .addElement('P', PAD.asElement())
        .addElement(
            'C',
            buildHatchAdder(MTEDroneAssemblyBay.class)
                .atLeast(InputBus, InputHatch, OutputBus, Maintenance, Energy.or(ExoticEnergy))
                .casingIndex(CASING.textureId)
                .hint(1)
                .buildAndChain(onElementPass(m -> ++m.casingAmount, CASING.asElement())))
        .build();

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
        tt.addMachineType("Drone Assembly Bay")
            .addInfo("Builds every NewGunrizons combat drone, from the LV Scout to the MAX Sentinel")
            .addInfo("The energy hatch tier decides which drones it can build")
            .addInfo("Drones leave the bay with an empty battery: charge them in a Battery Buffer of their tier")
            .beginStructureBlock(5, 3, 5, false)
            .addController("Front center")
            .addCasingInfoMin("Solid Steel Machine Casing", MIN_CASINGS, false)
            .addOtherStructurePart("Steel Gear Box Casing", "3x3 landing pad in the floor")
            .addOtherStructurePart("Any glass", "Side windows and the skylight")
            .addOtherStructurePart("Air", "The 3x3 bay above the pad")
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

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(MAIN, stackSize, hintsOnly, OFFSET_X, OFFSET_Y, OFFSET_Z);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivalBuildPiece(MAIN, stackSize, OFFSET_X, OFFSET_Y, OFFSET_Z, elementBudget, env, false, true);
    }

    @Override
    public void checkMachine(IGregTechTileEntity baseMetaTileEntity, ItemStack stack, List<StructureError> errors) {
        casingAmount = 0;
        if (!checkPiece(MAIN, OFFSET_X, OFFSET_Y, OFFSET_Z, errors)) return;
        checkCasingMin(errors, casingAmount, MIN_CASINGS);
        checkExoticAndNormalEnergyHatches(errors);
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
        buffer.writeInt(product != null && product.getItem() != null ? Item.getIdFromItem(product.getItem()) : -1);
    }

    @Override
    public void decodeRenderData(ByteArrayDataInput buffer) {
        renderItemId = buffer.readInt();
    }

    @Override
    public void renderTESR(double x, double y, double z, float partialTicks) {
        IGregTechTileEntity tile = getBaseMetaTileEntity();
        if (tile == null || !tile.isActive() || renderItemId < 0) return;
        Item item = Item.getItemById(renderItemId);
        if (!(item instanceof ItemDrone)) return;
        World world = tile.getWorld();
        if (world == null) return;
        ForgeDirection back = tile.getFrontFacing()
            .getOpposite();
        double t = (world.getTotalWorldTime() % 100000L) + partialTicks;
        // the drone lifts off the pad and spins its rotors up, then settles and starts again
        double cycle = (t % 200.0D) / 200.0D;
        double lift = Math.min(1.0D, cycle * 2.5D);
        float yaw = (float) (t * 1.5D % 360.0D);
        DroneRenderer.render(
            (ItemDrone) item,
            x + 0.5D + back.offsetX * 2.0D,
            y + 0.15D + lift * 0.45D,
            z + 0.5D + back.offsetZ * 2.0D,
            yaw,
            0.0F,
            0.0F,
            (float) (t * 40.0D * lift),
            (float) lift);
        if (world.rand.nextInt(6) == 0 && lift < 1.0D) {
            world.spawnParticle(
                "fireworksSpark",
                tile.getXCoord() + 0.5D + back.offsetX * 2.0D + (world.rand.nextDouble() - 0.5D),
                tile.getYCoord() + 0.2D,
                tile.getZCoord() + 0.5D + back.offsetZ * 2.0D + (world.rand.nextDouble() - 0.5D),
                0.0D,
                0.05D,
                0.0D);
        }
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox(int x, int y, int z) {
        return AxisAlignedBB.getBoundingBox(x - 5, y - 2, z - 5, x + 6, y + 3, z + 6);
    }
}
