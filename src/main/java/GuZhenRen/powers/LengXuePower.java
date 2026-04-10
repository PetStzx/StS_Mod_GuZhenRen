package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class LengXuePower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("LengXuePower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    // 联机防多重触发锁
    private boolean triggeredThisRound = false;

    public LengXuePower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.DEBUFF;
        this.isTurnBased = true;

        String pathLarge = GuZhenRen.assetPath("img/powers/LengXuePower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/LengXuePower.png");

        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathLarge), 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathSmall), 0, 0, 32, 32);

        this.updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    // 触发逻辑：回合开始时失去最大生命值的10%，随后层数减1
    @Override
    public void atStartOfTurn() {
        // 检查本轮是否已经触发过，如果是，直接拦截并跳过
        if (this.triggeredThisRound) {
            return;
        }

        // 标记本轮已触发
        this.triggeredThisRound = true;

        this.flash();

        // 计算最大生命值的 10%，至少为 1
        int damage = Math.max(1, this.owner.maxHealth / 10);

        // 扣除生命
        this.addToBot(new LoseHPAction(this.owner, this.owner, damage));

        // 层数减 1，到 0 则移除
        if (this.amount <= 1) {
            this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this.ID));
        } else {
            this.addToBot(new ReducePowerAction(this.owner, this.owner, this.ID, 1));
        }
    }

    // 轮次结束时，重置触发锁
    @Override
    public void atEndOfRound() {
        // 当所有玩家和怪物的行动彻底结束，把锁打开
        this.triggeredThisRound = false;
    }
}