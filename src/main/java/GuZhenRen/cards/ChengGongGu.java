package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.InstantKillAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.GrandFinalEffect;

import java.util.ArrayList;

public class ChengGongGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("ChengGongGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/ChengGongGu.png");

    private static final int COST = 0;
    private static final int INITIAL_RANK = 9;

    public ChengGongGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.RARE, // 金卡框
                CardTarget.ALL_ENEMY);

        this.setDao(Dao.LU_DAO);


        this.setRank(INITIAL_RANK);

        // 消耗、虚无、保留等属性根据需求设定，既然是特殊胜利牌，建议消耗
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 播放一个华丽的特效 (借用原版 GrandFinal 特效)
        this.addToBot(new VFXAction(new GrandFinalEffect()));

        // 处决所有敌人
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!mo.isDeadOrEscaped()) {
                this.addToBot(new InstantKillAction(mo));
            }
        }
    }

    // 禁止升级
    @Override
    public boolean canUpgrade() {
        return false;
    }

    @Override
    public void upgrade() {
    }

    // 禁止掉落
    @Override
    public boolean canSpawn(ArrayList<AbstractCard> rewardCards) {
        return false;
    }
}