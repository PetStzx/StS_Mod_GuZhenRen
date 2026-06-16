package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.patches.GuZhenRenTags;
import GuZhenRen.powers.JiTuPower;
import GuZhenRen.util.BattleStateManager;
import basemod.abstracts.CustomCard;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.watcher.ChooseOneAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

public class AnTuZhongShanBao extends AbstractXianGuWuCard {
    public static final String ID = GuZhenRen.makeID("AnTuZhongShanBao");
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/AnTuZhongShanBao.png");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 2;

    public static boolean usedBuDongRuShanThisCombat = false;

    static {
        BattleStateManager.onBattleStart(() -> AnTuZhongShanBao.usedBuDongRuShanThisCombat = false);
        BattleStateManager.onPostBattle(() -> AnTuZhongShanBao.usedBuDongRuShanThisCombat = false);
    }

    public AnTuZhongShanBao() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, CardType.SKILL, CardTarget.NONE);
        this.setDao(Dao.TU_DAO);

        this.previewCards.add(new OptionJiTuChengShan_AnTuZhongShanBao());
        this.previewCards.add(new OptionRuTuWeiAn_AnTuZhongShanBao());
        this.previewCards.add(new OptionJuanTuChongLai_AnTuZhongShanBao(this));
        this.previewCards.add(new OptionBuDongRuShan_AnTuZhongShanBao());

        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        ArrayList<AbstractCard> choices = new ArrayList<>();

        OptionJiTuChengShan_AnTuZhongShanBao option1 = new OptionJiTuChengShan_AnTuZhongShanBao(this);
        option1.applyPowers();

        choices.add(option1);
        choices.add(new OptionRuTuWeiAn_AnTuZhongShanBao());
        choices.add(new OptionJuanTuChongLai_AnTuZhongShanBao(this));

        if (!usedBuDongRuShanThisCombat) {
            choices.add(new OptionBuDongRuShan_AnTuZhongShanBao());
        }

        this.addToBot(new ChooseOneAction(choices));
    }

    @Override
    public void initializeDescription() {
        super.initializeDescription();

        if (cardStrings != null && cardStrings.EXTENDED_DESCRIPTION != null) {
            for (String kw : cardStrings.EXTENDED_DESCRIPTION) {
                this.keywords.add(kw);
            }
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
        return !usedBuDongRuShanThisCombat || !(card instanceof OptionBuDongRuShan_AnTuZhongShanBao);
    }

    // =================================================================================
    // 积土成山
    // =================================================================================
    public static class OptionJiTuChengShan_AnTuZhongShanBao extends CustomCard {
        public static final String ID = GuZhenRen.makeID("OptionJiTuChengShan_AnTuZhongShanBao");
        private static final CardStrings strings = CardCrawlGame.languagePack.getCardStrings(ID);

        private boolean showDynamicText = false;
        private java.util.UUID parentUuid;

        public OptionJiTuChengShan_AnTuZhongShanBao(AbstractCard parentCard) {
            super(ID, strings.NAME, IMG_PATH, -2, strings.DESCRIPTION, CardType.SKILL, CardColorEnum.GUZHENREN_GREY, CardRarity.SPECIAL, CardTarget.NONE);
            this.baseBlock = this.block = 8;
            this.baseMagicNumber = this.magicNumber = 0;
            this.parentUuid = parentCard != null ? parentCard.uuid : null;
        }

        public OptionJiTuChengShan_AnTuZhongShanBao() {
            this(null);
        }

        private int calculateHits() {
            if (!AbstractDungeon.isPlayerInDungeon() || AbstractDungeon.player == null) return 0;
            int count = 0;

            for (AbstractCard c : AbstractDungeon.player.hand.group) {
                boolean isSelf = parentUuid != null && c.uuid.equals(parentUuid);
                if (c.hasTag(GuZhenRenTags.TU_DAO) && !isSelf) {
                    count++;
                }
            }
            return count;
        }

        @Override
        public void applyPowers() {
            super.applyPowers();
            this.baseMagicNumber = calculateHits();
            this.magicNumber = this.baseMagicNumber;
            this.isMagicNumberModified = true;

            this.showDynamicText = true;
            if (strings.EXTENDED_DESCRIPTION != null) {
                this.rawDescription = strings.DESCRIPTION + strings.EXTENDED_DESCRIPTION[0];
                this.initializeDescription();
            }
        }

        @Override
        public void onMoveToDiscard() {
            this.showDynamicText = false;
            this.rawDescription = strings.DESCRIPTION;
            this.initializeDescription();
        }

        @Override
        public void triggerOnExhaust() {
            this.showDynamicText = false;
            this.rawDescription = strings.DESCRIPTION;
            this.initializeDescription();
        }

        @Override
        public void use(AbstractPlayer p, AbstractMonster m) {}
        @Override
        public void upgrade() {}

        @Override
        public void onChoseThisOption() {
            AbstractPlayer p = AbstractDungeon.player;
            int hits = this.magicNumber;
            if (hits > 0) {
                for (int i = 0; i < hits; i++) {
                    AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p, p, this.block));
                }
            }
        }
    }

    // =================================================================================
    // 入土为安
    // =================================================================================
    public static class OptionRuTuWeiAn_AnTuZhongShanBao extends CustomCard {
        public static final String ID = GuZhenRen.makeID("OptionRuTuWeiAn_AnTuZhongShanBao");
        private static final CardStrings strings = CardCrawlGame.languagePack.getCardStrings(ID);

        public OptionRuTuWeiAn_AnTuZhongShanBao() {
            super(ID, strings.NAME, IMG_PATH, -2, strings.DESCRIPTION, CardType.SKILL, CardColorEnum.GUZHENREN_GREY, CardRarity.SPECIAL, CardTarget.NONE);
        }

        @Override
        public void use(AbstractPlayer p, AbstractMonster m) {}
        @Override
        public void upgrade() {}

        @Override
        public void onChoseThisOption() {
            AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
                @Override
                public void update() {
                    for (AbstractCard c : AbstractDungeon.player.hand.group) {
                        HuaShiGu.HuaShiModifier mod = null;
                        for (basemod.abstracts.AbstractCardModifier m : CardModifierManager.modifiers(c)) {
                            if (m instanceof HuaShiGu.HuaShiModifier) {
                                mod = (HuaShiGu.HuaShiModifier) m;
                                break;
                            }
                        }
                        if (mod != null) {
                            mod.amount += 3;
                            c.initializeDescription();
                        } else {
                            CardModifierManager.addModifier(c, new HuaShiGu.HuaShiModifier(3));
                        }
                        c.superFlash();
                        if (c instanceof AbstractGuZhenRenCard) {
                            ((AbstractGuZhenRenCard) c).changeDao(AbstractGuZhenRenCard.Dao.TU_DAO);
                        }
                    }
                    this.isDone = true;
                }
            });
        }
    }

    // =================================================================================
    // 卷土重来
    // =================================================================================
    public static class OptionJuanTuChongLai_AnTuZhongShanBao extends CustomCard {
        public static final String ID = GuZhenRen.makeID("OptionJuanTuChongLai_AnTuZhongShanBao");
        public static final CardStrings strings = CardCrawlGame.languagePack.getCardStrings(ID);

        private java.util.UUID parentUuid;

        public OptionJuanTuChongLai_AnTuZhongShanBao(AbstractCard parentCard) {
            super(ID, strings.NAME, IMG_PATH, -2, strings.DESCRIPTION, CardType.SKILL, CardColorEnum.GUZHENREN_GREY, CardRarity.SPECIAL, CardTarget.NONE);
            this.parentUuid = parentCard != null ? parentCard.uuid : null;
        }

        public OptionJuanTuChongLai_AnTuZhongShanBao() {
            this(null);
        }

        @Override
        public void use(AbstractPlayer p, AbstractMonster m) {}
        @Override
        public void upgrade() {}

        @Override
        public void onChoseThisOption() {
            AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
                @Override
                public void update() {
                    AbstractPlayer p = AbstractDungeon.player;
                    ArrayList<AbstractCard> cardsToMove = new ArrayList<>();

                    for (AbstractCard c : p.discardPile.group) {
                        boolean isSelf = parentUuid != null && c.uuid.equals(parentUuid);
                        if (c.hasTag(GuZhenRenTags.TU_DAO) && !isSelf) {
                            cardsToMove.add(c);
                        }
                    }

                    int count = cardsToMove.size();
                    for (AbstractCard c : cardsToMove) {
                        p.discardPile.moveToDeck(c, true);
                    }
                    if (count > 0) {
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new JiTuPower(p, count * 3), count * 3));
                    }
                    this.isDone = true;
                }
            });
        }
    }

    // =================================================================================
    // 不动如山
    // =================================================================================
    public static class OptionBuDongRuShan_AnTuZhongShanBao extends CustomCard {
        public static final String ID = GuZhenRen.makeID("OptionBuDongRuShan_AnTuZhongShanBao");
        private static final CardStrings strings = CardCrawlGame.languagePack.getCardStrings(ID);

        public OptionBuDongRuShan_AnTuZhongShanBao() {
            super(ID, strings.NAME, IMG_PATH, -2, strings.DESCRIPTION, CardType.SKILL, CardColorEnum.GUZHENREN_GREY, CardRarity.SPECIAL, CardTarget.NONE);
            this.baseBlock = this.block = 30;
            this.baseMagicNumber = this.magicNumber = 30;
        }

        @Override
        public void use(AbstractPlayer p, AbstractMonster m) {}
        @Override
        public void upgrade() {}

        @Override
        public void onChoseThisOption() {
            AnTuZhongShanBao.usedBuDongRuShanThisCombat = true;
            AbstractPlayer p = AbstractDungeon.player;
            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p, p, this.block));
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new JiTuPower(p, this.magicNumber), this.magicNumber));
        }
    }
}