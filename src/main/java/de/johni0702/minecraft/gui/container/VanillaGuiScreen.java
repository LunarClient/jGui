package de.johni0702.minecraft.gui.container;

import de.johni0702.minecraft.gui.function.Draggable;
import de.johni0702.minecraft.gui.function.Scrollable;
import de.johni0702.minecraft.gui.function.Typeable;
import de.johni0702.minecraft.gui.utils.EventRegistrations;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadablePoint;
import de.johni0702.minecraft.gui.versions.MCVer;

//#if MC>=11400
//$$ import de.johni0702.minecraft.gui.utils.MouseUtils;
//$$ import de.johni0702.minecraft.gui.utils.lwjgl.Point;
//$$ import de.johni0702.minecraft.gui.versions.callbacks.InitScreenCallback;
//$$ import de.johni0702.minecraft.gui.versions.callbacks.OpenGuiScreenCallback;
//$$ import de.johni0702.minecraft.gui.versions.callbacks.PreTickCallback;
//$$ import net.minecraft.client.gui.screen.Screen;
//#endif

//#if FABRIC>=1
//$$ import de.johni0702.minecraft.gui.versions.callbacks.KeyboardCallback;
//$$ import de.johni0702.minecraft.gui.versions.callbacks.MouseCallback;
//$$ import de.johni0702.minecraft.gui.versions.callbacks.PostRenderScreenCallback;
//$$ import de.johni0702.minecraft.gui.versions.MatrixStack;
//#else
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
//#endif

//#if MC<10800
//$$ import cpw.mods.fml.common.eventhandler.Cancelable;
//$$ import cpw.mods.fml.common.eventhandler.Event;
//$$ import cpw.mods.fml.common.eventhandler.SubscribeEvent;
//#endif

//#if MC<11400
import net.minecraftforge.common.MinecraftForge;
import java.io.IOException;
//#endif

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;


public class VanillaGuiScreen extends GuiScreen implements Draggable, Typeable, Scrollable {

    private static final Map<net.minecraft.client.gui.GuiScreen, VanillaGuiScreen> WRAPPERS =
            Collections.synchronizedMap(new WeakHashMap<>());

    public static VanillaGuiScreen wrap(net.minecraft.client.gui.GuiScreen originalGuiScreen) {
        VanillaGuiScreen gui = WRAPPERS.get(originalGuiScreen);
        if (gui == null) {
            WRAPPERS.put(originalGuiScreen, gui = new VanillaGuiScreen(originalGuiScreen));
            gui.register();
        }
        return gui;
    }

    // Use wrap instead and make sure to preserve the existing layout.
    // (or if you really want your own, inline this code)
    @Deprecated
    public static VanillaGuiScreen setup(net.minecraft.client.gui.GuiScreen originalGuiScreen) {
        VanillaGuiScreen gui = new VanillaGuiScreen(originalGuiScreen);
        gui.register();
        return gui;
    }

    private final net.minecraft.client.gui.GuiScreen mcScreen;
    private final EventHandler eventHandler = new EventHandler();

    public VanillaGuiScreen(net.minecraft.client.gui.GuiScreen mcScreen) {
        this.mcScreen = mcScreen;
        this.suppressVanillaKeys = true;

        super.setBackground(Background.NONE);
    }

    // Needs to be called from or after GuiInitEvent.Post, will auto-unregister on any GuiOpenEvent
    public void register() {
        if (!eventHandler.active) {
            eventHandler.active = true;

            eventHandler.register();

            getSuperMcGui().setWorldAndResolution(MCVer.getMinecraft(), mcScreen.width, mcScreen.height);
        }
    }

    public void display() {
        getMinecraft().displayGuiScreen(mcScreen);
        register();
    }

    @Override
    public net.minecraft.client.gui.GuiScreen toMinecraft() {
        return mcScreen;
    }

    @Override
    public void setBackground(Background background) {
        throw new UnsupportedOperationException("Cannot set background of vanilla gui screen.");
    }

    private net.minecraft.client.gui.GuiScreen getSuperMcGui() {
        return super.toMinecraft();
    }

    @Override
    public boolean mouseClick(ReadablePoint position, int button) {
        //#if MC>=11400
        //#else
        eventHandler.handled = false;
        //#endif
        return false;
    }

    @Override
    public boolean mouseDrag(ReadablePoint position, int button, long timeSinceLastCall) {
        //#if MC>=11400
        //#else
        eventHandler.handled = false;
        //#endif
        return false;
    }

    @Override
    public boolean mouseRelease(ReadablePoint position, int button) {
        //#if MC>=11400
        //#else
        eventHandler.handled = false;
        //#endif
        return false;
    }

