package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.YongShengPower;
import com.evacipated.cardcrawl.mod.stslib.relics.OnPlayerDeathRelic;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import basemod.abstracts.CustomRelic;

public class XianQiao_10 extends AbstractKongQiao implements OnPlayerDeathRelic {
    public static final String ID = GuZhenRen.makeID("XianQiao_10");

    private int lastMaxHp = -1;

    public XianQiao_10() {
        super(ID, "XianQiao_10.png", RelicTier.SPECIAL, LandingSound.MAGICAL);

        this.rank = 10;
        this.neededXP = 999999;
        this.nextRelicID = null;
        this.counter = -1;

        updateDescription();
    }

    @Override
    public void setCounter(int setCounter) {
        this.counter = -1;
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void updateDescription() {
        this.description = getUpdatedDescription();
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
    }

    @Override
    public void onEquip() {
        super.onEquip();
        if (AbstractDungeon.player != null) {
            this.lastMaxHp = AbstractDungeon.player.maxHealth;
            AbstractDungeon.player.heal(AbstractDungeon.player.maxHealth, true);
        }
    }

    @Override
    public void atBattleStart() {
        this.flash();
        AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                        new YongShengPower(AbstractDungeon.player))
        );
    }

    @Override
    public int onLoseHpLast(int damageAmount) {
        if (damageAmount > 0) {
            this.flash();
            return 0;
        }
        return damageAmount;
    }

    @Override
    public boolean onPlayerDeath(AbstractPlayer p, DamageInfo info) {
        this.flash();
        p.isDead = false;
        p.isDying = false;
        p.halfDead = false;
        p.currentHealth = p.maxHealth;
        p.healthBarUpdatedEvent();
        return false;
    }

    @Override
    public void update() {
        super.update();
        if (AbstractDungeon.player != null) {
            boolean needRefresh = false;

            if (this.lastMaxHp == -1) {
                this.lastMaxHp = AbstractDungeon.player.maxHealth;
            } else if (AbstractDungeon.player.maxHealth < this.lastMaxHp) {
                AbstractDungeon.player.maxHealth = this.lastMaxHp;
                needRefresh = true;
            } else if (AbstractDungeon.player.maxHealth > this.lastMaxHp) {
                this.lastMaxHp = AbstractDungeon.player.maxHealth;
            }

            if (AbstractDungeon.player.currentHealth < AbstractDungeon.player.maxHealth) {
                AbstractDungeon.player.currentHealth = AbstractDungeon.player.maxHealth;
                needRefresh = true;
            }

            if (AbstractDungeon.player.isDead || AbstractDungeon.player.isDying || AbstractDungeon.player.halfDead) {
                AbstractDungeon.player.isDead = false;
                AbstractDungeon.player.isDying = false;
                AbstractDungeon.player.halfDead = false;
                needRefresh = true;
            }

            if (needRefresh) {
                AbstractDungeon.player.healthBarUpdatedEvent();
                this.flash();
            }
        }
    }

    @Override
    public void atBattleStartPreDraw() {}

    @Override
    public void onVictory() {}

    @Override
    public void gainXP(int amount) {}

    @Override
    public void onRightClick() {}

    @Override
    public CustomRelic makeCopy() {
        return new XianQiao_10();
    }
}