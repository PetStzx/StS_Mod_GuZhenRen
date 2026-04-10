package GuZhenRen.relics;
import GuZhenRen.GuZhenRen;
import basemod.abstracts.CustomRelic;

public class XianQiao_8 extends AbstractKongQiao {
    public static final String ID = GuZhenRen.makeID("XianQiao_8");

    public XianQiao_8() {
        super(ID, "XianQiao_8.png", RelicTier.SPECIAL, LandingSound.CLINK);
        initStats(8, 8, GuZhenRen.makeID("XianQiao_9"));
    }


    @Override
    public CustomRelic makeCopy() {
        return new XianQiao_8();
    }
}