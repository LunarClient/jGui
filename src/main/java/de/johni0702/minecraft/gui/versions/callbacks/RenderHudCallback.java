//#if FABRIC>=1
//$$ package de.johni0702.minecraft.gui.versions.callbacks;
//$$
//$$ import de.johni0702.minecraft.gui.utils.Event;
//$$ import de.johni0702.minecraft.gui.versions.MatrixStack;
//$$
//$$ public interface RenderHudCallback {
//$$     Event<RenderHudCallback> EVENT = Event.create((listeners) ->
//$$             (stack, partialTicks) -> {
//$$                 for (RenderHudCallback listener : listeners) {
//$$                     listener.renderHud(stack, partialTicks);
//$$                 }
//$$             }
//$$     );
//$$
//$$     void renderHud(MatrixStack stack, float partialTicks);
//$$ }
//#endif
