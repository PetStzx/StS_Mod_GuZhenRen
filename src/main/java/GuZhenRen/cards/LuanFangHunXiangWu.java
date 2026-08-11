package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.YunTouZhuanXiangPower;
import GuZhenRen.util.IProbabilityCard;
import GuZhenRen.util.ProbabilityHelper;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect;

public class LuanFangHunXiangWu extends AbstractShaZhaoCard implements IProbabilityCard {
    public static final String ID = GuZhenRen.makeID("LuanFangHunXiangWu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/LuanFangHunXiangWu.png");

    private static final int COST = 2;
    private static final float INITIAL_CHANCE = 0.50f;

    public float baseChance;

    public LuanFangHunXiangWu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardTarget.ALL_ENEMY);

        this.setDao(Dao.ZHI_DAO);
        this.exhaust = true;
        this.baseChance = INITIAL_CHANCE;
        this.isMultiDamage = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new VFXAction(p, new ShockWaveEffect(p.hb.cX, p.hb.cY, Color.WHITE.cpy(), ShockWaveEffect.ShockWaveType.CHAOTIC), 0.5F));

        float realChance = ProbabilityHelper.getModifiedChance(this, this.baseChance);
        int powerAmount = Math.round(realChance * 100);

        for (AbstractMonster monster : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!monster.isDeadOrEscaped()) {
                this.addToBot(new ApplyPowerAction(monster, p, new StunMonsterPower(monster, 1)));

                if (powerAmount > 0) {
                    this.addToBot(new ApplyPowerAction(monster, p, new YunTouZhuanXiangPower(monster, powerAmount), powerAmount));
                }
            }
        }
    }

    @Override
    public void increaseBaseChance(float amount) {
        this.baseChance += amount;
        if (this.baseChance > 1.0f) this.baseChance = 1.0f;
        if (this.baseChance < 0.0f) this.baseChance = 0.0f;
        this.initializeDescription();
    }

    @Override
    public float getBaseChance() {
        return this.baseChance;
    }

    @Override
    public AbstractShaZhaoCard makeStatEquivalentCopy() {
        LuanFangHunXiangWu c = (LuanFangHunXiangWu) super.makeStatEquivalentCopy();
        c.baseChance = this.baseChance;
        return c;
    }

    @Override
    protected String constructRawDescription() {
        String baseDesc = super.constructRawDescription();
        if (baseDesc.isEmpty()) return "";
        return baseDesc.replace("{CHANCE}", ProbabilityHelper.getDynamicColorString(this, this.baseChance));
    }
}