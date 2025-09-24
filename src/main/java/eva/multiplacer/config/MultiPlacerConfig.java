package eva.multiplacer.config;

public class MultiPlacerConfig {

    private static MultiPlacerConfig INSTANCE;
//    private static MultiPlacerConfig SERVER_INSTANCE;
    private static final boolean[] readSinceLastUpdate = {false, false, false, false, false, false, false};

    private MultiPlacerConfig() {}

    public MultiPlacerConfig(boolean hi) {

    }

    public static MultiPlacerConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MultiPlacerConfig();
        }
        return INSTANCE;
    }

    public void updateConfigs(MultiPlacerConfig config) {

    }

//    public static MultiPlacerConfig getServerInstance() {
//        return SERVER_INSTANCE;
//    }

//    private static void setServerInstance(MultiPlacerConfig newInstance) {
//        SERVER_INSTANCE = newInstance;
//    }
//    static List<String> getDef() {
//        return def;
//    }

//    public boolean[] getAllowWeakConfig() {
//        return this.allowWeakConfig;
//    }
//
//    public boolean getAllowWeakConfig(int i) {
//        return this.allowWeakConfig[i];
//    }
//
//    public static void disconnect() {
//        if (SERVER_INSTANCE != null)
//            SERVER_INSTANCE = null;
//    }


//    private static class ErrorSupplier implements Function<String, Optional<Component>> {
//        @Override
//        public Optional<Component> apply(String entry) {
//            entry = entry.toLowerCase();
//            if (entry.indexOf(':') != entry.lastIndexOf(':'))
//                return Optional.of(Component.literal("Enter a valid item ID! error code: 1"));
//            String name = entry.substring(entry.indexOf(':') + 1).replaceAll("[^a-z0-9/._-]", "");
//            if (!entry.equals(name)) {
//                if (entry.indexOf(':') == -1)
//                    return Optional.of(Component.literal("Enter a valid item ID! error code: 2"));
//                String namespace = entry.substring(0,entry.indexOf(':')).replaceAll("[^a-z0-9/._-]", "");
//                if (!entry.equals(namespace + ":" + name)) {
//                    return Optional.of(Component.literal("Enter a valid item ID! error code: 3"));
//                }
//            }
//            ResourceLocation id = ResourceLocation.parse(entry);
//            if (!BuiltInRegistries.ITEM.keySet().contains(id))
//                return Optional.of(Component.literal("Enter a valid item ID! error code: 4"));
//            return Optional.empty();
//        }
//    }

//    static Function<String, Optional<Component>> getErrorSupplier() {
//        return new ErrorSupplier();
//    }

}
