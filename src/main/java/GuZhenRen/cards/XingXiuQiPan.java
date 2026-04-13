package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.patches.GuZhenRenTags;
import GuZhenRen.powers.BuMieXingBiaoPower;
import GuZhenRen.powers.NianPower;
import GuZhenRen.powers.XingLuoQiBuPower;
import basemod.abstracts.CustomCard;
import com.badlogic.gdx.Gdx;
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

public class XingXiuQiPan extends AbstractShaZhaoCard {
    public static final String ID = GuZhenRen.makeID("XingXiuQiPan");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/XingXiuQiPan.png");

    private static final int COST = 1;

    public static boolean usedTengNuoThisCombat = false;

    private float rotationTimer = 0.0F;
    private int previewIndex = 0;
    private ArrayList<AbstractCard> previewCards = new ArrayList<>();

    public XingXiuQiPan() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardTarget.ENEMY);

        this.setDao(Dao.ZHI_DAO);
        this.tags.add(GuZhenRenTags.XIAN_GU_WU);

        // 将4张预览卡加入到轮播列表
        this.previewCards.add(new OptionFangHu_XingXiuQiPan());
        this.previewCards.add(new OptionZhenCha_XingXiuQiPan(null));
        this.previewCards.add(new OptionTuiSuan_XingXiuQiPan());
        this.previewCards.add(new OptionTengNuo_XingXiuQiPan());

        // 默认显示第一张
        this.cardsToPreview = this.previewCards.get(0);

        this.initializeDescription();
    }

    // 每帧更新逻辑，用于实现预览卡轮播效果
    @Override
    public void update() {
        super.update();

        // 当鼠标悬停在卡牌上时进行轮播计时
        if (this.hb.hovered) {
            this.rotationTimer += Gdx.graphics.getDeltaTime();
            if (this.rotationTimer >= 3.0F) { // 每隔 3.0 秒切换一次
                this.rotationTimer = 0.0F;
                this.previewIndex++;

                if (this.previewIndex >= this.previewCards.size()) {
                    this.previewIndex = 0;
                }

                // 如果“腾挪”已经使用过，预览时直接跳过它
                if (usedTengNuoThisCombat && this.previewCards.get(this.previewIndex) instanceof OptionTengNuo_XingXiuQiPan) {
                    this.previewIndex++;
                    if (this.previewIndex >= this.previewCards.size()) {
                        this.previewIndex = 0;
                    }
                }

                this.cardsToPreview = this.previewCards.get(this.previewIndex);
            }
        } else {
            // 鼠标移开时，重置为第一张
            this.rotationTimer = 0.0F;
            this.previewIndex = 0;
            this.cardsToPreview = this.previewCards.get(0);
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        ArrayList<AbstractCard> choices = new ArrayList<>();
        choices.add(new OptionFangHu_XingXiuQiPan());
        choices.add(new OptionZhenCha_XingXiuQiPan(m));
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
    }

    // 内部类 1：防护
    public static class OptionFangHu_XingXiuQiPan extends CustomCard {
        public static final String ID = GuZhenRen.makeID("OptionFangHu_XingXiuQiPan");
        private static final CardStrings strings = CardCrawlGame.languagePack.getCardStrings(ID);

        public OptionFangHu_XingXiuQiPan() {
            super(ID, strings.NAME, IMG_PATH, -2, strings.DESCRIPTION, CardType.SKILL, CardColorEnum.GUZHENREN_GREY, CardRarity.SPECIAL, CardTarget.NONE);
        }

        @Override
        public void use(AbstractPlayer p, AbstractMonster m) {}
        @Override
        public void upgrade() {}

        @Override
        public void onChoseThisOption() {
            AbstractPlayer p = AbstractDungeon.player;
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new XingLuoQiBuPower(p, 4), 4));
        }
    }

    // 内部类 2：侦查
    public static class OptionZhenCha_XingXiuQiPan extends CustomCard {
        public static final String ID = GuZhenRen.makeID("OptionZhenCha_XingXiuQiPan");
        private static final CardStrings strings = CardCrawlGame.languagePack.getCardStrings(ID);
        private AbstractMonster targetMonster;

        public OptionZhenCha_XingXiuQiPan(AbstractMonster m) {
            super(ID, strings.NAME, IMG_PATH, -2, strings.DESCRIPTION, CardType.SKILL, CardColorEnum.GUZHENREN_GREY, CardRarity.SPECIAL, CardTarget.NONE);
            this.targetMonster = m;
        }

        @Override
        public void use(AbstractPlayer p, AbstractMonster m) {}
        @Override
        public void upgrade() {}

        @Override
        public void onChoseThisOption() {
            if (this.targetMonster != null && !this.targetMonster.isDeadOrEscaped()) {
                AbstractPlayer p = AbstractDungeon.player;
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this.targetMonster, p, new BuMieXingBiaoPower(this.targetMonster, 1), 1));
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
        public void use(AbstractPlayer p, AbstractMonster m) {}
        @Override
        public void upgrade() {}

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
        public void use(AbstractPlayer p, AbstractMonster m) {}
        @Override
        public void upgrade() {}

        @Override
        public void onChoseThisOption() {
            XingXiuQiPan.usedTengNuoThisCombat = true;
            AbstractDungeon.actionManager.addToBottom(new SkipEnemiesTurnAction());
            AbstractDungeon.actionManager.addToBottom(new PressEndTurnButtonAction());
        }
    }
}