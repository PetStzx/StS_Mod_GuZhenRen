package GuZhenRen.character;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.*;
import GuZhenRen.patches.AbstractPlayerEnum;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.relics.KongQiao_1;
import basemod.abstracts.CustomPlayer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.cutscenes.CutscenePanel;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.CharSelectInfo;

import java.util.ArrayList;
import java.util.List;

public class FangYuan extends CustomPlayer {
    public static final String ID = GuZhenRen.makeID("FangYuan");
    private static final CharacterStrings characterStrings = CardCrawlGame.languagePack.getCharacterString(ID);

    private static final UIStrings eventStrings = CardCrawlGame.languagePack.getUIString(GuZhenRen.makeID("CharacterEvents"));

    // 能量球纹理
    private static final String[] ORB_TEXTURES = {
            GuZhenRen.assetPath("img/ui/orb/1.png"),
            GuZhenRen.assetPath("img/ui/orb/2.png"),
            GuZhenRen.assetPath("img/ui/orb/3.png"),
            GuZhenRen.assetPath("img/ui/orb/4.png"),
            GuZhenRen.assetPath("img/ui/orb/5.png"),
            GuZhenRen.assetPath("img/ui/orb/border.png"),
            GuZhenRen.assetPath("img/ui/orb/1d.png"),
            GuZhenRen.assetPath("img/ui/orb/2d.png"),
            GuZhenRen.assetPath("img/ui/orb/3d.png"),
            GuZhenRen.assetPath("img/ui/orb/4d.png"),
            GuZhenRen.assetPath("img/ui/orb/5d.png")
    };

    // 能量球特效路径
    private static final String ORB_VFX = GuZhenRen.assetPath("img/ui/orb/vfx.png");

    // 能量球每层转速
    private static final float[] LAYER_SPEED = new float[]{-40.0F, -32.0F, 20.0F, -20.0F, 0.0F, -10.0F, -8.0F, 5.0F, -5.0F, 0.0F};

    public FangYuan(String name) {
        super(name, AbstractPlayerEnum.FANG_YUAN, ORB_TEXTURES, ORB_VFX, LAYER_SPEED, null, null);

        this.dialogX = (this.drawX + 0.0F * Settings.scale);
        this.dialogY = (this.drawY + 220.0F * Settings.scale);

        initializeClass(
                null,
                GuZhenRen.assetPath("img/character/FangYuan/shoulder2.png"),
                GuZhenRen.assetPath("img/character/FangYuan/shoulder.png"),
                GuZhenRen.assetPath("img/character/FangYuan/corpse.png"),
                getLoadout(),
                20.0F, -10.0F, 220.0F, 290.0F,
                new EnergyManager(3)
        );

        this.loadAnimation(
                GuZhenRen.assetPath("img/character/FangYuan/FangYuan.atlas"),
                GuZhenRen.assetPath("img/character/FangYuan/FangYuan.json"),
                1.0F
        );

        com.esotericsoftware.spine.AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * com.badlogic.gdx.math.MathUtils.random());
    }

    @Override
    public ArrayList<String> getStartingDeck() {
        ArrayList<String> retVal = new ArrayList<>();
        retVal.add(YueGuangGu.ID);
        retVal.add(YueGuangGu.ID);
        retVal.add(YueGuangGu.ID);
        retVal.add(YueGuangGu.ID);
        retVal.add(YuPiGu.ID);
        retVal.add(YuPiGu.ID);
        retVal.add(YuPiGu.ID);
        retVal.add(YuPiGu.ID);
        retVal.add(XiaoGuangGu.ID);
        return retVal;
    }

    @Override
    public ArrayList<String> getStartingRelics() {
        ArrayList<String> retVal = new ArrayList<>();
        retVal.add(KongQiao_1.ID);
        return retVal;
    }

    @Override
    public CharSelectInfo getLoadout() {
        return new CharSelectInfo(
                getLocalizedCharacterName(),
                characterStrings.TEXT[0],
                72, 72, 0, 99, 5,
                this, getStartingRelics(), getStartingDeck(), false
        );
    }

    @Override
    public String getTitle(AbstractPlayer.PlayerClass playerClass) {
        return characterStrings.NAMES[0];
    }

    @Override
    public AbstractCard.CardColor getCardColor() {
        return CardColorEnum.GUZHENREN_GREY;
    }

    @Override
    public Color getCardRenderColor() {
        return GuZhenRen.GUZHENREN_COLOR;
    }

    @Override
    public AbstractCard getStartCardForEvent() {
        return new YueGuangGu();
    }

    @Override
    public Color getCardTrailColor() {
        return GuZhenRen.GUZHENREN_COLOR;
    }

    @Override
    public int getAscensionMaxHPLoss() {
        return 5;
    }

    @Override
    public BitmapFont getEnergyNumFont() {
        return FontHelper.energyNumFontBlue;
    }

    @Override
    public void doCharSelectScreenSelectEffect() {
        CardCrawlGame.sound.playA("ATTACK_HEAVY", 1.0f);
        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.SHORT, true);
    }

    @Override
    public String getCustomModeCharacterButtonSoundKey() {
        return "ATTACK_HEAVY";
    }

    @Override
    public String getLocalizedCharacterName() {
        return characterStrings.NAMES[0];
    }

    @Override
    public AbstractPlayer newInstance() {
        return new FangYuan(this.name);
    }

    @Override
    public Color getSlashAttackColor() {
        return Color.GRAY;
    }

    @Override
    public AbstractGameAction.AttackEffect[] getSpireHeartSlashEffect() {
        return new AbstractGameAction.AttackEffect[]{
                AbstractGameAction.AttackEffect.SLASH_HEAVY,
                AbstractGameAction.AttackEffect.FIRE,
                AbstractGameAction.AttackEffect.SLASH_DIAGONAL
        };
    }

    @Override
    public void applyStartOfCombatPreDrawLogic() {
        super.applyStartOfCombatPreDrawLogic();
    }

    @Override
    public String getSpireHeartText() {
        // 攻击心脏的文本
        return eventStrings.TEXT[0];
    }

    @Override
    public String getVampireText() {
        return CardCrawlGame.languagePack.getEventString("Vampires").DESCRIPTIONS[0];
    }


    @Override
    public Texture getCutsceneBg() {
        return ImageMaster.loadImage("images/scenes/redBg.jpg");
    }

    @Override
    public List<CutscenePanel> getCutscenePanels() {
        List<CutscenePanel> panels = new ArrayList<>();

        if (GuZhenRen.ENCOUNTER_LONG_GONG.equals(AbstractDungeon.bossKey)) {
            panels.add(new CutscenePanel(GuZhenRen.assetPath("img/scenes/FangYuan_Scene4.png")));
            panels.add(new CutscenePanel(GuZhenRen.assetPath("img/scenes/FangYuan_Scene5.png"),"WATCHER_HEART_PUNCH"));
            panels.add(new CutscenePanel(GuZhenRen.assetPath("img/scenes/FangYuan_Scene6.png")));
        } else {
            panels.add(new CutscenePanel(GuZhenRen.assetPath("img/scenes/FangYuan_Scene1.png"), "WATCHER_HEART_PUNCH"));
            panels.add(new CutscenePanel(GuZhenRen.assetPath("img/scenes/FangYuan_Scene2.png")));
            panels.add(new CutscenePanel(GuZhenRen.assetPath("img/scenes/FangYuan_Scene3.png")));
        }

        return panels;
    }
}