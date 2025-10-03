package eva.replacer.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.network.chat.Component;

import java.util.List;

import static eva.replacer.config.RePlacerConfig.*;

public class RePlacerConfigScreen implements ModMenuApi {
    public static ConfigBuilder builder() {
        ConfigBuilder builder = ConfigBuilder.create()
                .setTitle(Component.literal("RePlacer Config"))
                .setSavingRunnable(JsonConfigHelper::writeToConfig)
                .setEditable(true);
        if (reCording) {
            builder.getOrCreateCategory(Component.literal("Confirm build save?"))
                    .addEntry(builder.entryBuilder()
                            .startStrField(Component.literal("Build Name:"), "")
                            .setTooltip(Component.literal("This will be the build's name.\nThe build is what you just made!"))
                            .setSaveConsumer(newName -> buildName = newName)
                            .build()
                    )
                    .addEntry(builder.entryBuilder()
                            .startBooleanToggle(Component.literal("Click this to allow current build to be discarded"), false)
                            .setTooltip(Component.literal("If this is positive, your build will be deleted!"))
                            .setSaveConsumer(RePlacerConfig::saveBuild)
                            .build()
                    );
        } else {
            builder.getOrCreateCategory(Component.literal("Options"))
                    .addEntry(builder.entryBuilder()
                            .startBooleanToggle(Component.literal("Placement-based rotation:"), isRotatePlace())
                            .setTooltip(Component.literal("Builds 'sprout' out of the block you place them on"))
                            .setSaveConsumer(RePlacerConfig::setRotatePlace)
                            .setDefaultValue(true)
                            .build()
                    )
                    .addEntry(builder.entryBuilder()
                            .startBooleanToggle(Component.literal("Player direction-based rotation:"), isRotateFace())
                            .setTooltip(Component.literal("Builds go spin\nOnly affects builds saved vertically or placed vertically."))
                            .setSaveConsumer(RePlacerConfig::setRotateFace)
                            .setDefaultValue(true)
                            .build()
                    );
            builder.getOrCreateCategory(Component.literal("ReCorder"))
                    .addEntry(builder.entryBuilder()
                            .startBooleanToggle(Component.literal("ReCord on Menu Close:"), reCording)
                            .setSaveConsumer(value -> reCording = value)
                            .setTooltip(Component.literal("If this is on, your block\nplacements will be recorded\nas a build until you turn\nit off"))
                            .build()
                    );
            builder.getOrCreateCategory(Component.literal("Build Management"))
                    .addEntry(builder.entryBuilder()
                            .startStrList(Component.literal("Build Names:"), getNames())
                            .setDeleteButtonEnabled(true)
                            .setRemoveButtonTooltip(Component.literal("Deleting this will delete the build!"))
                            .setExpanded(true)
                            .setSaveConsumer(RePlacerConfig::buildDeleter)
                            .setDefaultValue(List.of("square"))
                            .build()
                    );
        }
        return builder;
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            // Return the screen here with the one you created from Cloth Config Builder
            return builder().setParentScreen(parent).build();
        };
    }

}