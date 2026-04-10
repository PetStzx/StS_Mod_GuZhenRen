package GuZhenRen.relics;
import GuZhenRen.GuZhenRen;
import basemod.abstracts.CustomRelic;

public class XianQiao_7 extends AbstractKongQiao {
    public static final String ID = GuZhenRen.makeID("XianQiao_7");

    public XianQiao_7() {
        super(ID, "XianQiao_7.png", RelicTier.SPECIAL, LandingSound.CLINK);
        initStats(7, 7, GuZhenRen.makeID("XianQiao_8"));
    }


    @Override
    public CustomRelic makeCopy() {
        return new XianQiao_7();
    }
}