    @Override
    public boolean scroll(ReadablePoint mousePosition, int dWheel) {
        //#if MC>=11400
        //#else
        eventHandler.handled = false;
        //#endif
        return false;
    }

    @Override
    public boolean typeKey(ReadablePoint mousePosition, int keyCode, char keyChar, boolean ctrlDown, boolean shiftDown) {
        //#if MC>=11400
        //#else
        eventHandler.handled = false;
        //#endif
        return false;
    }

    // Used when wrapping an already existing mc.GuiScreen
    //#if MC>=10800
    private
    //#else
    //$$ public
    //#endif
    class EventHandler extends EventRegistrations
        //#if FABRIC>=1
        //$$ implements KeyboardCallback, MouseCallback
        //#endif
    {
        private boolean active;

        //#if FABRIC>=1
        //$$ { on(OpenGuiScreenCallback.EVENT, screen -> onGuiClosed()); }
        //$$ private void onGuiClosed() {
        //#else
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public void onGuiClosed(GuiOpenEvent event) {
        //#endif
            unregister();

            if (active) {
                active = false;
                getSuperMcGui().onGuiClosed();
                WRAPPERS.remove(mcScreen, VanillaGuiScreen.this);
            }
        }

        //#if FABRIC>=1
        //$$ { on(InitScreenCallback.Pre.EVENT, this::preGuiInit); }
        //$$ private void preGuiInit(Screen screen) {
        //#else
        @SubscribeEvent
        public void preGuiInit(GuiScreenEvent.InitGuiEvent.Pre event) {
            //#if MC>=10904
            //$$ net.minecraft.client.gui.GuiScreen screen = event.getGui();
            //#else
            net.minecraft.client.gui.GuiScreen screen = event.gui;
            //#endif
        //#endif
            if (screen == mcScreen && active) {
                active = false;
                unregister();
                getSuperMcGui().onGuiClosed();
                WRAPPERS.remove(mcScreen, VanillaGuiScreen.this);
            }
        }

        //#if FABRIC>=1
        //$$ { on(PostRenderScreenCallback.EVENT, this::onGuiRender); }
        //$$ private void onGuiRender(MatrixStack stack, float partialTicks) {
        //$$     Point mousePos = MouseUtils.getMousePos();
        //$$     getSuperMcGui().render(
                    //#if MC>=11600
                    //$$ stack,
                    //#endif
        //$$             mousePos.getX(), mousePos.getY(), partialTicks);
        //$$ }
        //#else
        @SubscribeEvent
        public void onGuiRender(GuiScreenEvent.DrawScreenEvent.Post event) {
            //#if MC>=11400
            //$$ getSuperMcGui().render(MCVer.getMouseX(event), MCVer.getMouseY(event), MCVer.getPartialTicks(event));
            //#else
            getSuperMcGui().drawScreen(MCVer.getMouseX(event), MCVer.getMouseY(event), MCVer.getPartialTicks(event));
            //#endif
        }
        //#endif

        //#if FABRIC>=1
        //$$ { on(PreTickCallback.EVENT, this::tickOverlay); }
        //$$ private void tickOverlay() {
        //#else
        @SubscribeEvent
        public void tickOverlay(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.START) return;
        //#endif
            //#if MC>=11400
            //$$ getSuperMcGui().tick();
            //#else
            getSuperMcGui().updateScreen();
            //#endif
        }

