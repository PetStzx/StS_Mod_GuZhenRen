package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.patches.GuZhenRenTags;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

public class Xian extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("Xian");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/Xian.png");

    private static final int COST = 2;
    private static final int DAMAGE = 7;
    private static final int UPGRADE_PLUS_DMG = 3; // 升级加 3 点伤害，变为 10
    private static final int INITIAL_RANK = 7;

    public Xian() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.ENEMY);

        this.setDao(Dao.LU_DAO);
        this.tags.add(GuZhenRenTags.XIAN_GU);
        this.baseDamage = DAMAGE;
        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new SFXAction("POWER_SHACKLE"));
        this.addToBot(new XianAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn)));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(UPGRADE_PLUS_DMG); // 7 -> 10
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }


    public static class XianAction extends AbstractGameAction {
        private DamageInfo info;

        public XianAction(AbstractMonster target, DamageInfo info) {
            this.info = info;
            this.target = target;
            this.actionType = ActionType.DAMAGE;
            this.duration = Settings.ACTION_DUR_FAST;
        }

        @Override
        public void update() {
            if (this.duration == Settings.ACTION_DUR_FAST && this.target != null) {
                AbstractDungeon.effectList.add(new FlashAtkImgEffect(this.target.hb.cX, this.target.hb.cY, AttackEffect.SLASH_DIAGONAL));

                this.target.damage(this.info);

                if (this.target.lastDamageTaken > 0) {
                    int reduceAmt = this.target.lastDamageTaken;


                    this.addToTop(new ApplyPowerAction(this.target, this.info.owner, new GainStrengthPower(this.target, reduceAmt), reduceAmt));
                    this.addToTop(new ApplyPowerAction(this.target, this.info.owner, new StrengthPower(this.target, -reduceAmt), -reduceAmt));
                }

                if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                    AbstractDungeon.actionManager.clearPostCombatActions();
                }
            }
            this.tickDuration();
        }
    }
}