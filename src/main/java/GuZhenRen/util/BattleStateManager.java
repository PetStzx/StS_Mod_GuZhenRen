package GuZhenRen.util;

import java.util.ArrayList;
import java.util.List;

public class BattleStateManager {
    private static final List<Runnable> battleStartActions = new ArrayList<>();
    private static final List<Runnable> postBattleActions = new ArrayList<>();

    public static void onBattleStart(Runnable runnable){
        battleStartActions.add(runnable);
    }

    public static void onPostBattle(Runnable runnable){
        postBattleActions.add(runnable);
    }

    public static void publishBattleStart(){
        battleStartActions.forEach(Runnable::run);
    }

    public static void publishPostBattle(){
        postBattleActions.forEach(Runnable::run);
    }
}
