package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import basemod.abstracts.CustomRelic;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class XianQiao_9 extends AbstractKongQiao {
    public static final String ID = GuZhenRen.makeID("XianQiao_9");

    public XianQiao_9() {
        super(ID, "XianQiao_9.png", RelicTier.SPECIAL, LandingSound.MAGICAL);
        initStats(9, 18, GuZhenRen.makeID("XianQiao_10"));
    }

    @Override
    public void onEquip() {
        super.onEquip();
        CardCrawlGame.sound.play(GuZhenRen.makeID("LianTianMoZun"));
    }

    @Override
    public CustomRelic makeCopy() {
        return new XianQiao_9();
    }
}