/*
 * This file is part of jGui API, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2016 johni0702 <https://github.com/johni0702>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.johni0702.minecraft.gui;

import de.johni0702.minecraft.gui.utils.NonNull;
import de.johni0702.minecraft.gui.utils.lwjgl.Color;
import de.johni0702.minecraft.gui.utils.lwjgl.Point;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadableColor;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadableDimension;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadablePoint;
import de.johni0702.minecraft.gui.utils.lwjgl.WritableDimension;
import de.johni0702.minecraft.gui.versions.MCVer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import de.johni0702.minecraft.gui.versions.MatrixStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

//#if MC>=11400
//$$ import net.minecraft.client.MainWindow;
//#else
import net.minecraft.client.gui.ScaledResolution;
//#endif

//#if MC>=10800
import net.minecraft.client.renderer.GlStateManager;
import static net.minecraft.client.renderer.GlStateManager.*;
//#else
//$$ import net.minecraft.client.renderer.OpenGlHelper;
//$$ import static de.johni0702.minecraft.gui.versions.MCVer.*;
//#endif

import static de.johni0702.minecraft.gui.versions.MCVer.*;
import static org.lwjgl.opengl.GL11.*;

public class MinecraftGuiRenderer implements GuiRenderer {

    private final Gui gui = new Gui(){};

    //#if MC<11600
    @SuppressWarnings("FieldCanBeLocal")
    //#endif
    private final MatrixStack matrixStack;

    @NonNull
    //#if MC>=11400
    //$$ private final MainWindow size;
    //#else
    private final ScaledResolution size;
    //#endif

    //#if MC>=11400
    //$$ public MinecraftGuiRenderer(MatrixStack matrixStack, MainWindow size) {
    //#else
    public MinecraftGuiRenderer(MatrixStack matrixStack, ScaledResolution size) {
    //#endif
        this.matrixStack = matrixStack;
        this.size = size;
    }

    @Override
    public ReadablePoint getOpenGlOffset() {
        return new Point(0, 0);
    }

    @Override
    public MatrixStack getMatrixStack() {
        return matrixStack;
    }

    @Override
    public ReadableDimension getSize() {
        return new ReadableDimension() {
            @Override
            public int getWidth() {
                return size.getScaledWidth();
            }

            @Override
            public int getHeight() {
                return size.getScaledHeight();
            }

            @Override
            public void getSize(WritableDimension dest) {
                dest.setSize(getWidth(), getHeight());
            }
        };
    }

    @Override
    public void setDrawingArea(int x, int y, int width, int height) {
        // glScissor origin is bottom left corner whereas otherwise it's top left
        y = size.getScaledHeight() - y - height;

        //#if MC>=11400
        //$$ int f = (int) size.getGuiScaleFactor();
        //#else
        int f = size.getScaleFactor();
        //#endif
        GL11.glScissor(x * f, y * f, width * f, height * f);
    }

    @Override
    public void bindTexture(ResourceLocation location) {
        //#if MC>=11500
        //$$ MCVer.getMinecraft().getTextureManager().bindTexture(location);
        //#else
        MCVer.getMinecraft().getTextureManager().bindTexture(location);
        //#endif
    }

    @Override
    public void bindTexture(int glId) {
        //#if MC>=10800
        GlStateManager.bindTexture(glId);
        //#else
        //$$ GL11.glBindTexture(GL_TEXTURE_2D, glId);
        //#endif
    }

    @Override
    public void drawTexturedRect(int x, int y, int u, int v, int width, int height) {
        //#if MC>=11600
        //$$ gui.drawTexture(matrixStack, x, y, u, v, width, height);
        //#else
        //#if MC>=11400
        //$$ gui.blit(x, y, u, v, width, height);
        //#else
        gui.drawTexturedModalRect(x, y, u, v, width, height);
        //#endif
        //#endif
    }

    @Override
    public void drawTexturedRect(int x, int y, int u, int v, int width, int height, int uWidth, int vHeight, int textureWidth, int textureHeight) {
        color(1, 1, 1);
        //#if MC>=11600
        //$$ DrawableHelper.drawTexture(matrixStack, x, y, width, height, u, v, uWidth, vHeight, textureWidth, textureHeight);
        //#else
        //#if MC>=11400
        //$$ AbstractGui.blit(x, y, width, height, u, v, uWidth, vHeight, textureWidth, textureHeight);
        //#else
        Gui.drawScaledCustomSizeModalRect(x, y, u, v, uWidth, vHeight, width, height, textureWidth, textureHeight);
        //#endif
        //#endif
    }

    @Override
    public void drawRect(int x, int y, int width, int height, int color) {
        Gui.drawRect(
                //#if MC>=11600
                //$$ matrixStack,
                //#endif
                x, y, x + width, y + height, color);
        color(1, 1, 1);
        enableBlend();
    }

    @Override
    public void drawRect(int x, int y, int width, int height, ReadableColor color) {
        drawRect(x, y, width, height, color(color));
    }

    @Override
    public void drawRect(int x, int y, int width, int height, int topLeftColor, int topRightColor, int bottomLeftColor, int bottomRightColor) {
        drawRect(x, y, width, height, color(topLeftColor), color(topRightColor), color(bottomLeftColor), color(bottomRightColor));
    }

    @Override
    public void drawRect(int x, int y, int width, int height, ReadableColor tl, ReadableColor tr, ReadableColor bl, ReadableColor br) {
        disableTexture2D();
        enableBlend();
        disableAlpha();
        tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        shadeModel(GL_SMOOTH);
        MCVer.drawRect(x, y, width, height, tl, tr, bl, br);
        shadeModel(GL_FLAT);
        enableAlpha();
        enableTexture2D();
    }

    @Override
    public int drawString(int x, int y, int color, String text) {
        return drawString(x, y, color, text, false);
    }

    @Override
    public int drawString(int x, int y, ReadableColor color, String text) {
        return drawString(x, y, color(color), text);
    }

    @Override
    public int drawCenteredString(int x, int y, int color, String text) {
        return drawCenteredString(x, y, color, text, false);
    }

    @Override
    public int drawCenteredString(int x, int y, ReadableColor color, String text) {
        return drawCenteredString(x, y, color(color), text);
    }

    @Override
    public int drawString(int x, int y, int color, String text, boolean shadow) {
        FontRenderer fontRenderer = MCVer.getFontRenderer();
        try {
            if (shadow) {
                return fontRenderer.drawStringWithShadow(
                        //#if MC>=11600
                        //$$ matrixStack,
                        //#endif
                        text, x, y, color);
            } else {
                return fontRenderer.drawString(
                        //#if MC>=11600
                        //$$ matrixStack,
                        //#endif
                        text, x, y, color);
            }
        } finally {
            color(1, 1, 1);
        }
    }

    @Override
    public int drawString(int x, int y, ReadableColor color, String text, boolean shadow) {
        return drawString(x, y, color(color), text, shadow);
    }

    @Override
    public int drawCenteredString(int x, int y, int color, String text, boolean shadow) {
        FontRenderer fontRenderer = MCVer.getFontRenderer();
        x-=fontRenderer.getStringWidth(text) / 2;
        return drawString(x, y, color, text, shadow);
    }

    @Override
    public int drawCenteredString(int x, int y, ReadableColor color, String text, boolean shadow) {
        return drawCenteredString(x, y, color(color), text, shadow);
    }

    private int color(ReadableColor color) {
        return color.getAlpha() << 24
                | color.getRed() << 16
                | color.getGreen() << 8
                | color.getBlue();
    }

    private ReadableColor color(int color) {
        return new Color((color >> 16) & 0xff, (color >> 8) & 0xff, color & 0xff, (color >> 24) & 0xff);
    }

    private void color(int r, int g, int b) {
        //#if MC>=10800
        //#if MC>=11400
        //$$ GlStateManager.color4f(r, g, b, 1);
        //#else
        GlStateManager.color(r, g, b);
        //#endif
        //#else
        //$$ MCVer.color(r, g, b);
        //#endif
    }
}
