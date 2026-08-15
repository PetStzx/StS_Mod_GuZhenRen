package GuZhenRen.tribulations;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.HunBaoGuPower;
import GuZhenRen.util.TribulationManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.ArrayList;

public class DiZai_HunBaoGu extends AbstractTribulation {

    public DiZai_HunBaoGu() {
        super(
                GuZhenRen.makeID("DiZai_HunBaoGu"),
                "魂爆蛊",
                TribulationManager.TRIBULATION_TEXT[0],
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

                // 根据非爪牙敌人的数量决定发放层数
                int amount = 10;
                if (count == 1) {
                    amount = 20;
                } else if (count == 2) {
                    amount = 14;
                } else if (count == 3) {
                    amount = 10;
                } else if (count >= 4) {
                    amount = 8;
                }

                // 群体发放
                for (AbstractMonster target : targets) {
                    AbstractPower hunBaoGu = new HunBaoGuPower(target, amount);
                    AbstractDungeon.actionManager.addToTop(
                            new ApplyPowerAction(target, target, hunBaoGu, amount)
                    );
                }

                this.isDone = true;
            }
        });
    }
}