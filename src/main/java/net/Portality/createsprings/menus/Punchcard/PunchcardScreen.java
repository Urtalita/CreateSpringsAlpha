package net.Portality.createsprings.menus.Punchcard;

import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.Items.ModItems;
import net.Portality.createsprings.Items.advanced.Punchcard.PunchcardAction;
import net.Portality.createsprings.Items.advanced.Punchcard.PunchcardExecutor;
import net.Portality.createsprings.Items.advanced.Punchcard.PunchcardFunction;
import net.Portality.createsprings.server.NetworkHandler;
import net.Portality.createsprings.server.PunchcardUpdatePacket;
import net.Portality.createsprings.client.CSpringsGuiTextures;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static net.Portality.createsprings.Items.advanced.Punchcard.PunchcardFunction.getEndNum;

public class PunchcardScreen extends AbstractSimiScreen {

    private final ItemStack renderedBigPunchcard = ModItems.PUNCHCARD.asStack();
    private ItemStack renderedExecutor;

    private final CSpringsGuiTextures background = CSpringsGuiTextures.PUNCHCARD_BG;

    private IconButton confirmButton;
    private ScrollInput executorSelector;
    private EditBox nameInput;
    private ScrollInput[] selectors = new ScrollInput[5];
    private PunchcardFunction[] actions = new PunchcardFunction[5];
    private String[] parameters = new String[5];
    private EditBox[] editBoxes = new EditBox[5];

    private final CompoundTag tag;
    private boolean canConfigure;
    private String itemName;
    int maxActions;

    public PunchcardScreen(ItemStack stack){
        this.tag = stack.getOrCreateTag();
        renderedExecutor = PunchcardExecutor.values()[getExecutorOptionsState()].item.getDefaultInstance();
        canConfigure = !tag.getBoolean("Programmed");
        maxActions = tag.getInt("maxActions");

        if(tag.contains("display")){
             itemName = stack.getHoverName().getString();
        } else {
             itemName = I18n.get(ModItems.PUNCHCARD.get().getDescriptionId());
        }

        for(int i = 0; i < actions.length; i++){
            actions[i] = PunchcardFunction.END;
            parameters[i] = "";
        }
    }

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int x = guiLeft;
        int y = guiTop;

        background.render(graphics, x, y);

        graphics.drawString(this.font, Component.translatable(CreateSprings.MODID + ".punchcard.name"), x + 47, y + 184 + 4, 0x505050, false);
        if(!canConfigure){graphics.drawString(font, itemName, x + 47, y + 199 + 4, 0xFFFFFF);}

        int drawCnt = maxActions;
        if(drawCnt != actions.length){drawCnt += 1;}

        for(int i = 0; i < drawCnt; i++){
            CSpringsGuiTextures toDraw = CSpringsGuiTextures.PUNCHCARD_ACTION;
            toDraw.render(graphics, guiLeft + background.getWidth() - 173, guiTop + background.getHeight() - 195 + 23 * i);
            graphics.drawString(this.font,
                    Component.translatable(CreateSprings.MODID + ".punchcard." + actions[i].getFunctionName() + ".slim"),
                    guiLeft + background.getWidth() - 164 + 3, guiTop + background.getHeight() - 195 + 23 * i + 5, 0xFFFFFF);
        }

        for(int i = 0; i < drawCnt; i++){
            if(actions[i] == PunchcardFunction.END){continue;}
            if(!actions[i].isRequestParam()){continue;}
            CSpringsGuiTextures toDraw = CSpringsGuiTextures.PUNCHCARD_ACTION_PARAMETER;
            toDraw.render(graphics, guiLeft + background.getWidth() - 106, guiTop + background.getHeight() - 195 + 23 * i);
        }

