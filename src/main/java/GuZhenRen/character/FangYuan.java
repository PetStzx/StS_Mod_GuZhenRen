package GuZhenRen.character;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractGuZhenRenCard;
import GuZhenRen.cards.*;
import GuZhenRen.patches.AbstractPlayerEnum;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.relics.KongQiao_1;
import basemod.abstracts.CustomPlayer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.Vampires;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import java.util.ArrayList;

public class FangYuan extends CustomPlayer {
    // 1. 注册角色字符串ID
    public static final String ID = GuZhenRen.makeID("FangYuan");
    private static final CharacterStrings characterStrings = CardCrawlGame.languagePack.getCharacterString(ID);

    // 2. 能量球纹理
    // 请确保你已将原版提取的图片放入 resources/GuZhenRen/img/ui/orb/ 文件夹中
    private static final String[] ORB_TEXTURES = {
            GuZhenRen.assetPath("img/ui/orb/1.png"),      // 原版文件名是 1.png
            GuZhenRen.assetPath("img/ui/orb/2.png"),
            GuZhenRen.assetPath("img/ui/orb/3.png"),
            GuZhenRen.assetPath("img/ui/orb/4.png"),
            GuZhenRen.assetPath("img/ui/orb/5.png"),
            GuZhenRen.assetPath("img/ui/orb/border.png"), // 边框
            GuZhenRen.assetPath("img/ui/orb/1d.png"),     // 耗尽状态
            GuZhenRen.assetPath("img/ui/orb/2d.png"),
            GuZhenRen.assetPath("img/ui/orb/3d.png"),
            GuZhenRen.assetPath("img/ui/orb/4d.png"),
            GuZhenRen.assetPath("img/ui/orb/5d.png")
    };

    // 3. 能量球特效路径 (请确保提取了 energyRedVFX.png 并重命名为 vfx.png 或者是保留原名)
    // 这里假设你把它重命名为了 vfx.png 放在了同级目录，如果没改名，请填 "img/ui/orb/energyRedVFX.png"
    private static final String ORB_VFX = GuZhenRen.assetPath("img/ui/orb/vfx.png");

    // 4. 能量球每层的转速
    private static final float[] LAYER_SPEED = new float[]{-40.0F, -32.0F, 20.0F, -20.0F, 0.0F, -10.0F, -8.0F, 5.0F, -5.0F, 0.0F};

    public FangYuan(String name) {
        // 5. super 构造函数
        super(name, AbstractPlayerEnum.FANG_YUAN, ORB_TEXTURES, ORB_VFX, LAYER_SPEED, null, null);

        this.dialogX = (this.drawX + 0.0F * Settings.scale);
        this.dialogY = (this.drawY + 220.0F * Settings.scale);

        // 6. 初始化角色设置
        // 确保 Idle.png 存在
        initializeClass(
                GuZhenRen.assetPath("img/character/FangYuan/Idle.png"),
                GuZhenRen.assetPath("img/character/FangYuan/shoulder2.png"),
                GuZhenRen.assetPath("img/character/FangYuan/shoulder.png"),
                GuZhenRen.assetPath("img/character/FangYuan/corpse.png"),
                getLoadout(),
                20.0F, -10.0F, 220.0F, 290.0F,
                new EnergyManager(3)
        );
    }

    // 初始卡组
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

    // 初始遗物
    @Override
    public ArrayList<String> getStartingRelics() {
        ArrayList<String> retVal = new ArrayList<>();
        retVal.add(KongQiao_1.ID);
        return retVal;
    }

    // 角色配置信息
    @Override
    public CharSelectInfo getLoadout() {
        return new CharSelectInfo(
                getLocalizedCharacterName(),
                characterStrings.TEXT[0],
                80, 80, 0, 99, 5,
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
    public String getSpireHeartText() {
        return characterStrings.TEXT[1];
    }

    @Override
    public Color getSlashAttackColor() {
        return Color.GRAY;
    }

    @Override
    public String getVampireText() {
        return Vampires.DESCRIPTIONS[0];
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

        // 遍历当前的抽牌堆（此时所有的牌都在抽牌堆里）
        for (AbstractCard c : AbstractDungeon.player.drawPile.group) {
            if (c instanceof AbstractGuZhenRenCard) {
                // 对每一张蛊真人牌，执行费用锁定
                ((AbstractGuZhenRenCard) c).applyRankLock();
            }
        }
    }

}