package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;
import com.megacrit.cardcrawl.powers.IntangiblePower;

public class GuiGuaYiPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("GuiGuaYiPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private AbstractMonster.Intent lastIntent = null;

    public GuiGuaYiPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;

        String pathLarge = GuZhenRen.assetPath("img/powers/GuiGuaYiPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/GuiGuaYiPower.png");
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathLarge), 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathSmall), 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    @Override
    public void update(int slot) {
        super.update(slot);
        if (this.owner instanceof AbstractMonster) {
            AbstractMonster m = (AbstractMonster) this.owner;

            if (m.intent != this.lastIntent) {
                this.lastIntent = m.intent;

                AbstractDungeon.actionManager.addToBottom(new com.megacrit.cardcrawl.actions.AbstractGameAction() {
                    @Override
                    public void update() {
                        if (!m.isDeadOrEscaped() && !m.halfDead) {
                            checkIntentAndApply(m);
                        }
                        this.isDone = true;
                    }
                });
            }
        }
    }

    @Override
    public void onInitialApplication() {
        if (this.owner instanceof AbstractMonster) {
            AbstractMonster m = (AbstractMonster) this.owner;
            if (!m.isDeadOrEscaped() && !m.halfDead) {
                this.lastIntent = m.intent;
                checkIntentAndApply(m);
            }
        }
    }

    @Override
    public void atEndOfRound() {
        this.lastIntent = null;
    }

    private boolean isAttacking(AbstractMonster.Intent intent) {
        if (intent == null) return false;
        return intent == AbstractMonster.Intent.ATTACK ||
                intent == AbstractMonster.Intent.ATTACK_BUFF ||
                intent == AbstractMonster.Intent.ATTACK_DEBUFF ||
                intent == AbstractMonster.Intent.ATTACK_DEFEND;
    }

    private void checkIntentAndApply(AbstractMonster m) {
        boolean isAttack = isAttacking(m.intent);

        if (isAttack) {
            if (m.hasPower(IntangiblePlayerPower.POWER_ID)) {
                AbstractDungeon.actionManager.addToTop(new RemoveSpecificPowerAction(m, m, IntangiblePlayerPower.POWER_ID));
            }
        } else {
            boolean hasAnyIntangible = m.hasPower(IntangiblePower.POWER_ID) || m.hasPower(IntangiblePlayerPower.POWER_ID);

            if (!hasAnyIntangible) {
                this.flash();
                AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(m, m, new IntangiblePlayerPower(m, 1), 1));
            }
        }
    }
}