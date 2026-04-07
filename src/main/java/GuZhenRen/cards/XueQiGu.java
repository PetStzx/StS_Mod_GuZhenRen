package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class XueQiGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("XueQiGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/XueQiGu.png");

    private static final int COST = 1;
    private static final int BASE_HEAL = 2; // 基础回复 2 点
    private static final int INITIAL_RANK = 2; // 2转

    public XueQiGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON, // 蓝卡
                CardTarget.SELF);

        this.setDao(Dao.XUE_DAO);

        // 使用 magicNumber 来动态记录和展示最终的总回复量
        this.baseMagicNumber = this.magicNumber = BASE_HEAL;

        this.exhaust = true; // 消耗
        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 直接根据当前的 magicNumber 恢复生命
        this.addToBot(new HealAction(p, p, this.magicNumber));
    }

    // =========================================================================
    // 核心机制：只要在手牌、抽牌堆、弃牌堆中，玩家掉血/受击必定触发！
    // =========================================================================
    @Override
    public void tookDamage() {
        // 每次失去生命，总回复量 +1
        this.baseMagicNumber += 1;
        this.magicNumber = this.baseMagicNumber;
        this.isMagicNumberModified = true;

        // 视觉反馈：如果这张牌在手里，闪烁特效
        if (AbstractDungeon.player != null && AbstractDungeon.player.hand.contains(this)) {
            this.superFlash(Color.LIME.cpy());
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.selfRetain = true; // 升级增加保留
            this.myBaseDescription = UPGRADE_DESCRIPTION;
            this.upgradeRank(1); // 2转 -> 3转
            this.initializeDescription();
        }
    }
}