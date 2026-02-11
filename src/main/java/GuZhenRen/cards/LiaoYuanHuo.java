package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.FenShaoPower;
import GuZhenRen.powers.YanDaoDaoHenPower;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.ScreenOnFireEffect;

public class LiaoYuanHuo extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("LiaoYuanHuo");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/LiaoYuanHuo.png");

    private static final int COST = 1;
    private static final int INITIAL_RANK = 5;

    private static final int TIMES = 3;
    private static final int UPGRADE_TIMES = 1;

    // 基础焚烧层数
    private static final int FEN_SHAO_BASE = 2;

    public LiaoYuanHuo() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.RARE,
                CardTarget.ALL_ENEMY);

        this.setDao(Dao.YAN_DAO);

        // 1. 第一魔法值：次数
        this.baseMagicNumber = this.magicNumber = TIMES;

        // 2. 第二魔法值：层数
        this.baseSecondMagicNumber = this.secondMagicNumber = FEN_SHAO_BASE;

        this.setRank(INITIAL_RANK);

        // 初始预览：普通灼伤
        this.cardsToPreview = new Burn();
    }

    @Override
    public void applyPowers() {
        // 每次计算前重置基础值
        this.baseSecondMagicNumber = FEN_SHAO_BASE;
        this.secondMagicNumber = this.baseSecondMagicNumber;

        super.applyPowers();

        // 道痕加成逻辑
        int bonus = 0;
        if (AbstractDungeon.player.hasPower(YanDaoDaoHenPower.POWER_ID)) {
            bonus = AbstractDungeon.player.getPower(YanDaoDaoHenPower.POWER_ID).amount / 2;
        }

        if (bonus > 0) {
            this.secondMagicNumber += bonus;
            this.isSecondMagicNumberModified = true;
        }

        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new VFXAction(p, new ScreenOnFireEffect(), 1.0F));

        for (int i = 0; i < this.magicNumber; i++) {
            for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!mo.isDeadOrEscaped()) {
                    this.addToBot(new ApplyPowerAction(mo, p,
                            new FenShaoPower(mo, this.baseSecondMagicNumber),
                            this.baseSecondMagicNumber, true));
                }
            }
        }

        // 生成灼伤
        AbstractCard c = new Burn();
        if (this.upgraded) {
            c.upgrade(); // 如果燎原火升级了，生成的灼伤也升级
        }
        this.addToBot(new MakeTempCardInHandAction(c, 1));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_TIMES);
            this.upgradeRank(1);

            // 1. 更新预览卡牌为 灼伤+
            AbstractCard c = new Burn();
            c.upgrade();
            this.cardsToPreview = c;

            // 2. 【核心修复】 更新基础描述文本变量
            // 使用 myBaseDescription 而不是 rawDescription
            // 这样 initializeDescription() 时才不会被旧文本覆盖
            if (cardStrings.UPGRADE_DESCRIPTION != null) {
                this.myBaseDescription = cardStrings.UPGRADE_DESCRIPTION;
            }

            this.initializeDescription();
        }
    }
}