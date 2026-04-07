package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class XingHuoLiaoYuanPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("XingHuoLiaoYuanPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    // 【极其核心的安全锁】：防止 A传给B，B传给A 的无限死循环
    public static boolean isSpreading = false;

    public XingHuoLiaoYuanPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1; // -1 表示不显示层数
        this.type = PowerType.DEBUFF;

        String pathLarge = GuZhenRen.assetPath("img/powers/XingHuoLiaoYuanPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/XingHuoLiaoYuanPower.png");
        Texture texLarge = ImageMaster.loadImage(pathLarge);
        Texture texSmall = ImageMaster.loadImage(pathSmall);
        this.region128 = new TextureAtlas.AtlasRegion(texLarge, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(texSmall, 0, 0, 32, 32);

        updateDescription();
    }

    // 执行传染逻辑
    public void triggerSpread(int spreadAmount) {
        if (spreadAmount <= 0) return;

        this.flash();

        this.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                // 1. 上锁：我正在传染，其他人不要再触发了
                isSpreading = true;

                // 【修复核心】：使用 addToTop 确保传染动作的原子性！
                // 注意：由于 addToTop 是后进先出(插队)，所以我们要把顺序反过来写。

                // 3. 先把“解锁”动作压入栈，它会被垫在最下面，最后执行
                AbstractDungeon.actionManager.addToTop(new AbstractGameAction() {
                    @Override
                    public void update() {
                        isSpreading = false;
                        this.isDone = true;
                    }
                });

                // 2. 再把“上状态”动作压入栈，它们会盖在解锁动作上面，优先执行
                for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (!mo.isDeadOrEscaped() && mo != owner) {
                        AbstractDungeon.actionManager.addToTop(
                                new ApplyPowerAction(mo, owner, new FenShaoPower(mo, spreadAmount), spreadAmount)
                        );
                    }
                }

                this.isDone = true;
            }
        });
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}