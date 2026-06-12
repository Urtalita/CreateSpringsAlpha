package com.Portality.createsprings.server.packets;

import com.Portality.createsprings.items.CSpringsItems;
import com.Portality.createsprings.items.SpringStufs.PortativeSteamEngine.PortativeSteamEngineItem;
import com.Portality.createsprings.items.SpringStufs.SpringPoweredCore;
import com.Portality.createsprings.items.advanced.Punchcard.*;
import com.Portality.createsprings.server.CSpringsDataComponents;
import com.Portality.createsprings.server.CSpringsPackets;
import com.simibubi.create.content.equipment.armor.BacktankItem;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;

import java.util.function.Function;

import static com.Portality.createsprings.items.SpringStufs.SpringPoweredCore.checkItemInContains;
import static com.Portality.createsprings.items.advanced.Punchcard.PunchcardInterpritator.allPunchcardActions;

public enum ActivatePunchcard implements ServerboundPacketPayload {
    INSTANCE;

    public static final StreamCodec<ByteBuf, ActivatePunchcard> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public void handle(ServerPlayer player) {
        ExecutorInfo info = new ExecutorInfo(
                player.getItemInHand(InteractionHand.MAIN_HAND),
                player.level(),
                player, 0, true,
                PunchcardExecutor.getFromItem(player.getItemInHand(InteractionHand.MAIN_HAND).getItem()),
                player.getItemInHand(InteractionHand.MAIN_HAND).getItem());


        if(!checkItemInContains(SpringPoweredCore.getContent(info.getStack()), CSpringsItems.PUNCHCARD.get())){
            if(player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof BacktankItem){
                info = new ExecutorInfo(
                        player.getItemBySlot(EquipmentSlot.CHEST),
                        player.level(),
                        player, 0, true,
                        PunchcardExecutor.getFromItem(player.getItemBySlot(EquipmentSlot.CHEST).getItem()),
                        player.getItemBySlot(EquipmentSlot.CHEST).getItem());
                if(!checkItemInContains(SpringPoweredCore.getContent(info.getStack()), CSpringsItems.PUNCHCARD.get())){return;}
            }
        }

        CompoundTag punchcard = info.getStack().getOrDefault(CSpringsDataComponents.PUNCHCARD, new CompoundTag());

        int curAction = punchcard.getInt("curAction");
        String actionKey = PunchcardAction.getAllFromString(
                punchcard.getString(
                        String.valueOf(curAction))).getName();

        Function<ExecutorInfo, Void> action = allPunchcardActions.get(actionKey);

        if(action != PunchcardFunction.WAIT_FOR_ACTIVATION.getFunc()){return;}

        info.nextAction();
        PunchcardInterpritator.DoPunchcardLogic(info);
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return CSpringsPackets.ACTIVATE_PUNCHCARD;
    }
}
