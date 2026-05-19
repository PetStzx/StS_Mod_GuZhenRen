package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.util.IProbabilityModifier;
import basemod.abstracts.CustomRelic;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class HongYunQiTianGu extends CustomRelic implements IProbabilityModifier, CustomSavable<Integer> {
    public static final String ID = GuZhenRen.makeID("HongYunQiTianGu");
    private static final String IMG = GuZhenRen.assetPath("img/relics/HongYunQiTianGu.png");
    private static final String OUTLINE = GuZhenRen.assetPath("img/relics/outline/HongYunQiTianGu.png");

    public HongYunQiTianGu() {
        super(ID, ImageMaster.loadImage(IMG), new Texture(OUTLINE), RelicTier.RARE, LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public float getAdditiveProbability(AbstractCard card) {
        return 0.40f;
    }

    @Override
    public int changeRareCardRewardChance(int rareCardChance) {
        return rareCardChance * 2;
    }

    @Override
    public void atBattleStart() {
        this.flash();
    }

    @Override
    public void onEquip() {
        super.onEquip();
        if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(ChunQiuChan.ID)) {
            ChunQiuChan cqc = (ChunQiuChan) AbstractDungeon.player.getRelic(ChunQiuChan.ID);
            cqc.updateDescription();
        }
    }

    @Override
    public void onUnequip() {
        super.onUnequip();
        com.badlogic.gdx.Gdx.app.postRunnable(() -> {
            if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(ChunQiuChan.ID)) {
                ChunQiuChan cqc = (ChunQiuChan) AbstractDungeon.player.getRelic(ChunQiuChan.ID);
                cqc.updateDescription();
            }
        });
    }


    @Override
    public Integer onSave() {
        return 1;
    }

    @Override
    public void onLoad(Integer savedData) {
        if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(ChunQiuChan.ID)) {
            ChunQiuChan cqc = (ChunQiuChan) AbstractDungeon.player.getRelic(ChunQiuChan.ID);
            cqc.updateDescription();
        }
    }

    @Override
    public AbstractRelic makeCopy() {
        return new HongYunQiTianGu();
    }
}