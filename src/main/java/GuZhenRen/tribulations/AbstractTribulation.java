package GuZhenRen.tribulations;

import GuZhenRen.tribulations.interfaces.ITribulation;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.MinionPower;

import java.util.ArrayList;
import java.util.function.Function;

public abstract class AbstractTribulation implements ITribulation {
    protected String id;
    protected String name;
    protected String tribulationType;
    protected int category;

    public AbstractTribulation(String id, String name, String tribulationType, int category) {
        this.id = id;
        this.name = name;
        this.tribulationType = tribulationType;
        this.category = category;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getTribulationType() {
        return this.tribulationType;
    }

    @Override
    public int getCategory() {
        return this.category;
    }

    /**
     * 获取一个随机的存活怪物（排除爪牙）
     */
    protected AbstractMonster getRandomAliveMonster() {
        ArrayList<AbstractMonster> aliveMonsters = new ArrayList<>();
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (!m.isDeadOrEscaped() && !m.hasPower(MinionPower.POWER_ID)) {
                aliveMonsters.add(m);
            }
        }
        if (!aliveMonsters.isEmpty()) {
            return aliveMonsters.get(AbstractDungeon.miscRng.random(aliveMonsters.size() - 1));
        }
        return null;
    }

    /**
     * 获取所有存活的怪物列表（排除爪牙）
     */
    protected ArrayList<AbstractMonster> getAllAliveMonsters() {
        ArrayList<AbstractMonster> aliveMonsters = new ArrayList<>();
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (!m.isDeadOrEscaped() && !m.hasPower(MinionPower.POWER_ID)) {
                aliveMonsters.add(m);
            }
        }
        return aliveMonsters;
    }

    /**
     * 单体发放：随机寻找一个非爪牙怪物并赋予指定的 Power。
     */
    protected void applyPowerToRandomEnemyAction(Function<AbstractMonster, AbstractPower> powerBuilder) {
        AbstractDungeon.actionManager.addToTop(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractMonster target = getRandomAliveMonster();
                if (target != null) {
                    AbstractPower powerToApply = powerBuilder.apply(target);
                    AbstractDungeon.actionManager.addToTop(
                            new ApplyPowerAction(target, target, powerToApply, powerToApply.amount)
                    );
                }
                this.isDone = true;
            }
        });
    }

    /**
     * 群体发放：给全场所有非爪牙怪物赋予指定的 Power。
     */
    protected void applyPowerToAllEnemiesAction(Function<AbstractMonster, AbstractPower> powerBuilder) {
        AbstractDungeon.actionManager.addToTop(new AbstractGameAction() {
            @Override
            public void update() {
                ArrayList<AbstractMonster> targets = getAllAliveMonsters();
                for (AbstractMonster target : targets) {
                    AbstractPower powerToApply = powerBuilder.apply(target);
                    AbstractDungeon.actionManager.addToTop(
                            new ApplyPowerAction(target, target, powerToApply, powerToApply.amount)
                    );
                }
                this.isDone = true;
            }
        });
    }
}