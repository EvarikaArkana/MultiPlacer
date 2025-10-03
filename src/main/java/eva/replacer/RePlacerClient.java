package eva.replacer;

import com.mojang.blaze3d.platform.InputConstants;
import com.terraformersmc.modmenu.ModMenu;
import eva.replacer.config.JsonConfigHelper;
import eva.replacer.rendering.BlockHighlightRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static eva.replacer.config.JsonConfigHelper.writeSquare;
import static eva.replacer.config.RePlacerConfig.*;

public class RePlacerClient implements ClientModInitializer {
    public static final String MOD_ID = RePlacerMain.MOD_ID + "-client";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static KeyMapping modifierBind;
    public static KeyMapping cycleBind;
    public static KeyMapping modifierToggle;
    private static boolean toggled = false;
    private static boolean modMenu = false;
    private static final KeyMapping.Category REPLACER_KEYS = new KeyMapping.Category(ResourceLocation.tryBuild("eva", "replacer"));
    @Override
    public void onInitializeClient() {

        JsonConfigHelper.init();

        modMenu = FabricLoader.getInstance().isModLoaded("modmenu");
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
         modifierBind = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "RePlacer Modifier Key",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_ALT,
                 REPLACER_KEYS
         ));
         cycleBind = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                 "RePlacer Cycle Key",
                 InputConstants.Type.KEYSYM,
                 GLFW.GLFW_KEY_Z,
                 REPLACER_KEYS
         ));
         modifierToggle = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                 "RePlacer Modifier Toggle Key",
                 InputConstants.Type.KEYSYM,
                 GLFW.GLFW_KEY_UNKNOWN,
                 REPLACER_KEYS
         ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            assert client.player != null;
            if (modMenu && modifierBind.isDown() && cycleBind.consumeClick()) {
                if (reCording) {
                    client.setScreen(ModMenu.getConfigScreen("rp", client.screen));
                } else {
                    client.player.displayClientMessage(Component.literal("Recording started!"), true);
                    reCording = true;
                }
            } else if (cycleBind.consumeClick()) {
                if (client.player.isShiftKeyDown()) {
                    selection--;
                    if (selection < 0) selection = getNames().size() - 1;
                } else {
                    selection++;
                    if (selection >= getNames().size()) selection = 0;
                }
                try {
                    client.player.displayClientMessage(Component.literal("swapped to " + getNames().get(selection)), true);
                } catch (Exception ignored) {
                    client.player.displayClientMessage(Component.literal("No builds registered!"), false);
                    client.player.displayClientMessage(Component.literal("Loading default build"), false);
                    writeSquare();
                    selection = 0;
                    client.player.displayClientMessage(Component.literal("swapped to square"), true);
                }
            }
            if (modifierToggle.consumeClick()) {
                toggled = !toggled;
            }
        });
    }

    public static boolean isHeldOrToggled() {
        return toggled || modifierBind.isDown();
    }
}