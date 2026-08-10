package GuZhenRen.enums;

public enum FinalBossChoice {
    HEART,
    LONG_GONG,
    RANDOM;

    public static FinalBossChoice fromString(String raw) {
        if (raw == null) return RANDOM;
        for (FinalBossChoice choice : values()) {
            if (choice.name().equalsIgnoreCase(raw.trim())) {
                return choice;
            }
        }
        return RANDOM;
    }

    public static FinalBossChoice fromOrdinal(Integer raw, FinalBossChoice fallback) {
        if (raw == null) return fallback;
        FinalBossChoice[] vals = values();
        return (raw >= 0 && raw < vals.length) ? vals[raw] : fallback;
    }
}