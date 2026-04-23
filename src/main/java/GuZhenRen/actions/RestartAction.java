package GuZhenRen.actions;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.DeathScreen;

import static GuZhenRen.util.RestartRunHelper.restartRun;

public class RestartAction extends AbstractGameAction {


    public RestartAction() {

    }

    public void update() {
        AbstractDungeon.deathScreen = new DeathScreen(AbstractDungeon.getMonsters());
        restartRun();
        this.isDone = true;
    }
}
