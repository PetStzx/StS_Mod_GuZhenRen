package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.actions.ZhuMoBangAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ZhuMoBang extends AbstractShaZhaoCard {
    public static final String ID = GuZhenRen.makeID("ZhuMoBang");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/ZhuMoBang.png");

    private static final int COST = 3;
    private static final int DAMAGE = 20;

    public ZhuMoBang() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardTarget.ALL_ENEMY);

        this.setDao(Dao.XUE_DAO);
        this.baseDamage = DAMAGE;
        this.isMultiDamage = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        for (int i = 0; i < AbstractDungeon.getCurrRoom().monsters.monsters.size(); i++) {
            AbstractMonster mo = AbstractDungeon.getCurrRoom().monsters.monsters.get(i);

            if (!mo.isDeadOrEscaped()) {
                // 取出该怪物对应的真实伤害值
                int exactDamage = this.multiDamage[i];
                this.addToBot(new ZhuMoBangAction(mo, new DamageInfo(p, exactDamage, this.damageTypeForTurn)));
            }
        }
    }
}