package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import basemod.abstracts.CustomRelic;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class GouShiYun extends CustomRelic implements CustomSavable<Integer> {
    public static final String ID = GuZhenRen.makeID("GouShiYun");
    private static final String IMG = GuZhenRen.assetPath("img/relics/GouShiYun.png");
    private static final String OUTLINE = GuZhenRen.assetPath("img/relics/outline/GouShiYun.png");

    public GouShiYun() {
        super(ID, ImageMaster.loadImage(IMG), new Texture(OUTLINE), RelicTier.UNCOMMON, LandingSound.MAGICAL);
        updateDescription();
    }

    @Override
    public String getUpdatedDescription() {
        int chance = 25;
        boolean hasHongYun = false;

        if (AbstractDungeon.player != null) {
            if (AbstractDungeon.player.hasRelic(HongYunQiTianGu.ID)) {
                chance += 15;
                hasHongYun = true;
            }
        }

        StringBuilder desc = new StringBuilder(String.format(DESCRIPTIONS[0], chance));
        if (hasHongYun) {
            desc.append(DESCRIPTIONS[1]);
        }

        return desc.toString();
    }

    public void updateDescription() {
        this.description = getUpdatedDescription();
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        initializeTips();
    }

    @Override
    public void onEquip() {
        super.onEquip();
        updateDescription();
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
    public void onVictory() {
        if (AbstractDungeon.getCurrRoom().rewardAllowed) {
            int chance = 25;
            if (AbstractDungeon.player.hasRelic(HongYunQiTianGu.ID)) {
                chance += 15;
            }

            if (AbstractDungeon.treasureRng.random(0, 99) < chance) {
                this.flash();
                this.addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, this));
                AbstractDungeon.getCurrRoom().addRelicToRewards(AbstractDungeon.returnRandomRelicTier());
            }
        }
    }

    @Override
    public Integer onSave() {
        return 1;
    }

    @Override
    public void onLoad(Integer savedData) {
        this.updateDescription();

        if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(ChunQiuChan.ID)) {
            ChunQiuChan cqc = (ChunQiuChan) AbstractDungeon.player.getRelic(ChunQiuChan.ID);
            cqc.updateDescription();
        }
    }

    @Override
    public AbstractRelic makeCopy() {
        return new GouShiYun();
    }
}