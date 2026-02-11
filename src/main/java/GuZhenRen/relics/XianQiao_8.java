package GuZhenRen.relics;
import GuZhenRen.GuZhenRen;
import basemod.abstracts.CustomRelic;

public class XianQiao_8 extends AbstractKongQiao {
    public static final String ID = GuZhenRen.makeID("XianQiao_8");

    public XianQiao_8() {
        // 图片文件名: XianQiao_8.png
        // 这里设定为 SPECIAL，因为它是进化出来的，不是初始可选的，也不是商店买的
        super(ID, "XianQiao_8.png", RelicTier.SPECIAL, LandingSound.CLINK);
        // 初始化：8转，需要8点经验升级，下一级是 XianQiao_9
        initStats(8, 8, GuZhenRen.makeID("XianQiao_9"));
    }


    @Override
    public CustomRelic makeCopy() {
        return new XianQiao_8();
    }
}