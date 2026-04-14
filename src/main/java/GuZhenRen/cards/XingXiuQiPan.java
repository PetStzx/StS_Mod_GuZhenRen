package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.BuMieXingBiaoPower;
import GuZhenRen.powers.NianPower;
import GuZhenRen.powers.XingLuoQiBuPower;
import GuZhenRen.util.BattleStateManager;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.watcher.ChooseOneAction;
import com.megacrit.cardcrawl.actions.watcher.PressEndTurnButtonAction;
import com.megacrit.cardcrawl.actions.watcher.SkipEnemiesTurnAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

public class XingXiuQiPan extends AbstractXianGuWuCard {
    public static final String ID = GuZhenRen.makeID("XingXiuQiPan");
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/XingXiuQiPan.png");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 1;

    public static boolean usedTengNuoThisCombat = false;


    static {
        BattleStateManager.onBattleStart(() -> XingXiuQiPan.usedTengNuoThisCombat = false);
        BattleStateManager.onPostBattle(() -> XingXiuQiPan.usedTengNuoThisCombat = false);
    }

    public XingXiuQiPan() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardTarget.NONE);

        this.setDao(Dao.ZHI_DAO);

        this.previewCards.add(new OptionFangHu_XingXiuQiPan());
        this.previewCards.add(new OptionZhenCha_XingXiuQiPan());
        this.previewCards.add(new OptionTuiSuan_XingXiuQiPan());
        this.previewCards.add(new OptionTengNuo_XingXiuQiPan());

        this.initializeDescription();
    }


    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        ArrayList<AbstractCard> choices = new ArrayList<>();
        choices.add(new OptionFangHu_XingXiuQiPan());
        choices.add(new OptionZhenCha_XingXiuQiPan());
        choices.add(new OptionTuiSuan_XingXiuQiPan());

        if (!usedTengNuoThisCombat) {
            choices.add(new OptionTengNuo_XingXiuQiPan());
        }

        this.addToBot(new ChooseOneAction(choices));
    }

    @Override
    public void initializeDescription() {
        super.initializeDescription();

        if (cardStrings != null && cardStrings.EXTENDED_DESCRIPTION != null && cardStrings.EXTENDED_DESCRIPTION.length >= 2) {
            this.keywords.add(cardStrings.EXTENDED_DESCRIPTION[0]);
            this.keywords.add(cardStrings.EXTENDED_DESCRIPTION[1]);
        }

        java.util.ArrayList<String> uniqueKeywords = new java.util.ArrayList<>();
        for (String kw : this.keywords) {
            if (!uniqueKeywords.contains(kw)) {
                uniqueKeywords.add(kw);
            }
        }

        this.keywords.clear();
        this.keywords.addAll(uniqueKeywords);
    }

    @Override
    public boolean shouldShow(AbstractCard card) {
        return !usedTengNuoThisCombat || !(card instanceof OptionTengNuo_XingXiuQiPan);
    }

    // 内部类 1：防护
    public static class OptionFangHu_XingXiuQiPan extends CustomCard {
        public static final String ID = GuZhenRen.makeID("OptionFangHu_XingXiuQiPan");
        private static final CardStrings strings = CardCrawlGame.languagePack.getCardStrings(ID);

        public OptionFangHu_XingXiuQiPan() {
            super(ID, strings.NAME, IMG_PATH, -2, strings.DESCRIPTION, CardType.SKILL, CardColorEnum.GUZHENREN_GREY, CardRarity.SPECIAL, CardTarget.NONE);
        }

        @Override
        public void use(AbstractPlayer p, AbstractMonster m) {
        }

        @Override
        public void upgrade() {
        }

        @Override
        public void onChoseThisOption() {
            AbstractPlayer p = AbstractDungeon.player;
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new XingLuoQiBuPower(p, 3), 3));
        }
    }

    // 内部类 2：侦查
    public static class OptionZhenCha_XingXiuQiPan extends CustomCard {
        public static final String ID = GuZhenRen.makeID("OptionZhenCha_XingXiuQiPan");
        private static final CardStrings strings = CardCrawlGame.languagePack.getCardStrings(ID);

        public OptionZhenCha_XingXiuQiPan() {
            super(ID, strings.NAME, IMG_PATH, -2, strings.DESCRIPTION, CardType.SKILL, CardColorEnum.GUZHENREN_GREY, CardRarity.SPECIAL, CardTarget.NONE);
        }

        @Override
        public void use(AbstractPlayer p, AbstractMonster m) {
        }

        @Override
        public void upgrade() {
        }

        @Override
        public void onChoseThisOption() {
            AbstractPlayer p = AbstractDungeon.player;
            for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!mo.isDeadOrEscaped()) {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(mo, p, new BuMieXingBiaoPower(mo, 1), 1));
                }
            }
        }
    }

    // 内部类 3：推算
    public static class OptionTuiSuan_XingXiuQiPan extends CustomCard {
        public static final String ID = GuZhenRen.makeID("OptionTuiSuan_XingXiuQiPan");
        private static final CardStrings strings = CardCrawlGame.languagePack.getCardStrings(ID);

        public OptionTuiSuan_XingXiuQiPan() {
            super(ID, strings.NAME, IMG_PATH, -2, strings.DESCRIPTION, CardType.SKILL, CardColorEnum.GUZHENREN_GREY, CardRarity.SPECIAL, CardTarget.NONE);
        }

        @Override
        public void use(AbstractPlayer p, AbstractMonster m) {
        }

        @Override
        public void upgrade() {
        }

        @Override
        public void onChoseThisOption() {
            AbstractPlayer p = AbstractDungeon.player;
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new NianPower(p, 8), 8));
        }
    }

    // 内部类 4：腾挪
    public static class OptionTengNuo_XingXiuQiPan extends CustomCard {
        public static final String ID = GuZhenRen.makeID("OptionTengNuo_XingXiuQiPan");
        private static final CardStrings strings = CardCrawlGame.languagePack.getCardStrings(ID);

        public OptionTengNuo_XingXiuQiPan() {
            super(ID, strings.NAME, IMG_PATH, -2, strings.DESCRIPTION, CardType.SKILL, CardColorEnum.GUZHENREN_GREY, CardRarity.SPECIAL, CardTarget.NONE);
        }

        @Override
        public void use(AbstractPlayer p, AbstractMonster m) {
        }

        @Override
        public void upgrade() {
        }

        @Override
        public void onChoseThisOption() {
            XingXiuQiPan.usedTengNuoThisCombat = true;
            AbstractDungeon.actionManager.addToBottom(new SkipEnemiesTurnAction());
            AbstractDungeon.actionManager.addToBottom(new PressEndTurnButtonAction());
        }
    }
}