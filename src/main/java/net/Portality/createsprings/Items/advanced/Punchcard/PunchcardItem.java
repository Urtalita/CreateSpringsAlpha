package net.Portality.createsprings.Items.advanced.Punchcard;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.Portality.createsprings.menus.Punchcard.PunchcardMenu;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;


public class PunchcardItem extends Item {

    public PunchcardItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            player.openMenu(new SimpleMenuProvider(
                    (containerId, inv, p) -> new PunchcardMenu(containerId, inv, stack),
                    Component.literal("")
            ));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (player == null) return InteractionResult.PASS;

        CompoundTag tag = stack.getOrCreateTag();

        if (player.isShiftKeyDown()) {
            // Режим сохранения
            if(saveBlock(tag, player, level, pos)){
                stack.setHoverName(Component.literal("Punchcard"));
                player.setItemInHand(context.getHand(), stack);
                return InteractionResult.sidedSuccess(level.isClientSide());
            } else {
                return InteractionResult.FAIL;
            }

        } else {
            if(PlaceBlock(tag, player, level, pos, context)){
                return InteractionResult.sidedSuccess(level.isClientSide());
            } else {
                return InteractionResult.FAIL;
            }
        }
    }

    private boolean saveBlock(CompoundTag tag, Player player, Level level, BlockPos pos) {
        if (tag.contains("Programmed")) {
            if (tag.getBoolean("Programmed")){
                player.displayClientMessage(Component.literal("Already programmed"), true);
                return false;

            } else if (!player.isShiftKeyDown()) {
                player.displayClientMessage(Component.literal("Not programmed"), true);
                return false;
            }
        }

        BlockState state = level.getBlockState(pos);

        BlockEntity blockEntity = level.getBlockEntity(pos);


        CompoundTag beTag = null;
        if (isBlockInList(state)) {
            if (blockEntity != null) {
                beTag = saveWithoutTags(blockEntity);
                beTag.remove("x"); // Удаляем координаты
                beTag.remove("y");
                beTag.remove("z");
                beTag.remove("id"); // Удаляем идентификатор, так как он будет автоматически установлен при создании
                tag.put("BlockEntityData", beTag);
            }
        }

        if(beTag != null){
            if (beTag.contains("Filter")) {
                if (beTag.getCompound("Filter").getString("id").equals("create:filter")) {
                    tag.putBoolean("HaveAdvancedCreateFilter", false);
                } else if (beTag.getCompound("Filter").getString("id").equals("create:attribute_filter")) {
                    tag.putBoolean("HaveAdvancedCreateFilter", true);
                }
            }
        };

        // Сохраняем BlockState
        tag.put("BlockState", NbtUtils.writeBlockState(state));

        tag.putBoolean("Programmed", true);
        player.displayClientMessage(Component.literal("Block saved"), true);

        ListTag enchantments = new ListTag();

        CompoundTag enchantmentTag = new CompoundTag();
        enchantmentTag.putString("id", "minecraft:unbreaking"); // ID
        enchantmentTag.putInt("lvl", 1);
        enchantments.add(enchantmentTag);

        tag.put("Enchantments", enchantments);
        tag.putInt("HideFlags", 1);

        return true;
    }

    private boolean PlaceBlock(CompoundTag tag, Player player, Level level, BlockPos pos, UseOnContext context){
        if (!tag.getBoolean("Programmed")) {
            player.displayClientMessage(Component.literal("Already programmed"), true);
            return false;
        }

        if (level.isClientSide()) return true;

        BlockState state = NbtUtils.readBlockState(
                level.holderLookup(Registries.BLOCK),
                tag.getCompound("BlockState")
        );

        state = resetCheatProperties(state);

        /*

        if (tag.contains("HaveAdvancedCreateFilter")){
            if (tag.getBoolean("HaveAdvancedCreateFilter")){
                if (isExistInInventory(player, item(AllItems.ATTRIBUTE_FILTER.get()))){
                    deleateFromInventory(player, AllItems.ATTRIBUTE_FILTER.get());
                } else {
                    return false;
                }
            } else {
                if (isExistInInventory(player, AllItems.FILTER.get())){
                    deleateFromInventory(player, AllItems.FILTER.get());
                } else {
                    return false;
                }
            }
        }

         */

        if (isExistInInventory(player, state)) {
            boolean isWaterlogged = state.hasProperty(BlockStateProperties.WATERLOGGED)
                    && state.getValue(BlockStateProperties.WATERLOGGED);

            if (isWaterlogged) {
                // Для waterlogged блоков требуется замена ведра
                if (replaceWaterBucketWithEmpty(player)) {
                    deleateFromInventory(player, state);
                } else {
                    player.displayClientMessage(Component.literal("Need water bucket"), true);
                    state = state.setValue(BlockStateProperties.WATERLOGGED, false);
                    deleateFromInventory(player, state);
                }
            } else {
                deleateFromInventory(player, state);
            }
        } else {
            player.displayClientMessage(Component.literal("Item not found"), true);
            return false;
        }

        BlockPos placePos = context.getClickedPos().relative(context.getClickedFace());
        if (level.getBlockState(placePos).canBeReplaced()) {
            VoxelShape collisionShape = state.getCollisionShape(level, placePos);
            if (checkEntityCollisions(level, placePos, collisionShape)){
                level.setBlock(placePos, state, Block.UPDATE_ALL);
                level.updateNeighborsAt(placePos, state.getBlock());

                level.scheduleTick(placePos, Fluids.WATER, 1);

                player.displayClientMessage(Component.literal("Block placed"), true);
            }
        }

        BlockEntity newEntity = level.getBlockEntity(placePos);
        if (newEntity != null && tag.contains("BlockEntityData")) {
            CompoundTag beTag = tag.getCompound("BlockEntityData").copy();
            beTag.putInt("x", placePos.getX());
            beTag.putInt("y", placePos.getY());
            beTag.putInt("z", placePos.getZ());
            try {
                newEntity.load(beTag); // Загружаем данные в новую BlockEntity
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    static Set<String> targetIds = Set.of(
            "create:deployer",
            "create:steam_engine",
            "create:contraption_controls",
            "create:steam_whistle",
            "create:sequenced_gearshift",
            "create:rotation_speed_controller",
            "create:mechanical_arm",
            "create:cogwheel",
            "create:large_cogwheel",
            "create:creative_motor",
            "create:content_observer",
            "create:stockpile_switch",
            "create:rope_pulley",
            "create:smart_fluid_pipe",
            "create:redstone_link",
            "create:display_board",
            "create:brass_funnel",
            "create:brass_tunnel",
            "create:creative_crate",
            "create:basin"
    );

    public static Set<String> tagsToExclude = Set.of(
        "HeldItem",
        "Inventory"
    );

    public static boolean isBlockInList(BlockState state) {
        if (state == null) { //|| targetIds == null || targetIds.isEmpty()
            return false;
        }

        Block block = state.getBlock();
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);

        return blockId != null && targetIds.contains(blockId.toString());
    }

    public static CompoundTag saveWithoutTags(BlockEntity entity) {
        if (entity == null) {
            return new CompoundTag();
        }

        // Сохраняем оригинальный NBT
        CompoundTag originalNbt = entity.saveWithFullMetadata();
        CompoundTag filteredNbt = originalNbt.copy();

        // Удаляем указанные теги
        tagsToExclude.forEach(filteredNbt::remove);

        return filteredNbt;
    }



    private BlockState resetCheatProperties(BlockState originalState) {
        BlockState newState = originalState;

        // POWERED - для дверей, редстоун компонентов
        if (newState.hasProperty(BlockStateProperties.POWERED)) {
            newState = newState.setValue(BlockStateProperties.POWERED, false);
        }

        // LIT - для редстоун ламп, факелов
        if (newState.hasProperty(BlockStateProperties.LIT)) {
            newState = newState.setValue(BlockStateProperties.LIT, false);
        }

        return newState;
    }

    public boolean replaceWaterBucketWithEmpty(Player player) {
        // Не изменяем инвентарь в креативном режиме
        if (player.isCreative()) {
            return true;
        }

        Inventory inventory = player.getInventory();

        // Проходим по всем слотам инвентаря
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            // Проверяем, является ли предмет в слоте ведром с водой
            if (stack.getItem() == Items.WATER_BUCKET) {
                // Создаём новое пустое ведро
                ItemStack emptyBucket = new ItemStack(Items.BUCKET);
                // Заменяем ведро с водой на пустое
                inventory.setItem(slot, emptyBucket);
                // Обновляем состояние инвентаря
                inventory.setChanged();
                return true;
            }
        }
        return false;
    }

    private void deleateFromInventory(Player player, BlockState state){
        if (!player.isCreative()){
            Item item = state.getBlock().asItem();
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (stack.getItem() == item && !stack.isEmpty()) {
                    stack.shrink(1);
                }

                // Если стек опустел, заменяем его EMPTY
                if (stack.isEmpty()) {
                    player.getInventory().setItem(i, ItemStack.EMPTY);
                }

                // Обновляем инвентарь игрока
                player.getInventory().setChanged();
            }
        }
    }

    private void deleateFromInventory(Player player, String item){
        if (!player.isCreative()){
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (stack.getItem().toString() == item && !stack.isEmpty()) {
                    stack.shrink(1);
                }

                // Если стек опустел, заменяем его EMPTY
                if (stack.isEmpty()) {
                    player.getInventory().setItem(i, ItemStack.EMPTY);
                }

                // Обновляем инвентарь игрока
                player.getInventory().setChanged();
            }
        }
    }

    private boolean isExistInInventory(Player player, BlockState state) {
        if (!player.isCreative()){
            Item item = state.getBlock().asItem();
            if (item == Items.AIR) {
                return false;
            }

            // Проходим по всем слотам инвентаря
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);

                // Проверяем соответствие предмета и наличие хотя бы одного экземпляра
                if (stack.getItem() == item && !stack.isEmpty()) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    private boolean isExistInInventory(Player player, String item) {
        if (!player.isCreative()){
            if (item == Items.AIR.toString()) {
                return false;
            }

            // Проходим по всем слотам инвентаря
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);

                // Проверяем соответствие предмета и наличие хотя бы одного экземпляра
                if (stack.getItem().toString() == item && !stack.isEmpty()) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.getBoolean("Programmed")) {
            if (level != null) {
                BlockState state = NbtUtils.readBlockState(
                        level.holderLookup(Registries.BLOCK),
                        tag.getCompound("BlockState")
                );
                tooltip.add(Component.literal("Saved block: " + state.getBlock().getName().getString()));
                tooltip.add(Component.literal("(only properties)").withStyle(ChatFormatting.GRAY));
            }
        } else {
            tooltip.add(Component.literal("Empty punchcard"));
        }
    }

    private static void uTest(Player player){
        player.sendSystemMessage(Component.literal("Tested"));
    }

    private boolean checkEntityCollisions(Level level, BlockPos pos, VoxelShape shape) {
        AABB blockAABB = shape.bounds().move(pos);
        List<LivingEntity> entities = level.getEntitiesOfClass(
                LivingEntity.class,
                blockAABB.inflate(-0.1),
                e -> e.isAlive() && !(e instanceof Player && ((Player)e).isSpectator())
        );
        return entities.isEmpty();
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level world, Player player) {
        super.onCraftedBy(stack, world, player);
        player.getInventory().setChanged();
    }

    @Override
    public boolean shouldOverrideMultiplayerNbt() {
        return true;
    }
}