package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class BaiXiangXuYing extends AbstractXuYingCard {
    public static final String ID = GuZhenRen.makeID("BaiXiangXuYing");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/BaiXiangXuYing.png");

    public BaiXiangXuYing() {
        // 技能牌虚影，对自己施加增益，Target 设为 SELF
        super(ID, NAME, IMG_PATH, -2, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardTarget.SELF);

        // 基础概率 25%
        this.baseChanceFloat = 0.25f;
        this.baseMagicNumber = this.magicNumber = 1; // 获得1点力量

        this.initializeDescription();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            // 升级概率 40%
            this.baseChanceFloat = 0.40f;
            this.initializeDescription();
        }
    }

    @Override
    public void triggerPhantomEffect(AbstractMonster m) {
        // 触发效果：获得1点力量
        // addToTop 确保虚影触发效果优先结算
        this.addToTop(new ApplyPowerAction(
                AbstractDungeon.player,
                AbstractDungeon.player,
                new StrengthPower(AbstractDungeon.player, this.magicNumber),
                this.magicNumber
        ));
    }
}