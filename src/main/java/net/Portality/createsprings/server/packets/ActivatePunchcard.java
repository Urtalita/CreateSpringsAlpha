package net.Portality.createsprings.server.packets;

import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.Portality.createsprings.Items.ModItems;
import net.Portality.createsprings.Items.advanced.Punchcard.ExecutorInfo;
import net.Portality.createsprings.Items.advanced.Punchcard.PunchcardAction;
import net.Portality.createsprings.Items.advanced.Punchcard.PunchcardExecutor;
import net.Portality.createsprings.Items.advanced.Punchcard.PunchcardInterpritator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Function;

import static net.Portality.createsprings.Items.advanced.Punchcard.PunchcardInterpritator.allPunchcardActions;
import static net.Portality.createsprings.Items.advanced.SpringStufs.SpringPoweredCore.checkItemInContains;

public class ActivatePunchcard extends SimplePacketBase {

    public ActivatePunchcard(FriendlyByteBuf buffer) {}

    public ActivatePunchcard() {}

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        ExecutorInfo info = new ExecutorInfo(
                player.getItemInHand(InteractionHand.MAIN_HAND),
                player.level(),
                player, 0, 0,
                PunchcardExecutor.getFromItem(player.getItemInHand(InteractionHand.MAIN_HAND).getItem()),
                player.getItemInHand(InteractionHand.MAIN_HAND).getItem());


        if(!checkItemInContains(info.getTag(), ModItems.PUNCHCARD.get())){
            if(player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof BacktankItem){
            info = new ExecutorInfo(
                    player.getItemBySlot(EquipmentSlot.CHEST),
                    player.level(),
                    player, 0, 0,
                    PunchcardExecutor.getFromItem(player.getItemBySlot(EquipmentSlot.CHEST).getItem()),
                    player.getItemBySlot(EquipmentSlot.CHEST).getItem());
            if(!checkItemInContains(info.getTag(), ModItems.PUNCHCARD.get())){return true;}
        } else {return true;}}

        CompoundTag punchcard = info.getTag().getCompound("punchcard");

        int curAction = punchcard.getInt("curAction");
        String actionKey = PunchcardAction.getAllFromString(
                punchcard.getString(
                        String.valueOf(curAction))).getName();

        Function<ExecutorInfo, Void> action = allPunchcardActions.get(actionKey);
        if(action != PunchcardInterpritator.empty()){return true;}

        info.nextAction();
        PunchcardInterpritator.DoPunchcardLogic(info);
        return true;
    }
}
