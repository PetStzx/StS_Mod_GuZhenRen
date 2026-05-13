package GuZhenRen.tribulations;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.SuoPower;
import GuZhenRen.powers.ZhenPower;
import GuZhenRen.util.TribulationManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.ArrayList;

public class HaoJie_ZhenSuo extends AbstractTribulation {

    public HaoJie_ZhenSuo() {
        super(
                GuZhenRen.makeID("HaoJie_ZhenSuo"),
                "镇锁",
                TribulationManager.TRIBULATION_TEXT[2],
                2
        );
    }

    @Override
    public void atPreBattle(AbstractPower power) {
        AbstractDungeon.actionManager.addToTop(new AbstractGameAction() {
            @Override
            public void update() {
                ArrayList<AbstractMonster> targets = getAllAliveMonsters();
                int count = targets.size();

                if (count == 1) {
                    // 只有一个合法的敌人，把镇和锁都发给它
                    AbstractMonster target = targets.get(0);

                    AbstractPower zhen = new ZhenPower(target);
                    AbstractPower suo = new SuoPower(target);

                    AbstractDungeon.actionManager.addToTop(
                            new ApplyPowerAction(target, target, suo, suo.amount)
                    );
                    AbstractDungeon.actionManager.addToTop(
                            new ApplyPowerAction(target, target, zhen, zhen.amount)
                    );

                } else if (count >= 2) {
                    // 有两个及以上的敌人，随机选两个不同的分别发放

                    int index1 = AbstractDungeon.miscRng.random(targets.size() - 1);
                    AbstractMonster target1 = targets.get(index1);
                    targets.remove(index1);

                    int index2 = AbstractDungeon.miscRng.random(targets.size() - 1);
                    AbstractMonster target2 = targets.get(index2);

                    AbstractPower zhen = new ZhenPower(target1);
                    AbstractPower suo = new SuoPower(target2);

                    AbstractDungeon.actionManager.addToTop(
                            new ApplyPowerAction(target2, target2, suo, suo.amount)
                    );
                    AbstractDungeon.actionManager.addToTop(
                            new ApplyPowerAction(target1, target1, zhen, zhen.amount)
                    );
                }

                this.isDone = true;
            }
        });
    }
}