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
                CardRarity.SPECIAL, // 特殊牌
                CardTarget.ALL_ENEMY);

        this.setDao(Dao.LU_DAO);
        this.setRank(INITIAL_RANK);

        this.exhaust = true;
    }

    // =========================================================================
    //  显示成金卡
    // =========================================================================
    @Override
    public void render(SpriteBatch sb) {
        CardRarity originalRarity = this.rarity;

        this.rarity = CardRarity.RARE;

        super.render(sb);

        this.rarity = originalRarity;
    }

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

    // 防止掉落
    @Override
    public boolean canSpawn(ArrayList<AbstractCard> rewardCards) {
        return false;
    }
}