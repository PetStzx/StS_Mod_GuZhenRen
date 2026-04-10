package GuZhenRen.relics;
import GuZhenRen.GuZhenRen;
import basemod.abstracts.CustomRelic;

public class KongQiao_4 extends AbstractKongQiao {
    public static final String ID = GuZhenRen.makeID("KongQiao_4");

    public KongQiao_4() {
        super(ID, "KongQiao_4.png", RelicTier.SPECIAL, LandingSound.CLINK);
        initStats(4, 4, GuZhenRen.makeID("KongQiao_5"));
    }


    @Override
    public CustomRelic makeCopy() {
        return new KongQiao_4();
    }
}