        //#if FABRIC>=1
        //$$ { on(MouseCallback.EVENT, this); }
        //$$
        //$$ @Override
        //$$ public boolean mouseDown(double x, double y, int button) {
        //$$     return getSuperMcGui().mouseClicked(x, y, button);
        //$$ }
        //$$
        //$$ @Override
        //$$ public boolean mouseDrag(double x, double y, int button, double dx, double dy) {
        //$$     return getSuperMcGui().mouseDragged(x, y, button, dx, dy);
        //$$ }
        //$$
        //$$ @Override
        //$$ public boolean mouseUp(double x, double y, int button) {
        //$$     return getSuperMcGui().mouseReleased(x, y, button);
        //$$ }
        //$$
        //$$ @Override
        //$$ public boolean mouseScroll(double x, double y, double scroll) {
        //$$     return getSuperMcGui().mouseScrolled(x, y, scroll);
        //$$ }
        //$$
        //$$ { on(KeyboardCallback.EVENT, this); }
        //$$
        //$$ @Override
        //$$ public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        //$$     return getSuperMcGui().keyPressed(keyCode, scanCode, modifiers);
        //$$ }
        //$$
        //$$ @Override
        //$$ public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        //$$     return getSuperMcGui().keyReleased(keyCode, scanCode, modifiers);
        //$$ }
        //$$
        //$$ @Override
        //$$ public boolean charTyped(char keyChar, int scanCode) {
        //$$     return getSuperMcGui().charTyped(keyChar, scanCode);
        //$$ }
        //#else
        //#if MC>=11400
        //$$ @SubscribeEvent(priority = EventPriority.HIGH)
        //$$ public void onMouseClick(GuiScreenEvent.MouseClickedEvent.Pre event) {
        //$$     if (getSuperMcGui().mouseClicked(event.getMouseX(), event.getMouseY(), event.getButton())) {
        //$$         event.setCanceled(true);
        //$$     }
        //$$ }
        //$$
        //$$ @SubscribeEvent(priority = EventPriority.HIGH)
        //$$ public void onMouseReleased(GuiScreenEvent.MouseReleasedEvent.Pre event) {
        //$$     if (getSuperMcGui().mouseReleased(event.getMouseX(), event.getMouseY(), event.getButton())) {
        //$$         event.setCanceled(true);
        //$$     }
        //$$ }
        //$$
        //$$ @SubscribeEvent(priority = EventPriority.HIGH)
        //$$ public void onMouseDrag(GuiScreenEvent.MouseDragEvent.Pre event) {
        //$$     if (getSuperMcGui().mouseDragged(event.getMouseX(), event.getMouseY(), event.getMouseButton(), event.getDragX(), event.getDragY())) {
        //$$         event.setCanceled(true);
        //$$     }
        //$$ }
        //$$
        //$$ @SubscribeEvent(priority = EventPriority.HIGH)
        //$$ public void onMouseScroll(GuiScreenEvent.MouseScrollEvent.Pre event) {
        //$$     if (getSuperMcGui().mouseScrolled(
                        //#if MC>=11400
                        //$$ event.getMouseX(), event.getMouseY(),
                        //#endif
        //$$             event.getScrollDelta())) {
        //$$         event.setCanceled(true);
        //$$     }
        //$$ }
        //$$
        //$$ @SubscribeEvent(priority = EventPriority.HIGH)
        //$$ public void onKeyPressed(GuiScreenEvent.KeyboardKeyPressedEvent.Pre event) {
        //$$     if (getSuperMcGui().keyPressed(event.getKeyCode(), event.getModifiers(), event.getScanCode())) {
        //$$         event.setCanceled(true);
        //$$     }
        //$$ }
        //$$
        //$$ @SubscribeEvent(priority = EventPriority.HIGH)
        //$$ public void onKeyReleased(GuiScreenEvent.KeyboardKeyReleasedEvent.Pre event) {
        //$$     if (getSuperMcGui().keyReleased(event.getKeyCode(), event.getModifiers(), event.getScanCode())) {
        //$$         event.setCanceled(true);
        //$$     }
        //$$ }
        //$$
        //$$ @SubscribeEvent(priority = EventPriority.HIGH)
        //$$ public void onCharTyped(GuiScreenEvent.KeyboardCharTypedEvent.Pre event) {
        //$$     if (getSuperMcGui().charTyped(event.getCodePoint(), event.getModifiers())) {
        //$$         event.setCanceled(true);
        //$$     }
        //$$ }
        //#else
        private boolean handled;

        // Mouse/Keyboard events aren't supported in 1.7.10
        // so this requires a mixin in any mod making use of it
        // (see ReplayMod: GuiScreenMixin)
        @SubscribeEvent(priority = EventPriority.LOWEST)
        //#if MC>=10800
        public void onMouseInput(GuiScreenEvent.MouseInputEvent.Pre event) throws IOException {
        //#else
        //$$ public void onMouseInput(MouseInputEvent event) throws IOException {
        //#endif
            handled = true;
            getSuperMcGui().handleMouseInput();
            if (handled) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        //#if MC>=10800
        public void onKeyboardInput(GuiScreenEvent.KeyboardInputEvent.Pre event) throws IOException {
        //#else
        //$$ public void onKeyboardInput(KeyboardInputEvent event) throws IOException {
        //#endif
            handled = true;
            getSuperMcGui().handleKeyboardInput();
            if (handled) {
                event.setCanceled(true);
            }
        }
        //#endif
        //#endif
    }
    //#if MC<=10710
    //$$ @Cancelable
    //$$ public static class MouseInputEvent extends Event {}
    //$$ @Cancelable
    //$$ public static class KeyboardInputEvent extends Event {}
    //#endif
}
