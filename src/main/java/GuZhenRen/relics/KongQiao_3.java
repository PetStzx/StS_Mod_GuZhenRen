package GuZhenRen.relics;
import GuZhenRen.GuZhenRen;
import basemod.abstracts.CustomRelic;

public class KongQiao_3 extends AbstractKongQiao {
    public static final String ID = GuZhenRen.makeID("KongQiao_3");

    public KongQiao_3() {
        super(ID, "KongQiao_3.png", RelicTier.SPECIAL, LandingSound.CLINK);
        initStats(3, 3, GuZhenRen.makeID("KongQiao_4"));
    }


    @Override
    public CustomRelic makeCopy() {
        return new KongQiao_3();
    }
}