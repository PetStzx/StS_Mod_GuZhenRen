package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DrawReductionPower;

public class SheXinGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("SheXinGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/SheXinGu.png");

    private static final int COST = 1;
    private static final int INITIAL_RANK = 4;

    public SheXinGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.ENEMY);

        setDao(Dao.ZHI_DAO);
        setRank(INITIAL_RANK);
        exhaust = true;
    }

    @Override
    public void use(AbstractPlayer abstractPlayer, AbstractMonster abstractMonster) {
        if (upgraded){
            for (AbstractMonster monster : AbstractDungeon.getCurrRoom().monsters.monsters){
                if (!monster.isDeadOrEscaped()){
                    addToBot(new ApplyPowerAction(monster, abstractPlayer, new StunMonsterPower(monster)));
                }
            }
        }else {
            if (abstractMonster != null){
                addToBot(new ApplyPowerAction(abstractMonster, abstractPlayer, new StunMonsterPower(abstractMonster)));
            }
        }

        DrawReductionPower drawReductionPower = new DrawReductionPower(abstractPlayer, 1);
        ReflectionHacks.setPrivate(drawReductionPower, DrawReductionPower.class, "justApplied", false);
        addToBot(new ApplyPowerAction(abstractPlayer, abstractPlayer, drawReductionPower));
    }

    @Override
    public void upgrade() {
        if (!upgraded){
            upgradeName();
            target = CardTarget.ALL_ENEMY;
            upgradeRank(1);
            myBaseDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }
}
