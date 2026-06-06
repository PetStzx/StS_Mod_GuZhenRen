package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.combat.LightningEffect;

public class SiXuRuDianGu extends CustomRelic {
    public static final String ID = GuZhenRen.makeID("SiXuRuDianGu");
    private static final String IMG = GuZhenRen.assetPath("img/relics/SiXuRuDianGu.png");
    private static final String OUTLINE = GuZhenRen.assetPath("img/relics/outline/SiXuRuDianGu.png");

    private static final int DAMAGE_AMOUNT = 3;

    public SiXuRuDianGu() {
        super(ID, ImageMaster.loadImage(IMG), new Texture(OUTLINE), RelicTier.UNCOMMON, LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    public void onGainNian(int nianAmount) {
        if (nianAmount > 0) {
            this.flash();
            AbstractPlayer p = AbstractDungeon.player;

            AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(p, this));
            AbstractDungeon.actionManager.addToBottom(new com.megacrit.cardcrawl.actions.utility.SFXAction("ORB_LIGHTNING_EVOKE", 0.1F));

            for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!mo.isDeadOrEscaped()) {
                    AbstractDungeon.actionManager.addToBottom(new VFXAction(new LightningEffect(mo.drawX, mo.drawY), 0.0F));
                }
            }

            AbstractDungeon.actionManager.addToBottom(new DamageAllEnemiesAction(
                    null,
                    DamageInfo.createDamageMatrix(DAMAGE_AMOUNT, true),
                    DamageInfo.DamageType.THORNS,
                    AbstractGameAction.AttackEffect.NONE
            ));
        }
    }

    @Override
    public AbstractRelic makeCopy() {
        return new SiXuRuDianGu();
    }
}