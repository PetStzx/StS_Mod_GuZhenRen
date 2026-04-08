package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Color; // 【新增】颜色类
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction; // 【新增】视觉特效动作
import com.megacrit.cardcrawl.actions.common.InstantKillAction; // 【新增】安全的官方秒杀动作
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect; // 【新增】屏幕边缘闪烁特效
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

public class GuangYinFeiRen extends AbstractShaZhaoCard {
    public static final String ID = GuZhenRen.makeID("GuangYinFeiRen");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/GuangYinFeiRen.png");

    private static final int COST = 4;

    public GuangYinFeiRen() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardTarget.ENEMY);

        this.setDao(Dao.ZHOU_DAO);

        // 跨战斗追踪使用次数，初始为 3
        this.misc = 3;
        this.baseMagicNumber = this.magicNumber = this.misc;
    }

    @Override
    protected void onRankLoaded() {
        this.baseMagicNumber = this.magicNumber = this.misc;
        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new SFXAction("ATTACK_HEAVY"));
        this.addToBot(new VFXAction(new BorderFlashEffect(Color.LIGHT_GRAY)));

        if (m != null) {
            this.addToBot(new VFXAction(new FlashAtkImgEffect(m.hb.cX, m.hb.cY, AbstractGameAction.AttackEffect.SLASH_HEAVY)));
        }

        if (m != null) {
            this.addToBot(new InstantKillAction(m));
        }

        // 次数结算与移除逻辑
        this.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                // 扣除战斗中这张牌的次数
                GuangYinFeiRen.this.misc--;
                GuangYinFeiRen.this.baseMagicNumber = GuangYinFeiRen.this.magicNumber = GuangYinFeiRen.this.misc;

                // 对大师牌组中这张牌同步扣除次数
                AbstractCard masterCard = null;
                for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                    if (c.uuid.equals(GuangYinFeiRen.this.uuid)) {
                        masterCard = c;
                        break;
                    }
                }

                if (masterCard != null) {
                    masterCard.misc = GuangYinFeiRen.this.misc;
                    masterCard.baseMagicNumber = masterCard.magicNumber = masterCard.misc;

                    // 如果大师牌组里次数耗尽，将其移除
                    if (masterCard.misc <= 0) {
                        AbstractDungeon.player.masterDeck.removeCard(masterCard);
                    }
                }

                // 如果战斗中次数耗尽，打出后移除
                if (GuangYinFeiRen.this.misc <= 0) {
                    GuangYinFeiRen.this.purgeOnUse = true;
                }

                this.isDone = true;
            }
        });
    }
}