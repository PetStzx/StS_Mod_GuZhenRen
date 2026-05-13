package GuZhenRen.util;

import GuZhenRen.GuZhenRen;
import GuZhenRen.tribulations.interfaces.ITribulation;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import GuZhenRen.tribulations.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class TribulationManager {
    public static String[] TRIBULATION_TEXT;

    public static int currentCombatTribulationIndex = -1;

    static {
        BattleStateManager.onBattleStart(() -> TribulationManager.currentCombatTribulationIndex = -1);
        BattleStateManager.onPostBattle(() -> TribulationManager.currentCombatTribulationIndex = -1);
    }

    public static final Map<String, ITribulation> ALL_TRIBULATIONS = new HashMap<>();
    public static final Map<String, ArrayList<ITribulation>> TRIBULATION_POOLS = new HashMap<>();

    public static void initialize() {
        ALL_TRIBULATIONS.clear();
        TRIBULATION_POOLS.clear();

        TRIBULATION_TEXT = CardCrawlGame.languagePack.getUIString(GuZhenRen.makeID("TribulationNames")).TEXT;

        for (String type : TRIBULATION_TEXT) {
            TRIBULATION_POOLS.put(type, new ArrayList<>());
        }

        // =========================================================================
        // 灾劫注册区
        // =========================================================================
        // 1. 地灾
        register(new DiZai_HuoXiNi());
        register(new DiZai_ChiTongHuoYi());
        register(new DiZai_ZhuoShang());
        register(new DiZai_ZiAi());
        register(new DiZai_ShuiMuTianHuaGu());
        register(new DiZai_LangJing());
        register(new DiZai_HunBaoGu());

        // 2. 天劫
        register(new TianJie_ZhengChang());
        register(new TianJie_TieBi());
        register(new TianJie_SiQiJiangZhi());
        register(new TianJie_HuanMan());
        register(new TianJie_MuMeiGu());
        register(new TianJie_HongLeiGu());
        register(new TianJie_XiaoJiaZiQi());
        register(new TianJie_CaoMang());


        // 3. 浩劫
        register(new HaoJie_DingKong());
        register(new HaoJie_GuoDeQu());
        register(new HaoJie_XuKong());
        register(new HaoJie_Guan());
        register(new HaoJie_SongZhen());
        register(new HaoJie_YingShengChong());
        register(new HaoJie_GuiGuaYi());
        register(new HaoJie_DaJiaZhiQi());
        register(new HaoJie_ZhenSuo());

        // 4. 万劫
        register(new WanJie_ZhenYu());
        register(new WanJie_TongXin());
        register(new WanJie_LeiDianGu());
        register(new WanJie_TianWang());
        register(new WanJie_ChouHenGu());
        register(new WanJie_MingJia());
        register(new WanJie_DouZhuan());

        // 5. 混沌小难
        register(new HunDunXiaoNan_HeiHuo());

        // 6. 混沌大难
        register(new HunDunDaNan_HunDun());


        GuZhenRen.logger.info("灾劫系统初始化完毕！注册的灾劫总数: " + ALL_TRIBULATIONS.size());
    }

    private static void register(ITribulation tribulation) {
        ALL_TRIBULATIONS.put(tribulation.getId(), tribulation);

        String type = tribulation.getTribulationType();
        if ("ALL".equalsIgnoreCase(type)) {
            for (ArrayList<ITribulation> pool : TRIBULATION_POOLS.values()) {
                pool.add(tribulation);
            }
            return;
        }

        if (TRIBULATION_POOLS.containsKey(type)) {
            TRIBULATION_POOLS.get(type).add(tribulation);
        } else {
            GuZhenRen.logger.error("注册灾劫时出错：未知的灾劫类型 [" + type + "]");
        }
    }


    public static ITribulation drawTribulation(String type, int drawCount) {
        if (!TRIBULATION_POOLS.containsKey(type) || TRIBULATION_POOLS.get(type).isEmpty()) return null;

        ArrayList<ITribulation> pool = new ArrayList<>(TRIBULATION_POOLS.get(type));

        pool.sort(Comparator.comparing(ITribulation::getId));

        long independentSeed = Settings.seed ^ (type.hashCode() * 31415926535L);
        com.megacrit.cardcrawl.random.Random deterministicRng = new com.megacrit.cardcrawl.random.Random(independentSeed);

        Collections.shuffle(pool, new java.util.Random(deterministicRng.randomLong()));

        return pool.get(drawCount % pool.size());
    }

    public static ITribulation peekTribulation(String type, int currentDrawCount) {
        return drawTribulation(type, currentDrawCount);
    }
}