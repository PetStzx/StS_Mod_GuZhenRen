package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.GuoPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.ArrayList;

public class LaiYinQuGuo extends AbstractShaZhaoCard {
    public static final String ID = GuZhenRen.makeID("LaiYinQuGuo");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/LaiYinQuGuo.png");

    private static final int COST = 2;

    public LaiYinQuGuo() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardTarget.ALL_ENEMY);

        this.setDao(Dao.MU_DAO);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                ArrayList<GuoPower> fruitsToTransfer = new ArrayList<>();
                for (AbstractPower power : p.powers) {
                    if (power instanceof GuoPower) {
                        fruitsToTransfer.add((GuoPower) power);
                    }
                }

                if (!fruitsToTransfer.isEmpty()) {
                    ArrayList<AbstractMonster> validEnemies = new ArrayList<>();
                    for (AbstractMonster monster : AbstractDungeon.getCurrRoom().monsters.monsters) {
                        if (!monster.isDeadOrEscaped() && !monster.halfDead) {
                            validEnemies.add(monster);
                        }
                    }

                    if (validEnemies.isEmpty()) {
                        for (GuoPower fruit : fruitsToTransfer) {
                            AbstractDungeon.actionManager.addToTop(new RemoveSpecificPowerAction(p, p, fruit.ID));
                        }
                    } else {
                        for (GuoPower fruit : fruitsToTransfer) {
                            AbstractMonster target = validEnemies.get(AbstractDungeon.cardRandomRng.random(validEnemies.size() - 1));

                            GuoPower newFruit = fruit.makeTransferCopy(target);

                            AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(target, p, newFruit, newFruit.amount));
                            AbstractDungeon.actionManager.addToTop(new RemoveSpecificPowerAction(p, p, fruit.ID));
                        }
                    }
                }
                this.isDone = true;
            }
        });
    }
}