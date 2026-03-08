package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.HemokinesisEffect;

public class XueRenGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("XueRenGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/XueRenGu.png");

    private static final int COST = 1;
    private static final int DAMAGE = 7;
    private static final int HITS = 3;
    private static final int HP_LOSS = 2;
    private static final int UPGRADE_NEW_HP_LOSS = 1;
    private static final int INITIAL_RANK = 3;

    public XueRenGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.ENEMY);

        this.setDao(Dao.XUE_DAO);
        this.baseDamage = DAMAGE;
        this.baseMagicNumber = this.magicNumber = HITS;
        this.baseSecondMagicNumber = this.secondMagicNumber = HP_LOSS;
        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 播放一次“御血术”特效
        if (m != null) {
            this.addToBot(new VFXAction(new HemokinesisEffect(p.hb.cX, p.hb.cY, m.hb.cX, m.hb.cY), 0.5F));
        }

        // 2. 循环 3 次：交替失去生命并造成伤害
        for (int i = 0; i < this.magicNumber; i++) {
            // 失去生命
            this.addToBot(new LoseHPAction(p, p, this.secondMagicNumber));
            //造成伤害
            this.addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.baseSecondMagicNumber = UPGRADE_NEW_HP_LOSS;
            this.secondMagicNumber = this.baseSecondMagicNumber;
            this.upgradedSecondMagicNumber = true;
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }
}