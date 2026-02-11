package GuZhenRen.relics;
import GuZhenRen.GuZhenRen;
import basemod.abstracts.CustomRelic;

public class KongQiao_1 extends AbstractKongQiao {
    public static final String ID = GuZhenRen.makeID("KongQiao_1");

    public KongQiao_1() {
        super(ID, "KongQiao_1.png", RelicTier.STARTER, LandingSound.FLAT);
        initStats(1, 1, GuZhenRen.makeID("KongQiao_2"));
    }


    @Override
    public CustomRelic makeCopy() {
        return new KongQiao_1();
    }
}