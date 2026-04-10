package GuZhenRen.relics;
import GuZhenRen.GuZhenRen;
import basemod.abstracts.CustomRelic;

public class KongQiao_2 extends AbstractKongQiao {
    public static final String ID = GuZhenRen.makeID("KongQiao_2");

    public KongQiao_2() {
        super(ID, "KongQiao_2.png", RelicTier.SPECIAL, LandingSound.CLINK);
        initStats(2, 2, GuZhenRen.makeID("KongQiao_3"));
    }

    @Override
    public CustomRelic makeCopy() {
        return new KongQiao_2();
    }
}