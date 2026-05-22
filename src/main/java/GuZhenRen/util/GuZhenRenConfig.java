package GuZhenRen.util;

import GuZhenRen.GuZhenRen;
import basemod.BaseMod;
import basemod.ModLabeledToggleButton;
import basemod.ModPanel;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;

import java.util.Properties;

import static GuZhenRen.GuZhenRen.logger;

public class GuZhenRenConfig {
    public static SpireConfig modConfig;

    public static boolean quMode = false;
    public static boolean bgm9 = true;
    public static boolean bgm10 = true;

    public static void loadConfig() {
        try {
            Properties defaults = new Properties();
            defaults.setProperty("quMode", "false");
            defaults.setProperty("bgm9", "true");
            defaults.setProperty("bgm10", "true");
            modConfig = new SpireConfig(GuZhenRen.MOD_ID, "GuZhenRenConfig", defaults);
            quMode = modConfig.getBool("quMode");
            bgm9 = modConfig.getBool("bgm9");
            bgm10 = modConfig.getBool("bgm10");
        } catch (Exception e) {
            logger.error("Mod配置文件加载失败", e);
        }
    }

    public static void saveConfig() {
        try {
            modConfig.setBool("quMode", quMode);
            modConfig.setBool("bgm9", bgm9);
            modConfig.setBool("bgm10", bgm10);
            modConfig.save();
        } catch (Exception e) {
            logger.error("Mod配置文件保存失败", e);
        }
    }

    public static void setupConfigPanel() {
        ModPanel settingsPanel = getModPanel();

        //不知道用啥图 0.o
        Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        Texture badge = new Texture(pixmap);
        pixmap.dispose();

        BaseMod.registerModBadge(
                badge,
                "蛊真人",
                "PetStzx",
                "蛊真人Mod",
                settingsPanel
        );
    }

    private static ModPanel getModPanel() {
        ModPanel settingsPanel = new ModPanel();
        String[] loc = getLocString();
        float currentY = 700.0f;

        ModLabeledToggleButton quButton = new ModLabeledToggleButton(
                loc[0],
                350.0f, currentY, Settings.CREAM_COLOR, FontHelper.charDescFont,
                quMode, settingsPanel, (label) -> {
        }, (button) -> {
            quMode = button.enabled;
            saveConfig();
        });
        settingsPanel.addUIElement(quButton);

        currentY -= 50.0f;
        ModLabeledToggleButton bgm9Button = new ModLabeledToggleButton(
                loc[1],
                350.0f, currentY, Settings.CREAM_COLOR, FontHelper.charDescFont,
                bgm9, settingsPanel, (label) -> {
        }, (button) -> {
            bgm9 = button.enabled;
            saveConfig();
        });
        settingsPanel.addUIElement(bgm9Button);

        currentY -= 50.0f;
        ModLabeledToggleButton bgm10Button = new ModLabeledToggleButton(
                loc[2],
                350.0f, currentY, Settings.CREAM_COLOR, FontHelper.charDescFont,
                bgm10, settingsPanel, (label) -> {
        }, (button) -> {
            bgm10 = button.enabled;
            saveConfig();
        });
        settingsPanel.addUIElement(bgm10Button);

        return settingsPanel;
    }

    private static String[] getLocString() {
        return CardCrawlGame.languagePack.getUIString("GuZhenRen:ConfigUI").TEXT;
    }
}