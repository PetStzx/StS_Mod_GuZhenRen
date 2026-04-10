package GuZhenRen.relics;
import GuZhenRen.GuZhenRen;
import basemod.abstracts.CustomRelic;

public class KongQiao_5 extends AbstractKongQiao {
    public static final String ID = GuZhenRen.makeID("KongQiao_5");

    public KongQiao_5() {
        super(ID, "KongQiao_5.png", RelicTier.SPECIAL, LandingSound.CLINK);
        initStats(5, 5, GuZhenRen.makeID("XianQiao_6"));
    }


    @Override
    public CustomRelic makeCopy() {
        return new KongQiao_5();
    }
}