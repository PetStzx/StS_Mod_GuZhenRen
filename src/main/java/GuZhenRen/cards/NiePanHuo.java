package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.FenShaoPower;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnPlayerDeathPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.ScreenOnFireEffect;

public class NiePanHuo extends AbstractShaZhaoCard {
    public static final String ID = GuZhenRen.makeID("NiePanHuo");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/NiePanHuo.png");

    private static final int COST = 2;
    private static final int HEAL_PERCENT = 25;

    public NiePanHuo() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardTarget.ALL_ENEMY);

        this.setDao(Dao.YAN_DAO);
        this.exhaust = true;

        this.baseMagicNumber = this.magicNumber = HEAL_PERCENT;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new VFXAction(p, new ScreenOnFireEffect(), 0.75F));

        this.addToBot(new NiePanHuoAction(p, this.magicNumber));
    }

    private static class NiePanHuoAction extends AbstractGameAction {
        private final AbstractPlayer p;
        private final int healPercent;

        public NiePanHuoAction(AbstractPlayer p, int healPercent) {
            this.p = p;
            this.healPercent = healPercent;
            this.actionType = ActionType.DAMAGE;
            this.duration = Settings.ACTION_DUR_FAST;
        }

        @Override
        public void update() {
            if (this.duration == Settings.ACTION_DUR_FAST) {
                NiePanHuoDeathPreventPower preventPower = new NiePanHuoDeathPreventPower(p, this.healPercent);
                p.powers.add(preventPower);

                p.damage(new DamageInfo(p, p.currentHealth, DamageInfo.DamageType.HP_LOSS));

                int hpLost = preventPower.hpLostThisAction;

                if (hpLost > 0) {
                    for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                        if (!mo.isDeadOrEscaped() && !mo.halfDead) {
                            AbstractDungeon.actionManager.addToTop(
                                    new ApplyPowerAction(mo, p, new FenShaoPower(mo, hpLost), hpLost)
                            );
                        }
                    }
                }

                if (!preventPower.hasTriggeredDeath) {
                    int targetHp = p.maxHealth * this.healPercent / 100;
                    if (targetHp < 1) targetHp = 1;

                    if (p.currentHealth < targetHp) {
                        p.heal(targetHp - p.currentHealth, true);
                    }
                }

                p.powers.remove(preventPower);
            }
            this.tickDuration();
        }
    }

    public static class NiePanHuoDeathPreventPower extends AbstractPower implements InvisiblePower, OnPlayerDeathPower {
        public int hpLostThisAction = 0;
        public boolean hasTriggeredDeath = false;
        private final int healPercent;

        public NiePanHuoDeathPreventPower(AbstractCreature owner, int healPercent) {
            this.name = "NiePanHuoPower";
            this.ID = GuZhenRen.makeID("NiePanHuoPower");
            this.owner = owner;
            this.healPercent = healPercent;
            this.type = PowerType.BUFF;
        }

        @Override
        public void wasHPLost(DamageInfo info, int damageAmount) {
            this.hpLostThisAction += damageAmount;
        }

        @Override
        public boolean onPlayerDeath(AbstractPlayer p, DamageInfo info) {
            this.hasTriggeredDeath = true;

            int targetHp = p.maxHealth * this.healPercent / 100;
            if (targetHp < 1) targetHp = 1;

            p.heal(targetHp, true);

            if (p.currentHealth <= 0) {
                return true;
            }

            return false;
        }
    }
}