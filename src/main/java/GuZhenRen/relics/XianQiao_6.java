package GuZhenRen.relics;
import GuZhenRen.GuZhenRen;
import basemod.abstracts.CustomRelic;

public class XianQiao_6 extends AbstractKongQiao {
    public static final String ID = GuZhenRen.makeID("XianQiao_6");

    public XianQiao_6() {
        super(ID, "XianQiao_6.png", RelicTier.SPECIAL, LandingSound.CLINK);
        initStats(6, 6, GuZhenRen.makeID("XianQiao_7"));
    }


    @Override
    public CustomRelic makeCopy() {
        return new XianQiao_6();
    }
}