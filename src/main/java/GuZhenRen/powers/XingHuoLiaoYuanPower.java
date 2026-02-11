package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.ArrayList;

public class XingHuoLiaoYuanPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("XingHuoLiaoYuanPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public XingHuoLiaoYuanPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1; // -1 表示不显示层数，这是一个状态效果
        this.type = PowerType.DEBUFF;

        // 图片加载逻辑
        String pathLarge = GuZhenRen.assetPath("img/powers/XingHuoLiaoYuanPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/XingHuoLiaoYuanPower.png");

        Texture texLarge = ImageMaster.loadImage(pathLarge);
        Texture texSmall = ImageMaster.loadImage(pathSmall);
        this.region128 = new TextureAtlas.AtlasRegion(texLarge, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(texSmall, 0, 0, 32, 32);


        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    @Override
    public void onDeath() {
        // 1. 检查持有者是否有“焚烧”
        if (this.owner.hasPower(FenShaoPower.POWER_ID)) {
            int totalStacks = this.owner.getPower(FenShaoPower.POWER_ID).amount;

            if (totalStacks > 0) {
                // 2. 寻找其他活着的敌人
                ArrayList<AbstractMonster> livingMonsters = new ArrayList<>();
                for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (!m.isDeadOrEscaped() && m != this.owner) {
                        livingMonsters.add(m);
                    }
                }

                // 3. 均分层数
                if (!livingMonsters.isEmpty()) {
                    int amountPerTarget = totalStacks / livingMonsters.size();

                    // 至少要有 1 层才施加，避免显示 0
                    if (amountPerTarget > 0) {
                        this.flash();
                        for (AbstractMonster m : livingMonsters) {
                            this.addToBot(new ApplyPowerAction(m, this.owner,
                                    new FenShaoPower(m, amountPerTarget), amountPerTarget));
                        }
                    }
                }
            }
        }
    }
}