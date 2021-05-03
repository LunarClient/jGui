//#if FABRIC>=1
//$$ package de.johni0702.minecraft.gui.versions.callbacks;
//$$
//$$ import de.johni0702.minecraft.gui.utils.Event;
//$$ import de.johni0702.minecraft.gui.versions.MatrixStack;
//$$
//$$ public interface PostRenderScreenCallback {
//$$     Event<PostRenderScreenCallback> EVENT = Event.create((listeners) ->
//$$             (stack, partialTicks) -> {
//$$                 for (PostRenderScreenCallback listener : listeners) {
//$$                     listener.postRenderScreen(stack, partialTicks);
//$$                 }
//$$             }
//$$     );
//$$
//$$     void postRenderScreen(MatrixStack stack, float partialTicks);
//$$ }
//#endif
