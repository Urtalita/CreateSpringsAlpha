package com.Portality.createsprings.client;

import com.Portality.createsprings.CreateSprings;
import net.createmod.catnip.gui.TextureSheetSegment;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.gui.element.ScreenElement;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public enum CSpringsGuiTextures implements ScreenElement, TextureSheetSegment {
    PUNCHCARD_BG("punchcard_background", 0, 0, 256, 256),
    PUNCHCARD_ACTION("action", 0, 0, 67, 18),
    PUNCHCARD_ACTION_PARAMETER("action", 67, 0, 61, 18),
    LEVER_BASE("lever_base", 0, 0, 80, 30),
    LEVER_HEAD("lever", 0, 0, 20, 100),
    PORTATIVE_STEAM_BG("portative_steam_engine_bg", 0, 0, 256, 256),
    PORTATIVE_STEAM_BG_POWERED("portative_steam_engine_bg_powered", 0, 0, 256, 256),
    PORTATIVE_STEAM_SIDE("portative_steam_engine_side", 0, 0, 256, 256),
    PORTATIVE_STEAM_SIDE_RIGHT("portative_steam_engine_side_right", 0, 0, 256, 256),
    PORTATIVE_STEAM_BOOST("portative_steam_engine_boost", 0, 0, 256, 256),
    PORTATIVE_STEAM_CORNER("portative_steam_engine_corner", 0, 0, 256, 256),
    NO_BOOST("no_boost", 0, 0, 256, 256)
    ;

    public static final int FONT_COLOR = 0x575F7A;

    public final ResourceLocation location;
    private final int width;
    private final int height;
    private final int startX;
    private final int startY;

    CSpringsGuiTextures(String location, int width, int height) {
        this(location, 0, 0, width, height);
    }

    CSpringsGuiTextures(String location, int startX, int startY, int width, int height) {
        this(CreateSprings.MODID, location, startX, startY, width, height);
    }

    CSpringsGuiTextures(String namespace, String location, int startX, int startY, int width, int height) {
        this.location = ResourceLocation.fromNamespaceAndPath(namespace, "textures/gui/" + location + ".png");
        this.width = width;
        this.height = height;
        this.startX = startX;
        this.startY = startY;
    }

    @Override
    public int getStartX() {
        return startX;
    }

    @Override
    public int getStartY() {
        return startY;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public ResourceLocation getLocation() {
        return location;
    }

    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y) {
        graphics.blit(location, x, y, startX, startY, width, height);
    }

    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y, Color c) {
        bind();
        UIRenderHelper.drawColoredTexture(graphics, c, x, y, startX, startY, width, height);
    }
}
