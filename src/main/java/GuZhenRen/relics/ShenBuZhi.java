package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import basemod.abstracts.CustomRelic;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.helpers.ImageMaster;

public class ShenBuZhi extends CustomRelic implements CustomSavable<Integer> {
    public static final String ID = GuZhenRen.makeID("ShenBuZhi");
    private static final String IMG = "ShenBuZhi.png";
    private static final String OUTLINE = "ShenBuZhi.png";

    public ShenBuZhi() {
        super(ID,
                ImageMaster.loadImage(GuZhenRen.assetPath("img/relics/" + IMG)),
                new Texture(GuZhenRen.assetPath("img/relics/outline/" + OUTLINE)),
                RelicTier.SPECIAL,
                LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public void onEquip() {
        super.onEquip();
        AbstractKongQiao kq = AbstractKongQiao.getInstance();
        if (kq != null) {
            kq.updatePulseStatus();
            kq.updateDescription();
        }
    }

    @Override
    public void onUnequip() {
        super.onUnequip();
        com.badlogic.gdx.Gdx.app.postRunnable(() -> {
            AbstractKongQiao kq = AbstractKongQiao.getInstance();
            if (kq != null) {
                kq.updatePulseStatus();
                kq.updateDescription();
            }
        });
    }


    @Override
    public Integer onSave() {
        return 1;
    }

    @Override
    public void onLoad(Integer savedData) {
        AbstractKongQiao kq = AbstractKongQiao.getInstance();
        if (kq != null) {
            kq.updatePulseStatus();
            kq.updateDescription();
        }
    }
}