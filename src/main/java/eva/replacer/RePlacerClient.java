package eva.replacer;

import com.mojang.blaze3d.platform.InputConstants;
import eva.replacer.config.JsonConfigHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static eva.replacer.ItemStackAccess.findFirst;
import static eva.replacer.config.RePlacerConfig.*;

public class RePlacerClient implements ClientModInitializer {
    public static final String MOD_ID = RePlacerMain.MOD_ID + "-client";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static KeyMapping modifierBind;
    public static KeyMapping cycleBind;
    @Override
    public void onInitializeClient() {

        JsonConfigHelper.init();
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
         modifierBind = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "RePlacer Modifier Key",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_ALT,
                "RePlacer"
         ));
         cycleBind = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                 "RePlacer Cycle Key",
                 InputConstants.Type.KEYSYM,
                 GLFW.GLFW_KEY_Z,
                 "RePlacer"
         ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (ItemStackAccess.player[0] == null) ItemStackAccess.passPlayer(client.player);
            if (cycleBind.consumeClick()) {
                assert client.player != null;
                if (client.player.isShiftKeyDown()) {
                    selection--;
                    if (selection < 0) selection = getNames().size() - 1;
                } else {
                    selection++;
                    if (selection >= getNames().size()) selection = 0;
                }
                if (isRotate()) findFirst();
                try {
                    client.player.displayClientMessage(Component.literal("swapped to " + getNames().get(selection)), true);
                } catch (Exception ignored) {
                    client.player.displayClientMessage(Component.literal("No builds registered!"), true);
                }
            }
        });
    }
}