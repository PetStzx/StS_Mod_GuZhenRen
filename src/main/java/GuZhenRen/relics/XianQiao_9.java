package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.util.GuZhenRenConfig;
import basemod.abstracts.CustomRelic;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class XianQiao_9 extends AbstractKongQiao {
    public static final String ID = GuZhenRen.makeID("XianQiao_9");

    public XianQiao_9() {
        super(ID, "XianQiao_9.png", RelicTier.SPECIAL, LandingSound.MAGICAL);
        initStats(9, 18, GuZhenRen.makeID("XianQiao_10"));
    }

    @Override
    public void onEquip() {
        super.onEquip();
        if (!GuZhenRenConfig.bgm9) return;

        boolean hasShaGu = false;

        if (AbstractDungeon.player != null && AbstractDungeon.player.masterDeck != null) {
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                if (c.cardID.equals(GuZhenRen.makeID("ShaGu"))) {
                    hasShaGu = true;
                    break;
                }
            }
        }

        if (hasShaGu) {
            CardCrawlGame.sound.play(GuZhenRen.makeID("YouHunMoZun"));
        } else {
            CardCrawlGame.sound.play(GuZhenRen.makeID("LianTianMoZun"));
        }
    }

    @Override
    public CustomRelic makeCopy() {
        return new XianQiao_9();
    }
}