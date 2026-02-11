package GuZhenRen.relics;
import GuZhenRen.GuZhenRen;
import basemod.abstracts.CustomRelic;

public class KongQiao_5 extends AbstractKongQiao {
    public static final String ID = GuZhenRen.makeID("KongQiao_5");

    public KongQiao_5() {
        // 图片文件名: KongQiao_5.png
        // 这里设定为 SPECIAL，因为它是进化出来的，不是初始可选的，也不是商店买的
        super(ID, "KongQiao_5.png", RelicTier.SPECIAL, LandingSound.CLINK);
        // 初始化：2转，需要2点经验升级，下一级是 KongQiao_3
        initStats(5, 5, GuZhenRen.makeID("XianQiao_6"));
    }


    @Override
    public CustomRelic makeCopy() {
        return new KongQiao_5();
    }
}