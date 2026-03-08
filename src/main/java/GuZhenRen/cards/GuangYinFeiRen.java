package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect; // 引入原版斩击特效

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

        // 使用 misc 变量来跨战斗追踪使用次数，初始为 3
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

        this.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                if (m != null && !m.isDeadOrEscaped()) {
                    AbstractDungeon.effectList.add(new FlashAtkImgEffect(m.hb.cX, m.hb.cY, AbstractGameAction.AttackEffect.SLASH_HEAVY));

                    // 清空血量并更新血条 UI
                    m.currentHealth = 0;
                    m.healthBarUpdatedEvent();
                    // 调用怪物死亡机制
                    m.die();
                }
                this.isDone = true;
            }
        });

        // 3. 次数结算与移除逻辑
        this.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                // 扣除当前战斗中这张牌的次数
                GuangYinFeiRen.this.misc--;
                GuangYinFeiRen.this.baseMagicNumber = GuangYinFeiRen.this.magicNumber = GuangYinFeiRen.this.misc;

                // 寻找大师牌组中对应的这张牌，并同步扣除次数
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

                    // 如果大师牌组里的次数耗尽，将其永久移除
                    if (masterCard.misc <= 0) {
                        AbstractDungeon.player.masterDeck.removeCard(masterCard);
                    }
                }

                // 如果当前战斗中次数耗尽，打出后直接消耗，不进入弃牌堆
                if (GuangYinFeiRen.this.misc <= 0) {
                    GuangYinFeiRen.this.purgeOnUse = true;
                }

                this.isDone = true;
            }
        });
    }
}