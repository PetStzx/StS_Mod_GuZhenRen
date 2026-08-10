package GuZhenRen.util;

import GuZhenRen.GuZhenRen;
import GuZhenRen.enums.FinalBossChoice;
import GuZhenRen.patches.AbstractPlayerEnum;
import GuZhenRen.relics.AbstractKongQiao;
import basemod.abstracts.CustomSavable;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.io.IOException;
import java.util.Properties;

public class FinalBossChoiceManager implements CustomSavable<Integer> {
    public static final String SAVE_KEY = GuZhenRen.makeID("FinalBossChoice");
    private static final FinalBossChoiceManager INSTANCE = new FinalBossChoiceManager();

    private static SpireConfig modConfig;
    private static FinalBossChoice menuChoice = FinalBossChoice.RANDOM;

    private static FinalBossChoice currentRunChoice = null;

    public static FinalBossChoiceManager getInstance() {
        return INSTANCE;
    }

    public static void loadGlobalConfig() {
        try {
            Properties defaults = new Properties();
            defaults.setProperty("FinalBossPreference", FinalBossChoice.RANDOM.name());
            modConfig = new SpireConfig("GuZhenRen", "GeneralConfig", defaults);
            modConfig.load();
            menuChoice = FinalBossChoice.fromString(modConfig.getString("FinalBossPreference"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveGlobalConfig(FinalBossChoice choice) {
        menuChoice = choice;
        try {
            if (modConfig != null) {
                modConfig.setString("FinalBossPreference", choice.name());
                modConfig.save();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FinalBossChoice getMenuChoice() {
        return menuChoice;
    }

    public static boolean canChooseFinalBoss(AbstractPlayer.PlayerClass playerClass) {
        return playerClass == AbstractPlayerEnum.FANG_YUAN;
    }

    public static boolean shouldUseLongGong() {
        if (AbstractDungeon.player == null || AbstractDungeon.player.chosenClass != AbstractPlayerEnum.FANG_YUAN) {
            return false;
        }

        FinalBossChoice choiceToEvaluate = (currentRunChoice != null) ? currentRunChoice : menuChoice;

        if (choiceToEvaluate == FinalBossChoice.RANDOM) {
            int rank = AbstractKongQiao.getCurrentRank();
            if (rank <= 5) return false;

            int chance = Math.min((rank - 5) * 25, 100);
            return AbstractDungeon.miscRng.random(1, 100) <= chance;
        }

        return choiceToEvaluate == FinalBossChoice.LONG_GONG;
    }

    @Override
    public Integer onSave() {
        return (currentRunChoice != null) ? currentRunChoice.ordinal() : menuChoice.ordinal();
    }

    @Override
    public void onLoad(Integer integer) {
        currentRunChoice = FinalBossChoice.fromOrdinal(integer, FinalBossChoice.RANDOM);
    }

    public static void clearCurrentRunChoice() {
        currentRunChoice = null;
    }
}