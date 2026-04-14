package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.actions.ZhuMoBangAction;
import GuZhenRen.actions.ZhuMoBangRecoverAction;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.XueDianXingBanPower;
import GuZhenRen.powers.XueYuanMarkPower;
import GuZhenRen.powers.XueYuanPower;
import GuZhenRen.util.BattleStateManager;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.watcher.ChooseOneAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

public class ZhuMoBang extends AbstractXianGuWuCard {
    public static final String ID = GuZhenRen.makeID("ZhuMoBang");
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/ZhuMoBang.png");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 1;

    public static boolean usedHuiFuThisCombat = false;

    static {
        BattleStateManager.onBattleStart(() -> ZhuMoBang.usedHuiFuThisCombat = false);
        BattleStateManager.onPostBattle(() -> ZhuMoBang.usedHuiFuThisCombat = false);
    }

    public ZhuMoBang() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardTarget.NONE);

        this.setDao(Dao.XUE_DAO);

        this.previewCards.add(new OptionZhenCha_ZhuMoBang());
        this.previewCards.add(new OptionFangHu_ZhuMoBang());
        this.previewCards.add(new OptionGongFa_ZhuMoBang());
        this.previewCards.add(new OptionHuiFu_ZhuMoBang());

        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        ArrayList<AbstractCard> choices = new ArrayList<>();
        choices.add(new OptionZhenCha_ZhuMoBang());
        choices.add(new OptionFangHu_ZhuMoBang());
        choices.add(new OptionGongFa_ZhuMoBang());

        if (!usedHuiFuThisCombat) {
            choices.add(new OptionHuiFu_ZhuMoBang());
        }

        this.addToBot(new ChooseOneAction(choices));
    }

    @Override
    public void initializeDescription() {
        super.initializeDescription();
        if (cardStrings != null && cardStrings.EXTENDED_DESCRIPTION != null && cardStrings.EXTENDED_DESCRIPTION.length >= 1) {
            this.keywords.add(cardStrings.EXTENDED_DESCRIPTION[0]);
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
        return !usedHuiFuThisCombat || !(card instanceof OptionHuiFu_ZhuMoBang);
    }


    // 内部类 1：侦查
    public static class OptionZhenCha_ZhuMoBang extends CustomCard {
        public static final String ID = GuZhenRen.makeID("OptionZhenCha_ZhuMoBang");
        private static final CardStrings strings = CardCrawlGame.languagePack.getCardStrings(ID);

        public OptionZhenCha_ZhuMoBang() {
            super(ID, strings.NAME, IMG_PATH, -2, strings.DESCRIPTION, CardType.SKILL, CardColorEnum.GUZHENREN_GREY, CardRarity.SPECIAL, CardTarget.NONE);
        }

        @Override
        public void use(AbstractPlayer p, AbstractMonster m) {}
        @Override
        public void upgrade() {}

        @Override
        public void onChoseThisOption() {
            AbstractPlayer p = AbstractDungeon.player;

            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new XueYuanPower(p, 1), 1));

            for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!mo.isDeadOrEscaped()) {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(mo, p, new XueYuanMarkPower(mo, 1), 1));
                }
            }

            AbstractDungeon.actionManager.addToBottom(new com.megacrit.cardcrawl.actions.AbstractGameAction() {
                @Override
                public void update() {
                    boolean hasOther = false;
                    for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                        if (!mo.isDeadOrEscaped() && mo.hasPower(XueYuanMarkPower.POWER_ID)) {
                            hasOther = true;
                            break;
                        }
                    }
                    if (!hasOther && p.hasPower(XueYuanPower.POWER_ID)) {
                        AbstractDungeon.actionManager.addToTop(new com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction(p, p, XueYuanPower.POWER_ID));
                    }
                    this.isDone = true;
                }
            });
        }
    }


    // 内部类 2：防御
    public static class OptionFangHu_ZhuMoBang extends CustomCard {
        public static final String ID = GuZhenRen.makeID("OptionFangHu_ZhuMoBang");
        private static final CardStrings strings = CardCrawlGame.languagePack.getCardStrings(ID);

        public OptionFangHu_ZhuMoBang() {
            super(ID, strings.NAME, IMG_PATH, -2, strings.DESCRIPTION, CardType.SKILL, CardColorEnum.GUZHENREN_GREY, CardRarity.SPECIAL, CardTarget.NONE);
            this.baseBlock = 8;
        }

        @Override
        public void use(AbstractPlayer p, AbstractMonster m) {}
        @Override
        public void upgrade() {}

        @Override
        public void onChoseThisOption() {
            AbstractPlayer p = AbstractDungeon.player;

            this.applyPowers();

            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p, p, this.block));
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new XueDianXingBanPower(p), 1));
        }
    }

    // 内部类 3：攻伐
    public static class OptionGongFa_ZhuMoBang extends CustomCard {
        public static final String ID = GuZhenRen.makeID("OptionGongFa_ZhuMoBang");
        private static final CardStrings strings = CardCrawlGame.languagePack.getCardStrings(ID);

        public OptionGongFa_ZhuMoBang() {
            super(ID, strings.NAME, IMG_PATH, -2, strings.DESCRIPTION, CardType.SKILL, CardColorEnum.GUZHENREN_GREY, CardRarity.SPECIAL, CardTarget.ALL_ENEMY);
            this.baseMagicNumber = 18;
            this.magicNumber = this.baseMagicNumber;
        }

        @Override
        public void use(AbstractPlayer p, AbstractMonster m) {}
        @Override
        public void upgrade() {}

        @Override
        public void onChoseThisOption() {
            AbstractPlayer p = AbstractDungeon.player;

            for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!mo.isDeadOrEscaped()) {
                    int hits = 0;
                    if (mo.hasPower(XueYuanMarkPower.POWER_ID)) {
                        hits = mo.getPower(XueYuanMarkPower.POWER_ID).amount;
                    }
                    for (int i = 0; i < hits; i++) {
                        AbstractDungeon.actionManager.addToBottom(new ZhuMoBangAction(mo, new DamageInfo(p, this.magicNumber, DamageInfo.DamageType.THORNS)));
                    }
                }
            }
        }
    }


    // 内部类 4：恢复
    public static class OptionHuiFu_ZhuMoBang extends CustomCard {
        public static final String ID = GuZhenRen.makeID("OptionHuiFu_ZhuMoBang");
        private static final CardStrings strings = CardCrawlGame.languagePack.getCardStrings(ID);

        public OptionHuiFu_ZhuMoBang() {
            super(ID, strings.NAME, IMG_PATH, -2, strings.DESCRIPTION, CardType.SKILL, CardColorEnum.GUZHENREN_GREY, CardRarity.SPECIAL, CardTarget.NONE);
        }

        @Override
        public void use(AbstractPlayer p, AbstractMonster m) {}
        @Override
        public void upgrade() {}

        @Override
        public void onChoseThisOption() {
            ZhuMoBang.usedHuiFuThisCombat = true;
            AbstractDungeon.actionManager.addToBottom(new ZhuMoBangRecoverAction());
        }
    }
}