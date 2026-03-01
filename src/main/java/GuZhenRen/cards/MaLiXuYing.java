package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class MaLiXuYing extends AbstractXuYingCard {
    public static final String ID = GuZhenRen.makeID("MaLiXuYing");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/MaLiXuYing.png");

    public MaLiXuYing() {
        // 技能牌虚影，Target 设为 NONE，因为抽滤不针对敌人
        super(ID, NAME, IMG_PATH, -2, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardTarget.NONE);

        // 概率：基础 20%
        this.baseChanceFloat = 0.20f;
        this.initializeDescription();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            // 概率：升级 35%
            this.baseChanceFloat = 0.35f;
            this.initializeDescription();
        }
    }

    @Override
    public void triggerPhantomEffect(AbstractMonster m) {
        // 触发效果：抽1丢1
        // addToTop 确保效果在动作序列中立即发生
        this.addToTop(new DiscardAction(AbstractDungeon.player, AbstractDungeon.player, 1, false));
        this.addToTop(new DrawCardAction(AbstractDungeon.player, 1));
    }
}