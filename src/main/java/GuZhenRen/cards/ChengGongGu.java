package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.badlogic.gdx.graphics.g2d.SpriteBatch; // 【新增】用于渲染
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.InstantKillAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings; // 【新增】用于检测快速模式
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.GrandFinalEffect;

import java.util.ArrayList;

public class ChengGongGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("ChengGongGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/ChengGongGu.png");

    private static final int COST = 0;
    private static final int INITIAL_RANK = 9;

    public ChengGongGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.SPECIAL, // 【修改】逻辑上设为特殊，防止掉落
                CardTarget.ALL_ENEMY);

        this.setDao(Dao.LU_DAO);
        this.setRank(INITIAL_RANK);

        this.exhaust = true;
    }

    // =========================================================================
    //  【核心新增】渲染欺诈：在画牌的一瞬间，伪装成金卡
    // =========================================================================
    @Override
    public void render(SpriteBatch sb) {
        // 1. 保存当前的真实稀有度 (SPECIAL)
        CardRarity originalRarity = this.rarity;

        // 2. 临时改成金卡 (RARE)，这样游戏就会画出金色的边框和旗帜
        this.rarity = CardRarity.RARE;

        // 3. 调用父类的渲染方法
        super.render(sb);

        // 4. 立刻改回真实稀有度 (SPECIAL)，以免影响逻辑
        this.rarity = originalRarity;
    }

    // 为了让图鉴里看起来也是金卡，也可以重写这个方法
    @Override
    public void renderInLibrary(SpriteBatch sb) {
        CardRarity originalRarity = this.rarity;
        this.rarity = CardRarity.RARE;
        super.renderInLibrary(sb);
        this.rarity = originalRarity;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 播放特效
        if (Settings.FAST_MODE) {
            this.addToBot(new VFXAction(new GrandFinalEffect(), 0.7F));
        } else {
            this.addToBot(new VFXAction(new GrandFinalEffect(), 1.0F));
        }

        // 处决所有敌人
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!mo.isDeadOrEscaped()) {
                this.addToBot(new InstantKillAction(mo));
            }
        }
    }

    // 禁止升级
    @Override
    public boolean canUpgrade() {
        return false;
    }

    @Override
    public void upgrade() {
    }

    // 双重保险：禁止掉落
    @Override
    public boolean canSpawn(ArrayList<AbstractCard> rewardCards) {
        return false;
    }
}