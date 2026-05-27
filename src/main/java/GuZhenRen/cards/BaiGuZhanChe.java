package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.patches.GuZhenRenTags;
import GuZhenRen.powers.GuCiPower;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.watcher.ChooseOneAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ThornsPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;

import java.util.ArrayList;

public class BaiGuZhanChe extends AbstractXianGuWuCard {
    public static final String ID = GuZhenRen.makeID("BaiGuZhanChe");
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/BaiGuZhanChe.png");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 1;

    public BaiGuZhanChe() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardTarget.NONE);

        this.setDao(Dao.GU_DAO);

        this.tags.remove(GuZhenRenTags.XIAN_GU_WU);
        this.tags.add(GuZhenRenTags.FAN_GU_WU);

        this.previewCards.add(new OptionGongFa_BaiGuZhanChe());
        this.previewCards.add(new OptionFangHu_BaiGuZhanChe());
        this.previewCards.add(new OptionYiDong_BaiGuZhanChe());

        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        ArrayList<AbstractCard> choices = new ArrayList<>();
        choices.add(new OptionGongFa_BaiGuZhanChe());
        choices.add(new OptionFangHu_BaiGuZhanChe());
        choices.add(new OptionYiDong_BaiGuZhanChe());

        this.addToBot(new ChooseOneAction(choices));
    }

    // ==========================================================
    // 攻伐
    // ==========================================================
    public static class OptionGongFa_BaiGuZhanChe extends CustomCard {
        public static final String ID = GuZhenRen.makeID("OptionGongFa_BaiGuZhanChe");
        private static final CardStrings strings = CardCrawlGame.languagePack.getCardStrings(ID);

        public OptionGongFa_BaiGuZhanChe() {
            super(ID, strings.NAME, IMG_PATH, -2, strings.DESCRIPTION, CardType.SKILL, CardColorEnum.GUZHENREN_GREY, CardRarity.SPECIAL, CardTarget.NONE);
        }

        @Override
        public void use(AbstractPlayer p, AbstractMonster m) {}
        @Override
        public void upgrade() {}

        @Override
        public void onChoseThisOption() {
            AbstractPlayer p = AbstractDungeon.player;
            int damage = p.currentBlock;

            if (damage > 0) {
                for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (!mo.isDeadOrEscaped()) {
                        AbstractDungeon.actionManager.addToBottom(new DamageAction(mo,
                                new DamageInfo(p, damage, DamageInfo.DamageType.THORNS),
                                AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                    }
                }
            }

            for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!mo.isDeadOrEscaped()) {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(mo, p, new VulnerablePower(mo, 2, false), 2));
                }
            }
        }
    }

    // ==========================================================
    // 防御
    // ==========================================================
    public static class OptionFangHu_BaiGuZhanChe extends CustomCard {
        public static final String ID = GuZhenRen.makeID("OptionFangHu_BaiGuZhanChe");
        private static final CardStrings strings = CardCrawlGame.languagePack.getCardStrings(ID);

        public OptionFangHu_BaiGuZhanChe() {
            super(ID, strings.NAME, IMG_PATH, -2, strings.DESCRIPTION, CardType.SKILL, CardColorEnum.GUZHENREN_GREY, CardRarity.SPECIAL, CardTarget.NONE);
        }

        @Override
        public void use(AbstractPlayer p, AbstractMonster m) {}
        @Override
        public void upgrade() {}

        @Override
        public void onChoseThisOption() {
            AbstractPlayer p = AbstractDungeon.player;
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new ThornsPower(p, 3), 3));
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new GuCiPower(p, 3), 3));
        }
    }

    // ==========================================================
    // 移动
    // ==========================================================
    public static class OptionYiDong_BaiGuZhanChe extends CustomCard {
        public static final String ID = GuZhenRen.makeID("OptionYiDong_BaiGuZhanChe");
        private static final CardStrings strings = CardCrawlGame.languagePack.getCardStrings(ID);

        public OptionYiDong_BaiGuZhanChe() {
            super(ID, strings.NAME, IMG_PATH, -2, strings.DESCRIPTION, CardType.SKILL, CardColorEnum.GUZHENREN_GREY, CardRarity.SPECIAL, CardTarget.NONE);
        }

        @Override
        public void use(AbstractPlayer p, AbstractMonster m) {}
        @Override
        public void upgrade() {}

        @Override
        public void onChoseThisOption() {
            AbstractPlayer p = AbstractDungeon.player;
            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p, p, 18));

            for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!mo.isDeadOrEscaped() && mo.hasPower(VulnerablePower.POWER_ID)) {
                    int hits = mo.getPower(VulnerablePower.POWER_ID).amount;
                    for (int i = 0; i < hits; i++) {
                        AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
                            @Override
                            public void update() {
                                if (mo != null && !mo.isDeadOrEscaped() && !mo.halfDead) {
                                    mo.useFastAttackAnimation();
                                    AbstractDungeon.actionManager.addToTop(new DamageAction(p,
                                            new DamageInfo(mo, 1, DamageInfo.DamageType.NORMAL),
                                            AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                                }
                                this.isDone = true;
                            }
                        });
                    }
                }
            }
        }
    }
}