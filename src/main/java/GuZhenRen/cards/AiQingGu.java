package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.SmokeBombEffect;

import java.util.ArrayList;

public class AiQingGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("AiQingGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/AiQingGu.png");

    private static final int COST = -2; // 不可被打出
    private static final int INITIAL_RANK = 9;

    public AiQingGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.RARE,
                CardTarget.NONE);

        this.setDao(Dao.ZHI_DAO);
        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        return false;
    }

    @Override
    public boolean canUpgrade() {
        return false;
    }

    @Override
    public void upgrade() {
    }

    @Override
    public void triggerWhenDrawn() {
        this.addToBot(new AiQingGuAction(this));
    }

    // =========================================================================
    // 专属 Action
    // =========================================================================
    public static class AiQingGuAction extends AbstractGameAction {
        private final AbstractCard card;
        private static ArrayList<AbstractCard> shaZhaoCache = null;

        public AiQingGuAction(AbstractCard card) {
            this.card = card;
            this.actionType = ActionType.SPECIAL;
        }

        @Override
        public void update() {
            AbstractPlayer p = AbstractDungeon.player;

            // 1. 判定随机正面效果 (0-99)
            int posRoll = AbstractDungeon.cardRandomRng.random(0, 99);

            if (posRoll < 60) {
                // 60%：随机杀招
                if (shaZhaoCache == null) {
                    shaZhaoCache = new ArrayList<>();
                    for (AbstractCard c : CardLibrary.getAllCards()) {
                        // 排除特殊衍生杀招“送友风·送别”
                        if (c instanceof AbstractShaZhaoCard && !c.cardID.equals(SongYouFengSongBie.ID)) {
                            shaZhaoCache.add(c);
                        }
                    }
                }

                if (!shaZhaoCache.isEmpty()) {
                    AbstractCard randomShaZhao = shaZhaoCache.get(AbstractDungeon.cardRandomRng.random(shaZhaoCache.size() - 1)).makeCopy();
                    AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(randomShaZhao, 1));
                }
            } else if (posRoll < 75) {
                // 15%：获得随机遗物 (保持纯净原生逻辑)
                AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
                    @Override
                    public void update() {
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(
                                (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F,
                                AbstractDungeon.returnRandomRelic(AbstractDungeon.returnRandomRelicTier())
                        );
                        this.isDone = true;
                    }
                });
            } else if (posRoll < 90) {
                // 15%：回复 15 生命
                AbstractDungeon.actionManager.addToBottom(new HealAction(p, p, 15));
            } else {
                // 10%：逃离本场战斗
                AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if (!AbstractDungeon.getCurrRoom().smoked) {
                            AbstractDungeon.getCurrRoom().smoked = true;
                            AbstractDungeon.actionManager.addToTop(new VFXAction(new SmokeBombEffect(p.hb.cX, p.hb.cY)));
                            p.hideHealthBar();
                            p.isEscaping = true;
                            p.flipHorizontal = !p.flipHorizontal;
                            AbstractDungeon.overlayMenu.endTurnButton.disable();
                            p.escapeTimer = 2.5F;
                        }
                        this.isDone = true;
                    }
                });
            }

            // 2. 判定随机负面效果 (0-99)
            int negRoll = AbstractDungeon.cardRandomRng.random(0, 99);

            if (negRoll < 25) {
                // 25%：失去 6 点生命
                AbstractDungeon.actionManager.addToBottom(new LoseHPAction(p, p, 6));
            } else if (negRoll < 50) {
                // 25%：失去 3 点生命上限
                AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
                    @Override
                    public void update() {
                        p.decreaseMaxHealth(3);
                        this.isDone = true;
                    }
                });
            } else if (negRoll < 75) {
                // 25%：失去 2 点力量
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new StrengthPower(p, -2), -2));
            } else {
                // 25%：失去 2 点敏捷
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new DexterityPower(p, -2), -2));
            }

            // 3. 消耗
            AbstractDungeon.actionManager.addToBottom(new ExhaustSpecificCardAction(this.card, p.hand));

            this.isDone = true;
        }
    }
}