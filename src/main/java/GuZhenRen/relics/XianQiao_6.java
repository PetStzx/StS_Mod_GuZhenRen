package GuZhenRen.relics;
import GuZhenRen.GuZhenRen;
import basemod.abstracts.CustomRelic;

public class XianQiao_6 extends AbstractKongQiao {
    public static final String ID = GuZhenRen.makeID("XianQiao_6");

    public XianQiao_6() {
        // 图片文件名: XianQiao_6.png
        // 这里设定为 SPECIAL，因为它是进化出来的，不是初始可选的，也不是商店买的
        super(ID, "XianQiao_6.png", RelicTier.SPECIAL, LandingSound.CLINK);
        // 初始化：2转，需要2点经验升级，下一级是 KongQiao_3
        initStats(6, 6, GuZhenRen.makeID("XianQiao_7"));
    }


    @Override
    public CustomRelic makeCopy() {
        return new XianQiao_6();
    }
}