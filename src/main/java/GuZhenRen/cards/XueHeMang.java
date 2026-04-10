package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.BiteEffect;

public class XueHeMang extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("XueHeMang");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/XueHeMang.png");

    private static final int COST = 2;
    private static final int DAMAGE = 14;
    private static final int UPGRADE_PLUS_DAMAGE = 4; // 14 -> 18
    private static final int MAGIC = 4; // 初始每次加 4 伤
    private static final int UPGRADE_PLUS_MAGIC = 2; // 升级后每次加 6 伤
    private static final int INITIAL_RANK = 4;

    public XueHeMang() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.ENEMY);

        this.setDao(Dao.XUE_DAO);

        this.baseDamage = DAMAGE;
        this.baseMagicNumber = this.magicNumber = MAGIC;

        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (m != null) {
            this.addToBot(new VFXAction(new BiteEffect(m.hb.cX, m.hb.cY - 40.0F * Settings.scale, Color.SCARLET.cpy()), 0.3F));
        }

        this.addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.NONE));
    }

    @Override
    public void tookDamage() {
        // 增加基础伤害
        this.baseDamage += this.magicNumber;

        // 手牌中发红光提示伤害已增加
        if (AbstractDungeon.player != null && AbstractDungeon.player.hand.contains(this)) {
            this.superFlash(Color.SCARLET.cpy());
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(UPGRADE_PLUS_DAMAGE);
            this.upgradeMagicNumber(UPGRADE_PLUS_MAGIC);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }
}