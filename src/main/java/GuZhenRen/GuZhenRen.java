package GuZhenRen;

import GuZhenRen.effects.BenMingGuOpeningEffect;
import basemod.BaseMod;
import basemod.interfaces.*;
import GuZhenRen.character.FangYuan;
import GuZhenRen.potions.*;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import GuZhenRen.cards.*;
import GuZhenRen.relics.*;
import GuZhenRen.patches.*;
import GuZhenRen.powers.*;
import GuZhenRen.variables.SecondMagicNumber;
import GuZhenRen.variables.FenShaoVariable;
import GuZhenRen.variables.NianVariable;
import com.megacrit.cardcrawl.localization.Keyword;
import com.google.gson.Gson;
import com.badlogic.gdx.Gdx;
import java.nio.charset.StandardCharsets;

import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import java.util.ArrayList;

@SpireInitializer
public class GuZhenRen implements
        EditCardsSubscriber,
        EditStringsSubscriber,
        EditRelicsSubscriber,
        EditCharactersSubscriber,
        EditKeywordsSubscriber,
        PostInitializeSubscriber,
        PostDungeonInitializeSubscriber,
        OnStartBattleSubscriber,
        PostEnergyRechargeSubscriber,
        PostBattleSubscriber
{
    public static final Logger logger = LogManager.getLogger(GuZhenRen.class.getName());
    public static final String MOD_ID = "GuZhenRen";

    // 用于存储所有配方遗物ID的列表
    public static ArrayList<String> recipeRelicIDs = new ArrayList<>();

    public static String makeID(String id) {
        return MOD_ID + ":" + id;
    }

    public static String assetPath(String path) {
        return MOD_ID + "/" + path;
    }

    public static final Color GUZHENREN_COLOR = new Color(0.275F, 0.275F, 0.275F, 1.0F);

    public GuZhenRen() {
        logger.info("========================= 开始注册订阅 =========================");
        BaseMod.subscribe(this);

        BaseMod.addColor(CardColorEnum.GUZHENREN_GREY,
                GUZHENREN_COLOR, GUZHENREN_COLOR, GUZHENREN_COLOR,
                GUZHENREN_COLOR, GUZHENREN_COLOR, GUZHENREN_COLOR, GUZHENREN_COLOR,
                assetPath("img/cardui/512/bg_attack_grey.png"),
                assetPath("img/cardui/512/bg_skill_grey.png"),
                assetPath("img/cardui/512/bg_power_grey.png"),
                assetPath("img/cardui/512/card_grey_orb.png"),
                assetPath("img/cardui/1024/bg_attack_grey.png"),
                assetPath("img/cardui/1024/bg_skill_grey.png"),
                assetPath("img/cardui/1024/bg_power_grey.png"),
                assetPath("img/cardui/1024/card_grey_orb.png"),
                assetPath("img/cardui/512/card_grey_small_orb.png")
        );
        BaseMod.addDynamicVariable(new SecondMagicNumber());
        BaseMod.addDynamicVariable(new FenShaoVariable());
        BaseMod.addDynamicVariable(new NianVariable());
        logger.info("========================= 订阅完成 =========================");
    }

    public static void initialize() {
        new GuZhenRen();
    }

    @Override
    public void receivePostDungeonInitialize() {
        if (AbstractDungeon.player instanceof FangYuan) {
            if (AbstractDungeon.floorNum <= 1 && !CardCrawlGame.loadingSave) {
                AbstractDungeon.topLevelEffects.add(new BenMingGuOpeningEffect());
            }
        }
    }

    @Override
    public void receiveEditCharacters() {
        BaseMod.addCharacter(
                new FangYuan("FangYuan"),
                assetPath("img/character/FangYuan/Button.png"),
                assetPath("img/character/FangYuan/Portrait.png"),
                AbstractPlayerEnum.FANG_YUAN
        );
    }

    @Override
    public void receiveEditCards() {
        logger.info("开始加载卡牌...");
        BaseMod.addCard(new YueGuangGu());
        BaseMod.addCard(new YuPiGu());
        BaseMod.addCard(new XiaoGuangGu());
        BaseMod.addCard(new JuChiJinWu());
        BaseMod.addCard(new TouXiGu());
        BaseMod.addCard(new ZiLiGengShengGu());
        BaseMod.addCard(new JiuYeShengJiCao());
        BaseMod.addCard(new ChiLi());
        BaseMod.addCard(new ChengGongGu());
        BaseMod.addCard(new ShiBaiGu());
        BaseMod.addCard(new QuanLiYiFuGu());
        BaseMod.addCard(new ShaGu());
        BaseMod.addCard(new HuoGu());
        BaseMod.addCard(new HuoMaoSanZhangGu());
        BaseMod.addCard(new WuZuNiao());
        BaseMod.addCard(new RongYanZhaLieGu());
        BaseMod.addCard(new LiLiangGu());
        BaseMod.addCard(new YanTongGu());
        BaseMod.addCard(new LiaoYuanHuo());
        BaseMod.addCard(new XingHuoLiaoYuanGu());
        BaseMod.addCard(new AngryBird());
        BaseMod.addCard(new KuLiGu());
        BaseMod.addCard(new ZhanNianGu());
        BaseMod.addCard(new WanWuDaTongBian());
        BaseMod.addCard(new JinGangNian());
        BaseMod.addCard(new HuiGu());
        BaseMod.addCard(new XingNianGu());
        BaseMod.addCard(new ShaYiGu());
        BaseMod.addCard(new YiNianGu());
        BaseMod.addCard(new YiXinErYongGu());
        BaseMod.addCard(new ZhiZhang());
        BaseMod.addCard(new ZhiHuiGu());
        BaseMod.addCard(new ZiYiGu());
        BaseMod.addCard(new WanXingFeiYing());
        BaseMod.addCard(new TunHuoGu());
        BaseMod.addCard(new YanZhouGu());
        BaseMod.addCard(new YangMangBeiHuoYi());
        BaseMod.addCard(new ZhuiMingHuo());
        BaseMod.addCard(new LeShanLeShuiGu());
        BaseMod.addCard(new ZhuiNian());
        BaseMod.addCard(new XingXiuQiPan());
        BaseMod.addCard(new SheXinGu());
        BaseMod.addCard(new WuJinXuanGuangQi());
        BaseMod.addCard(new WuZhiQuanXinJian());
        BaseMod.addCard(new JianYing());
        BaseMod.addCard(new ShanJianGu());
        BaseMod.addCard(new HuiJian());
        BaseMod.addCard(new JianHenSuoMing());
        BaseMod.addCard(new JianDun());
        BaseMod.addCard(new JianQiGu());
        BaseMod.addCard(new JianQiaoGu());
        BaseMod.addCard(new DieYingGu());
        BaseMod.addCard(new FeiJian());
        BaseMod.addCard(new LangJian());
        BaseMod.addCard(new JianYingGu());
        BaseMod.addCard(new DuoChongJianYingGu());
        BaseMod.addCard(new RuiYiGu());
        BaseMod.addCard(new JianLangSanDie());
        BaseMod.addCard(new AnQiSha());
        BaseMod.addCard(new RenGu());
        BaseMod.addCard(new QingNiuLaoLiGu());
        BaseMod.addCard(new NiuLiXuYing());
        BaseMod.addCard(new ChiMaJunLiGu());
        BaseMod.addCard(new MaLiXuYing());
        BaseMod.addCard(new HeiMangChanLiGu());
        BaseMod.addCard(new HeiMangXuYing());
        BaseMod.addCard(new BaiXiangYuanLiGu());
        BaseMod.addCard(new BaiXiangXuYing());
        BaseMod.addCard(new ShiGuiFuLiGu());
        BaseMod.addCard(new GuiLiXuYing());
        BaseMod.addCard(new FeiXiongZhiLiGu());
        BaseMod.addCard(new FeiXiongXuYing());
        BaseMod.addCard(new WoLi());
        BaseMod.addCard(new WoLiXuYing());
        BaseMod.addCard(new FeiLiGu());
        BaseMod.addCard(new YunSuan());
        BaseMod.addCard(new BaShan());
        BaseMod.addCard(new LiQiGu());
        BaseMod.addCard(new QunLiGu());
        BaseMod.addCard(new DingLi());
        BaseMod.addCard(new WanWoDaShouYin());
        BaseMod.addCard(new WanWo());
        BaseMod.addCard(new ShangFangJieWa());
        BaseMod.addCard(new WanLan());
        BaseMod.addCard(new HengChongZhiZhuangGu());
        BaseMod.addCard(new LongXingHuBuGu());
        BaseMod.addCard(new ZhuanYiGu());
        BaseMod.addCard(new XueChou());
        BaseMod.addCard(new XinXue());
        BaseMod.addCard(new DaoChiXueFu());
        BaseMod.addCard(new XueShenZi());
        BaseMod.addCard(new XueKuangGu());
        BaseMod.addCard(new XueMuTianHuaGu());
        BaseMod.addCard(new XueHeMang());
        BaseMod.addCard(new XueRenGu());
        BaseMod.addCard(new XueZouGu());
        BaseMod.addCard(new XueQiGu());
        BaseMod.addCard(new ZhiXueGu());
        BaseMod.addCard(new XueZhanGu());
        BaseMod.addCard(new XueYueGu());
        BaseMod.addCard(new XueYuan());
        BaseMod.addCard(new XueShouYinGu());
        BaseMod.addCard(new LengXue());
        BaseMod.addCard(new XueJianLeng());
        BaseMod.addCard(new XuePiaoLiu());
        BaseMod.addCard(new ZhuMoBang());
        BaseMod.addCard(new TaiGuangGu());
        BaseMod.addCard(new GuangGu());
        BaseMod.addCard(new TianPuGuangHe());
        BaseMod.addCard(new SanShiSanTianGuang());
        BaseMod.addCard(new BianTong());
        BaseMod.addCard(new TouSheng());
        BaseMod.addCard(new FangWeiGu());
        BaseMod.addCard(new ZhuanYun());
        BaseMod.addCard(new SongYouFeng());
        BaseMod.addCard(new SongYouFengSongBie());
        BaseMod.addCard(new GuangYinFeiRen());
        BaseMod.addCard(new BaMianWeiFengGu());
        BaseMod.addCard(new ShiZhen());
        BaseMod.addCard(new RenRuGu());
    }

    @Override
    public void receiveEditRelics() {
        logger.info("开始加载遗物...");

        BaseMod.addRelicToCustomPool(new KongQiao_1(), CardColorEnum.GUZHENREN_GREY);
        BaseMod.addRelicToCustomPool(new KongQiao_2(), CardColorEnum.GUZHENREN_GREY);
        BaseMod.addRelicToCustomPool(new KongQiao_3(), CardColorEnum.GUZHENREN_GREY);
        BaseMod.addRelicToCustomPool(new KongQiao_4(), CardColorEnum.GUZHENREN_GREY);
        BaseMod.addRelicToCustomPool(new KongQiao_5(), CardColorEnum.GUZHENREN_GREY);
        BaseMod.addRelicToCustomPool(new XianQiao_6(), CardColorEnum.GUZHENREN_GREY);
        BaseMod.addRelicToCustomPool(new XianQiao_7(), CardColorEnum.GUZHENREN_GREY);
        BaseMod.addRelicToCustomPool(new XianQiao_8(), CardColorEnum.GUZHENREN_GREY);
        BaseMod.addRelicToCustomPool(new XianQiao_9(), CardColorEnum.GUZHENREN_GREY);

        BaseMod.addRelicToCustomPool(new LiDaoDaoHen(), CardColorEnum.GUZHENREN_GREY);
        BaseMod.addRelicToCustomPool(new XianGuCanHai(), CardColorEnum.GUZHENREN_GREY);
        BaseMod.addRelicToCustomPool(new YanXinGu(), CardColorEnum.GUZHENREN_GREY);
        BaseMod.addRelicToCustomPool(new DingXianYou(), CardColorEnum.GUZHENREN_GREY);
        BaseMod.addRelicToCustomPool(new JianMei(), CardColorEnum.GUZHENREN_GREY);

        BaseMod.addRelicToCustomPool(new Recipe_AngryBird(), CardColorEnum.GUZHENREN_GREY);
        recipeRelicIDs.add(Recipe_AngryBird.ID);
        BaseMod.addRelicToCustomPool(new Recipe_WanXingFeiYing(), CardColorEnum.GUZHENREN_GREY);
        recipeRelicIDs.add(Recipe_WanXingFeiYing.ID);
        BaseMod.addRelicToCustomPool(new Recipe_YangMangBeiHuoYi(), CardColorEnum.GUZHENREN_GREY);
        recipeRelicIDs.add(Recipe_YangMangBeiHuoYi.ID);
        BaseMod.addRelicToCustomPool(new Recipe_ZhuiMingHuo(), CardColorEnum.GUZHENREN_GREY);
        recipeRelicIDs.add(Recipe_ZhuiMingHuo.ID);
        BaseMod.addRelicToCustomPool(new Recipe_XingXiuQiPan(), CardColorEnum.GUZHENREN_GREY);
        recipeRelicIDs.add(Recipe_XingXiuQiPan.ID);
        BaseMod.addRelicToCustomPool(new Recipe_WuZhiQuanXinJian(), CardColorEnum.GUZHENREN_GREY);
        recipeRelicIDs.add(Recipe_WuZhiQuanXinJian.ID);
        BaseMod.addRelicToCustomPool(new Recipe_JianLangSanDie(), CardColorEnum.GUZHENREN_GREY);
        recipeRelicIDs.add(Recipe_JianLangSanDie.ID);
        BaseMod.addRelicToCustomPool(new Recipe_AnQiSha(), CardColorEnum.GUZHENREN_GREY);
        recipeRelicIDs.add(Recipe_AnQiSha.ID);
        BaseMod.addRelicToCustomPool(new Recipe_JianHenSuoMing(), CardColorEnum.GUZHENREN_GREY);
        recipeRelicIDs.add(Recipe_JianHenSuoMing.ID);
        BaseMod.addRelicToCustomPool(new Recipe_WanWo(), CardColorEnum.GUZHENREN_GREY);
        recipeRelicIDs.add(Recipe_WanWo.ID);
        BaseMod.addRelicToCustomPool(new Recipe_WanWoDaShouYin(), CardColorEnum.GUZHENREN_GREY);
        recipeRelicIDs.add(Recipe_WanWoDaShouYin.ID);
        BaseMod.addRelicToCustomPool(new Recipe_ShangFangJieWa(), CardColorEnum.GUZHENREN_GREY);
        recipeRelicIDs.add(Recipe_ShangFangJieWa.ID);
        BaseMod.addRelicToCustomPool(new Recipe_XueJianLeng(), CardColorEnum.GUZHENREN_GREY);
        recipeRelicIDs.add(Recipe_XueJianLeng.ID);
        BaseMod.addRelicToCustomPool(new Recipe_XuePiaoLiu(), CardColorEnum.GUZHENREN_GREY);
        recipeRelicIDs.add(Recipe_XuePiaoLiu.ID);
        BaseMod.addRelicToCustomPool(new Recipe_ZhuMoBang(), CardColorEnum.GUZHENREN_GREY);
        recipeRelicIDs.add(Recipe_ZhuMoBang.ID);
        BaseMod.addRelicToCustomPool(new Recipe_SanShiSanTianGuang(), CardColorEnum.GUZHENREN_GREY);
        recipeRelicIDs.add(Recipe_SanShiSanTianGuang.ID);
        BaseMod.addRelicToCustomPool(new Recipe_TianPuGuangHe(), CardColorEnum.GUZHENREN_GREY);
        recipeRelicIDs.add(Recipe_TianPuGuangHe.ID);
    }

    @Override
    public void receivePostInitialize() {
        BaseMod.addPotion(
                ShengJiYe.class,
                Color.GREEN.cpy(),
                null,
                null,
                ShengJiYe.POTION_ID,
                AbstractPlayerEnum.FANG_YUAN
        );
    }

    @Override
    public void receiveEditStrings() {
        String language = "eng";
        if (Settings.language == Settings.GameLanguage.ZHS) {
            language = "zhs";
        }

        BaseMod.loadCustomStringsFile(CardStrings.class, assetPath("localization/" + language + "/CardStrings.json"));
        BaseMod.loadCustomStringsFile(CharacterStrings.class, assetPath("localization/" + language + "/CharacterStrings.json"));
        BaseMod.loadCustomStringsFile(RelicStrings.class, assetPath("localization/" + language + "/RelicStrings.json"));
        BaseMod.loadCustomStringsFile(PowerStrings.class, assetPath("localization/" + language + "/PowerStrings.json"));
        BaseMod.loadCustomStringsFile(PotionStrings.class, assetPath("localization/" + language + "/PotionStrings.json"));
        BaseMod.loadCustomStringsFile(UIStrings.class, assetPath("localization/" + language + "/UIStrings.json"));
    }

    @Override
    public void receiveEditKeywords() {
        Gson gson = new Gson();
        String language = "eng";
        if (Settings.language == Settings.GameLanguage.ZHS) {
            language = "zhs";
        }

        String json = Gdx.files.internal(assetPath("localization/" + language + "/KeywordStrings.json"))
                .readString(String.valueOf(StandardCharsets.UTF_8));

        Keyword[] keywords = gson.fromJson(json, Keyword[].class);
        if (keywords != null) {
            for (Keyword keyword : keywords) {
                BaseMod.addKeyword("guzhenren", keyword.NAMES[0], keyword.NAMES, keyword.DESCRIPTION);
            }
        }
    }

    // =================================================================================
    // “人如故” 回溯血量机制 - 账本管理
    // =================================================================================
    @Override
    public void receiveOnBattleStart(AbstractRoom room) {
        RenRuGu.hpHistory.clear();
        RenRuGu.hpHistory.add(com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.currentHealth);
    }

    @Override
    public void receivePostEnergyRecharge() {
        // 玩家每回合开始（能量恢复完成）时，记录当下的生命值
        while (RenRuGu.hpHistory.size() < com.megacrit.cardcrawl.dungeons.AbstractDungeon.actionManager.turn) {
            RenRuGu.hpHistory.add(com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.currentHealth);
        }
    }

    @Override
    public void receivePostBattle(AbstractRoom room) {
        //1. 静态变量清理区 (防止下一场战斗数据残留)
        SanShiSanTianGuang.totalShanYaoGainedThisCombat = 0;
        TouDaoDaoHenPower.totalGoldStolenThisCombat = 0;

        // 清空时间轴，防止内存泄漏或数据污染下一场战斗
        RenRuGu.hpHistory.clear();

        //2. 杀招合成遗物掉落逻辑
        if (AbstractDungeon.relicRng.randomBoolean(0.15f)) {
            if (!recipeRelicIDs.isEmpty()) {
                String randomID = recipeRelicIDs.get(AbstractDungeon.relicRng.random(recipeRelicIDs.size() - 1));
                boolean hasRelic = AbstractDungeon.player.hasRelic(randomID);

                if (!hasRelic) {
                    AbstractRelic relic = com.megacrit.cardcrawl.helpers.RelicLibrary.getRelic(randomID).makeCopy();
                    room.rewards.add(new RewardItem(relic));
                }
            }
        }
    }
}