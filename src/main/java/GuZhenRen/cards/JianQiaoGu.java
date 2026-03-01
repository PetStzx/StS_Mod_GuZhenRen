package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.actions.JianQiaoGuAction;
import GuZhenRen.patches.CardColorEnum;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class JianQiaoGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("JianQiaoGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/JianQiaoGu.png");

    private static final int COST = 1;
    private static final int INITIAL_RANK = 4; // 4转

    public JianQiaoGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON, // 蓝卡
                CardTarget.SELF);

        this.setDao(Dao.JIAN_DAO);
        this.setRank(INITIAL_RANK);
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new JianQiaoGuAction()); // 调用重命名后的Action
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeRank(1); // 4转 -> 5转
            this.selfRetain = true;
            this.myBaseDescription = UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }

    // 【静态内部类】纯净版的 Modifier
    @AbstractCardModifier.SaveIgnore
    public static class JianQiaoModifier extends AbstractCardModifier {
        public static final String MODIFIER_ID = GuZhenRen.makeID("JianQiaoModifier");

        private boolean isBuffed = false;

        @Override
        public void onInitialApplication(AbstractCard card) {
            card.selfRetain = true;
        }

        @Override
        public void onRetained(AbstractCard card) {
            if (!this.isBuffed) {
                this.isBuffed = true;
            }
        }

        @Override
        public float modifyDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
            if (this.isBuffed && damage > 0) {
                return damage * 2; // 处于增幅状态时伤害翻倍
            }
            return damage;
        }

        @Override
        public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
            this.isBuffed = false; // 打出后重置状态
            card.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
        }

        @Override
        public String modifyDescription(String rawDescription, AbstractCard card) {
            String trimmed = rawDescription.trim();

            // 只有在文本不以保留结尾时，才给它加上后缀
            if (!trimmed.endsWith("保留 。") && !trimmed.endsWith("保留。") && !trimmed.endsWith("保留")) {
                return rawDescription + " 保留 。";
            }
            return rawDescription;
        }

        @Override
        public void onUpdate(AbstractCard card) {
            if (this.isBuffed) {
                card.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();

                if (card.costForTurn > 0) {
                    card.setCostForTurn(0);
                }
            }
        }

        @Override
        public String identifier(AbstractCard card) {
            return MODIFIER_ID;
        }

        @Override
        public AbstractCardModifier makeCopy() {
            JianQiaoModifier copy = new JianQiaoModifier();
            copy.isBuffed = this.isBuffed;
            return copy;
        }
    }
}