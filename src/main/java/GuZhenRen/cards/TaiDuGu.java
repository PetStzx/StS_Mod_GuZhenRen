package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;

public class TaiDuGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("TaiDuGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/TaiDuGu.png");

    private static final int COST = 1;
    private static final int VAL = 8; // 伤害和格挡的基础数值
    private static final int UPGRADE_PLUS_VAL = 3; // 升级后都加 3 (变为 11)
    private static final int MAGIC = 1; // 虚弱和易伤的层数
    private static final int INITIAL_RANK = 7;

    public TaiDuGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.ENEMY);

        this.setDao(Dao.BIAN_HUA_DAO);
        this.setRank(INITIAL_RANK);

        this.baseDamage = this.damage = VAL;
        this.baseBlock = this.block = VAL;
        this.baseMagicNumber = this.magicNumber = MAGIC;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        boolean isAttacking = false;
        if (m != null) {
            isAttacking = (m.intent == AbstractMonster.Intent.ATTACK ||
                    m.intent == AbstractMonster.Intent.ATTACK_BUFF ||
                    m.intent == AbstractMonster.Intent.ATTACK_DEBUFF ||
                    m.intent == AbstractMonster.Intent.ATTACK_DEFEND);
        }

        if (m != null) {
            if (isAttacking) {
                this.addToBot(new GainBlockAction(p, p, this.block));
                this.addToBot(new ApplyPowerAction(m, p, new WeakPower(m, this.magicNumber, false), this.magicNumber));
            } else {
                this.addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                this.addToBot(new ApplyPowerAction(m, p, new VulnerablePower(m, this.magicNumber, false), this.magicNumber));
            }
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(UPGRADE_PLUS_VAL);
            this.upgradeBlock(UPGRADE_PLUS_VAL);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }
}