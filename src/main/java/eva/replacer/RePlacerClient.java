package eva.replacer;

import com.mojang.blaze3d.platform.InputConstants;
import eva.replacer.config.JsonConfigHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RePlacerClient implements ClientModInitializer {
    public static final String MOD_ID = RePlacerMain.MOD_ID + "-client";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static KeyMapping modifierBind;
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

    }
}