package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.actions.utility.UnlimboAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class WanLan extends AbstractGuZhenRenCard {

    public static final String ID = GuZhenRen.makeID("WanLan");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/WanLan.png");

    private static final int COST = 1;
    private static final int BASE_DAMAGE = 1;
    private static final int UPGRADE_PLUS_DAMAGE = 1; // 升级后增加 1 点伤害，变为 3
    private static final int HITS = 4; // 攻击 4 次
    private static final int INITIAL_RANK = 6; // 6转仙蛊

    public WanLan() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.ENEMY);

        this.baseDamage = BASE_DAMAGE;
        this.baseMagicNumber = this.magicNumber = HITS;

        this.setDao(Dao.LI_DAO);
        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 造成 4 次连击
        for (int i = 0; i < this.magicNumber; i++) {
            this.addToBot(new DamageAction(
                    m,
                    new DamageInfo(p, this.damage, this.damageTypeForTurn),
                    AbstractGameAction.AttackEffect.BLUNT_LIGHT
            ));
        }

        // 2. 打出抽牌堆顶部的攻击牌
        this.addToBot(new PlayTopAttackFromDrawPileAction(m));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(UPGRADE_PLUS_DAMAGE);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }

    // =========================================================================
    // 从“抽牌堆”顶部寻找攻击牌并打出的动作
    // =========================================================================
    public static class PlayTopAttackFromDrawPileAction extends AbstractGameAction {
        private AbstractMonster targetMonster;

        public PlayTopAttackFromDrawPileAction(AbstractMonster target) {
            this.duration = Settings.ACTION_DUR_FAST;
            this.actionType = ActionType.WAIT;
            this.source = AbstractDungeon.player;
            this.targetMonster = target;
        }

        @Override
        public void update() {
            if (this.duration == Settings.ACTION_DUR_FAST) {
                // 1. 检查抽牌堆是否为空
                if (AbstractDungeon.player.drawPile.isEmpty()) {
                    this.isDone = true;
                    return;
                }

                AbstractCard cardToPlay = null;

                // 2. 从抽牌堆顶部（即 List 的末尾）开始向下寻找攻击牌
                for (int i = AbstractDungeon.player.drawPile.group.size() - 1; i >= 0; i--) {
                    AbstractCard c = AbstractDungeon.player.drawPile.group.get(i);
                    if (c.type == CardType.ATTACK) {
                        cardToPlay = c;
                        break;
                    }
                }

                if (cardToPlay != null) {
                    // 3. 将其从抽牌堆中移除
                    AbstractDungeon.player.drawPile.group.remove(cardToPlay);

                    cardToPlay.freeToPlayOnce = true;
                    cardToPlay.exhaustOnUseOnce = false;

                    // 检测目标死活，进行重新索敌
                    AbstractMonster validTarget = this.targetMonster;
                    if (validTarget == null || validTarget.isDeadOrEscaped() || validTarget.currentHealth <= 0) {
                        validTarget = AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
                    }

                    // 动画处理
                    AbstractDungeon.player.limbo.addToBottom(cardToPlay);
                    cardToPlay.current_y = -200.0F * Settings.scale;
                    cardToPlay.target_x = (float)Settings.WIDTH / 2.0F + 200.0F * Settings.scale;
                    cardToPlay.target_y = (float)Settings.HEIGHT / 2.0F;
                    cardToPlay.targetAngle = 0.0F;
                    cardToPlay.lighten(false);
                    cardToPlay.drawScale = 0.12F;
                    cardToPlay.targetDrawScale = 0.75F;
                    cardToPlay.applyPowers();

                    this.addToTop(new UnlimboAction(cardToPlay));
                    this.addToTop(new NewQueueCardAction(cardToPlay, validTarget, false, true));
                }
                this.isDone = true;
            }
        }
    }
}