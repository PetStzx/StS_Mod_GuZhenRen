package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class GuiLiXuYing extends AbstractXuYingCard {
    public static final String ID = GuZhenRen.makeID("GuiLiXuYing");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/GuiLiXuYing.png");

    private static final int BASE_BLOCK = 3;

    public GuiLiXuYing() {
        super(ID, NAME, IMG_PATH, -2, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardTarget.SELF);

        this.baseChanceFloat = 0.30f;
        this.baseBlock = this.block = BASE_BLOCK;
        this.initializeDescription();
    }

    // 将力量塞进基础值参与计算
    @Override
    public void applyPowers() {
        AbstractPlayer p = AbstractDungeon.player;
        if (p != null) {
            int strengthBonus = 0;
            if (p.hasPower(StrengthPower.POWER_ID)) {
                strengthBonus = p.getPower(StrengthPower.POWER_ID).amount;
            }

            // 记录真实的基础格挡
            int realBaseBlock = this.baseBlock;

            // 把力量临时垫进 baseBlock 里
            this.baseBlock += strengthBonus;

            // 调用父类，让引擎去计算敏捷、脆弱等Buff的加成
            super.applyPowers();

            // 计算完之后，把真实的 baseBlock 换回来
            this.baseBlock = realBaseBlock;

            this.isBlockModified = (this.block != this.baseBlock);
        } else {
            super.applyPowers();
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBlock(1); // 基础格挡 3 -> 4
            this.baseChanceFloat = 0.40f; // 概率 30% -> 40%
            this.initializeDescription();
        }
    }

    @Override
    public void triggerPhantomEffect(AbstractMonster m) {
        if (AbstractDungeon.player != null) {
            // 在触发效果前，实时计算一次当前的格挡值
            this.applyPowers();

            this.addToTop(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, this.block));
        }
    }
}