package eva.replacer.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.network.chat.Component;

import static eva.replacer.config.RePlacerConfig.*;

public class RePlacerConfigScreen implements ModMenuApi {
    public static ConfigBuilder builder() {
        ConfigBuilder builder = ConfigBuilder.create()
                .setTitle(Component.literal("Simply Dual Wielding Config"))
                .setSavingRunnable(JsonConfigHelper::writeToConfig)
                .setEditable(true);
        RePlacerConfig INSTANCE = getInstance();
        if (reCording)
            builder.getOrCreateCategory(Component.literal("Confirm build save?"))
                            .addEntry(builder.entryBuilder()
                                    .startBooleanToggle(Component.literal("Are you sure you want to save " + buildName + " as a build?"), false)
                                    .setSaveConsumer(RePlacerConfig::saveBuild)
                                    .build()
                            );
        else {
            builder.getOrCreateCategory(Component.literal("Options"))
                    .addEntry(builder.entryBuilder()
                            .startBooleanToggle(Component.literal("Rotate builds:"), isRotate())
                            .setTooltip(Component.literal("Rotate builds along all three axes"))
                            .setSaveConsumer(RePlacerConfig::setRotate)
                            .build()
                    );
            builder.getOrCreateCategory(Component.literal("ReCorder"))
                    .addEntry(builder.entryBuilder()
                            .startStrField(Component.literal("Build Name:"), buildName)
                            .setTooltip(Component.literal("When you save a build,\nwhatever's in this field\nwill be its name."))
                            .setSaveConsumer(newName -> buildName = newName)
                            .build()
                    )
                    .addEntry(builder.entryBuilder()
                            .startBooleanToggle(Component.literal("ReCord on Menu Close:"), reCording)
                            .setSaveConsumer(value -> reCording = value)
                            .setTooltip(Component.literal("If this is on, your block\nplacements will be recorded\nas a build until you turn\nit off"))
                            .build()
                    );
            builder.getOrCreateCategory(Component.literal("Build Management"))
                    .addEntry(builder.entryBuilder()
                            .startStrList(Component.literal("Build Names:"), buildNames())
                            .setDeleteButtonEnabled(true)
                            .setRemoveButtonTooltip(Component.literal("Deleting this will delete the build!"))
                            .setExpanded(true)
                            .setSaveConsumer(RePlacerConfig::saveBuilds)
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