package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import GuZhenRen.util.BattleStateManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class GuoPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("GuoPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static int guoIdOffset = 0;
    public static boolean isGuoApplying = false;

    static {
        BattleStateManager.onBattleStart(() -> {
            GuoPower.isGuoApplying = false;
            GuoPower.guoIdOffset = 0;
        });
        BattleStateManager.onPostBattle(() -> {
            GuoPower.isGuoApplying = false;
            GuoPower.guoIdOffset = 0;
        });
    }

    private boolean isDamage;
    private int storedDamage = 0;
    private AbstractPower storedPower = null;

    public GuoPower(AbstractCreature owner, int turns, int damage) {
        this.name = NAME;
        this.ID = POWER_ID + guoIdOffset++;
        this.owner = owner;
        this.amount = turns;
        this.isDamage = true;
        this.storedDamage = damage;
        this.type = PowerType.BUFF;

        initTextures();
        updateDescription();
    }

    public GuoPower(AbstractCreature owner, int turns, AbstractPower power) {
        this.name = NAME;
        this.ID = POWER_ID + guoIdOffset++;
        this.owner = owner;
        this.amount = turns;
        this.isDamage = false;
        this.storedPower = power;
        this.type = PowerType.BUFF;

        initTextures();
        updateDescription();
    }

    private void initTextures() {
        String pathLarge = GuZhenRen.assetPath("img/powers/GuoPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/GuoPower.png");
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathLarge), 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathSmall), 0, 0, 32, 32);
    }

    public boolean isDamageFruit() { return isDamage; }
    public int getStoredDamage() { return storedDamage; }
    public AbstractPower getStoredPower() { return storedPower; }

    public GuoPower makeTransferCopy(AbstractCreature newTarget) {
        if (this.isDamage) {
            return new GuoPower(newTarget, this.amount, this.storedDamage);
        } else {
            return new GuoPower(newTarget, this.amount, this.storedPower);
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer && this.owner.isPlayer) {
            triggerFruit();
        }
    }

    @Override
    public void duringTurn() {
        if (!this.owner.isPlayer) {
            triggerFruit();
        }
    }

    private void triggerFruit() {
        if (this.amount == 1) {
            this.flash();

            this.addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    isGuoApplying = true;

                    AbstractDungeon.actionManager.addToTop(new AbstractGameAction() {
                        @Override
                        public void update() {
                            isGuoApplying = false;
                            this.isDone = true;
                        }
                    });

                    if (isDamage) {
                        AbstractDungeon.actionManager.addToTop(new DamageAction(owner, new DamageInfo(owner, storedDamage, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                    } else if (storedPower != null) {
                        storedPower.owner = owner;
                        AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(owner, owner, storedPower, storedPower.amount));
                    }

                    this.isDone = true;
                }
            });

            this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this.ID));
        } else {
            this.addToBot(new ReducePowerAction(this.owner, this.owner, this.ID, 1));
        }
    }

    @Override
    public void updateDescription() {
        String timeStr = (this.amount == 1) ? DESCRIPTIONS[2] : (DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1]);

        if (this.isDamage) {
            this.description = timeStr + DESCRIPTIONS[3] + this.storedDamage + DESCRIPTIONS[4];
        } else if (this.storedPower != null) {
            if (this.storedPower.amount != 0) {
                this.description = timeStr + DESCRIPTIONS[5] + this.storedPower.amount + DESCRIPTIONS[6] + "#y" + this.storedPower.name + DESCRIPTIONS[7];
            } else {
                this.description = timeStr + " #y" + this.storedPower.name + DESCRIPTIONS[7];
            }
        }
    }
}