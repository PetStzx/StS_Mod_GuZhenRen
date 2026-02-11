package GuZhenRen.relics;
import GuZhenRen.GuZhenRen;
import basemod.abstracts.CustomRelic;

public class XianQiao_9 extends AbstractKongQiao {
    public static final String ID = GuZhenRen.makeID("XianQiao_9");

    public XianQiao_9() {
        // 图片文件名: XianQiao_9.png
        // 这里设定为 SPECIAL，因为它是进化出来的，不是初始可选的，也不是商店买的
        super(ID, "XianQiao_9.png", RelicTier.SPECIAL, LandingSound.MAGICAL);
        initStats(9, 999, null);
    }


    @Override
    public CustomRelic makeCopy() {
        return new XianQiao_9();
    }
}