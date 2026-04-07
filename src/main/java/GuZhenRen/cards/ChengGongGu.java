package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.watcher.ChooseOneAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.SpotlightPlayerEffect;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

import java.util.ArrayList;

public class ChengGongGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("ChengGongGu");
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
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
                CardTarget.NONE);

        this.setDao(Dao.LU_DAO);
        this.setRank(INITIAL_RANK);

        this.exhaust = true;
    }

    // =========================================================================
    // 渲染成金卡的视觉特效
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

    // =========================================================================
    // 打出结算：弹出 3 个选项
    // =========================================================================
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        ArrayList<AbstractCard> choices = new ArrayList<>();
        choices.add(new OptionCaiFu());
        choices.add(new OptionYongSheng());
        choices.add(new OptionZiYou());

        // 呼出选择界面
        this.addToBot(new ChooseOneAction(choices));
    }

    @Override
    public boolean canUpgrade() { return false; }

    @Override
    public void upgrade() { }

    @Override
    public boolean canSpawn(ArrayList<AbstractCard> rewardCards) { return false; }


    // =========================================================================
    // 内部类 1：财富 (获取300金币)
    // =========================================================================
    public static class OptionCaiFu extends CustomCard {
        public static final String ID = GuZhenRen.makeID("OptionCaiFu");
        private static final CardStrings strings = CardCrawlGame.languagePack.getCardStrings(ID);
        private static final String OPTION_IMG = GuZhenRen.assetPath("img/cards/OptionCaiFu.png");

        public OptionCaiFu() {
            super(ID, strings.NAME, OPTION_IMG, -2, strings.DESCRIPTION, CardType.SKILL, CardColorEnum.GUZHENREN_GREY, CardRarity.SPECIAL, CardTarget.NONE);
        }

        @Override
        public void use(AbstractPlayer p, AbstractMonster m) { }
        @Override
        public void upgrade() { }

        @Override
        public void onChoseThisOption() {
            AbstractPlayer p = AbstractDungeon.player;
            p.gainGold(300); // 获得 300 金币

            // 下金币雨与聚光灯特效 (完美对应观者的"财富")
            AbstractDungeon.actionManager.addToBottom(new VFXAction(new RainingGoldEffect(300 * 2, true)));
            AbstractDungeon.actionManager.addToBottom(new VFXAction(new SpotlightPlayerEffect()));
        }
    }

    // =========================================================================
    // 内部类 2：永生 (增加16点最大生命)
    // =========================================================================
    public static class OptionYongSheng extends CustomCard {
        public static final String ID = GuZhenRen.makeID("OptionYongSheng");
        private static final CardStrings strings = CardCrawlGame.languagePack.getCardStrings(ID);
        private static final String OPTION_IMG = GuZhenRen.assetPath("img/cards/OptionYongSheng.png");

        public OptionYongSheng() {
            super(ID, strings.NAME, OPTION_IMG, -2, strings.DESCRIPTION, CardType.SKILL, CardColorEnum.GUZHENREN_GREY, CardRarity.SPECIAL, CardTarget.NONE);
        }

        @Override
        public void use(AbstractPlayer p, AbstractMonster m) { }
        @Override
        public void upgrade() { }

        @Override
        public void onChoseThisOption() {
            AbstractDungeon.actionManager.addToBottom(new VFXAction(new BorderFlashEffect(Color.CHARTREUSE, true)));

            // 增加 16 点最大生命
            AbstractDungeon.player.increaseMaxHp(16, true);
        }
    }

    // =========================================================================
    // 自由 (从牌组移除2张牌)
    // =========================================================================
    public static class OptionZiYou extends CustomCard {
        public static final String ID = GuZhenRen.makeID("OptionZiYou");
        private static final CardStrings strings = CardCrawlGame.languagePack.getCardStrings(ID);
        private static final String OPTION_IMG = GuZhenRen.assetPath("img/cards/OptionZiYou.png");

        public OptionZiYou() {
            super(ID, strings.NAME, OPTION_IMG, -2, strings.DESCRIPTION, CardType.SKILL, CardColorEnum.GUZHENREN_GREY, CardRarity.SPECIAL, CardTarget.NONE);
        }

        @Override
        public void use(AbstractPlayer p, AbstractMonster m) { }
        @Override
        public void upgrade() { }

        @Override
        public void onChoseThisOption() {
            AbstractDungeon.actionManager.addToBottom(new VFXAction(new BorderFlashEffect(Color.WHITE, true)));

            AbstractDungeon.actionManager.addToBottom(new ZiYouRemoveAction());
        }
    }

    // =========================================================================
    // 打开卡组移除 2 张牌
    // =========================================================================
    public static class ZiYouRemoveAction extends AbstractGameAction {
        public ZiYouRemoveAction() {
            this.actionType = ActionType.CARD_MANIPULATION;
            this.duration = Settings.ACTION_DUR_FAST;
        }

        @Override
        public void update() {
            if (this.duration == Settings.ACTION_DUR_FAST) {
                CardGroup purgeableCards = AbstractDungeon.player.masterDeck.getPurgeableCards();

                // 如果没有可删的牌，直接结束
                if (purgeableCards.isEmpty()) {
                    this.isDone = true;
                    return;
                }

                AbstractDungeon.gridSelectScreen.open(purgeableCards, 2, ChengGongGu.cardStrings.EXTENDED_DESCRIPTION[0], false, false, false, true);
                this.tickDuration();
                return;
            }

            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                    AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(c, (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
                    AbstractDungeon.player.masterDeck.removeCard(c);
                }
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
            }

            this.tickDuration();
        }
    }
}