        renderAdditional(graphics, mouseX, mouseY, partialTicks, x, y, background);
    }

    @Override
    protected void init() {
        setWindowSize(background.getWidth(), background.getHeight());
        super.init();

        int x = guiLeft;
        int y = guiTop;

        if(canConfigure){
            initNameEditBox(x, y);
            initInput();
            reInitMainSelectors();
        }

        confirmButton = new IconButton(x + background.getWidth() - 63, y + background.getHeight() - 59, AllIcons.I_CONFIRM);
        confirmButton.withCallback(() -> {
            program();
        });
        addRenderableWidget(confirmButton);
    }

    private void reInitMainSelectors(){
        int drawCnt = maxActions;
        if(drawCnt != actions.length){drawCnt += 1;}
        for(int i = 0; i < drawCnt; i++){
            initMainSelectors(i);
            initParamsEditBoxes(i);
        }
    }

    private void initMainSelectors(int i){
        removeWidget(selectors[i]);
        int finalI = i;
        selectors[i] = new SelectionScrollInput(guiLeft + background.getWidth() - 173, guiTop + background.getHeight() - 195 + 23 * i, 50, 18)
                .forOptions(PunchcardFunction.getForSelector(PunchcardExecutor.getFromItem(renderedExecutor.getItem())))
                .calling(state -> mainScrollUpdated(state, finalI))
                .setState(getEndNum(PunchcardExecutor.getFromItem(renderedExecutor.getItem())))
                .titled(Component.translatable(CreateSprings.MODID + ".punchcard.action.title"));

        addRenderableWidget(selectors[i]);
    }

    private void initParamsEditBoxes(int i){
        removeWidget(editBoxes[i]);

        if(!actions[i].isRequestParam()){return;}

        editBoxes[i] = new EditBox(font, guiLeft + background.getWidth() - 100, guiTop + background.getHeight() - 195 + 23 * i + 5, 61, 18, CommonComponents.EMPTY);
        editBoxes[i].setMaxLength(48);
        editBoxes[i].setBordered(false);
        editBoxes[i].setTextColor(0xFFFFFF);
        int finalI = i;
        editBoxes[i].setValue(parameters[finalI]);
        editBoxes[i].setResponder(text -> parameters[finalI] = text);
        editBoxes[i].setFocused(false);

        if(actions[i].isNeedNumericInput()){
            editBoxes[i].setFilter(s -> {
                if (s.isEmpty() || s.equals("-"))
                    return true;
                try {
                    Integer.parseInt(s);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            });
        }
        addRenderableWidget(editBoxes[i]);
    }

    private void initNameEditBox(int x, int y){
        nameInput = new EditBox(font, x + 47, y + 199 + 4, 136, 18, CommonComponents.EMPTY);
        nameInput.setMaxLength(48);
        nameInput.setBordered(false);
        nameInput.setTextColor(0xFFFFFF);
        nameInput.setValue(itemName);
        nameInput.setResponder(text -> itemName = text);
        nameInput.setFocused(false);
        addRenderableWidget(nameInput);
    }

    private void renderAdditional(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, int guiLeft, int guiTop,
                                  CSpringsGuiTextures background) {
        GuiGameElement.of(renderedBigPunchcard).<GuiGameElement
                        .GuiRenderBuilder>at(guiLeft + background.getWidth() - 20, guiTop + background.getHeight() - 100, 100)
                .scale(5)
                .render(graphics);

        GuiGameElement.of(renderedExecutor).<GuiGameElement
                        .GuiRenderBuilder>at(guiLeft + background.getWidth() - 211, guiTop + background.getHeight() - 195, 100)
                .scale(2)
                .render(graphics);
    }
    private void initInput(){
        executorSelector =
                new SelectionScrollInput(guiLeft + background.getWidth() - 211, guiTop + background.getHeight() - 195, 32, 32).forOptions(getExecutorOptions())
                        .calling(this::scrollUpdated)
                        .setState(getExecutorOptionsState())
                        .titled(Component.translatable(CreateSprings.MODID + ".punchcard.executor.title"));

        addRenderableWidget(executorSelector);
    }

    private int getExecutorOptionsState(){
        String name = tag.getString("executor");
        for(int i = 0; i < PunchcardExecutor.values().length; i++){
            if(PunchcardExecutor.values()[i].nameOfExecutor.equals(name)){
                return i;
            }
        }
        return 0;
    }

    private List<Component> getExecutorOptions() {
        List<Component> options = new ArrayList<>();
        PunchcardExecutor[] executors = PunchcardExecutor.values();

        for(int i = 0; i < executors.length; i++){
            options.add(Component.translatable(executors[i].nameOfExecutor));
        }
        return options;
    }

    private void mainScrollUpdated(int state, int selector){
        actions[selector] = PunchcardFunction.getForActions(PunchcardExecutor.getFromItem(renderedExecutor.getItem())).get(state);
        initParamsEditBoxes(selector);

        if(state != getEndNum(PunchcardExecutor.getFromItem(renderedExecutor.getItem()))){
            if(maxActions <= selector){
                maxActions = selector + 1;
            }
            if(maxActions != 5){
                initMainSelectors(maxActions);
                initParamsEditBoxes(maxActions);
            }
        } else {
            maxActions = selector;

            int drawCnt = maxActions;
            if(drawCnt != actions.length){drawCnt += 1;}

            if(drawCnt < 5){
                for(int i = drawCnt; i < 5; i++){
                    removeWidget(selectors[i]);
                    removeWidget(editBoxes[i]);
                }
            }
        }
    }

    private void scrollUpdated(Integer state){
        PunchcardExecutor[] executors = PunchcardExecutor.values();
        renderedExecutor = executors[state].item.getDefaultInstance();
        tag.putString("executor", executors[state].nameOfExecutor);

        reInitMainSelectors();
    }

    public void sendPacket() {
        NetworkHandler.CHANNEL.sendToServer(new PunchcardUpdatePacket(tag));
    }

    @Override
    public void removed() {
        sendPacket();
    }

    private void program(){
        tag.putBoolean("Programmed", true);

        ListTag enchantments = new ListTag();

        CompoundTag enchantmentTag = new CompoundTag();
        enchantmentTag.putString("id", "minecraft:unbreaking"); // ID
        enchantmentTag.putInt("lvl", 1);
        enchantments.add(enchantmentTag);

        tag.put("Enchantments", enchantments);
        tag.putInt("HideFlags", 1);

        CompoundTag display = new CompoundTag();
        display.putString("Name", Component.Serializer.toJson(Component.literal(itemName)));
        tag.put("display" ,display);

        tag.putInt("maxActions", maxActions);
        tag.putInt("curAction", 0);

        for(int i = 0; i < actions.length; i++){
            if(actions[i] == PunchcardFunction.END){continue;}

            if(!actions[i].isRequestParam()){
                tag.putString(String.valueOf(i), PunchcardAction.putPunchcardActionInString(new PunchcardAction(actions[i].getFunctionName(), "0")));
                continue;
            }

            tag.putString(String.valueOf(i), PunchcardAction.putPunchcardActionInString(new PunchcardAction(actions[i].getFunctionName(), parameters[i])));
        }

        onClose();
    }
}
