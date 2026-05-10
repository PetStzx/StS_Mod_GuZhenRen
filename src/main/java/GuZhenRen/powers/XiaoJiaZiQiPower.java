package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect;

import java.util.HashSet;
import java.util.Set;

public class XiaoJiaZiQiPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("XiaoJiaZiQiPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private Set<String> lastDaoTags = new HashSet<>();

    public XiaoJiaZiQiPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = 0;

        this.type = PowerType.BUFF;
        this.isTurnBased = false;

        String pathLarge = GuZhenRen.assetPath("img/powers/XiaoJiaZiQiPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/XiaoJiaZiQiPower.png");
        Texture texLarge = ImageMaster.loadImage(pathLarge);
        Texture texSmall = ImageMaster.loadImage(pathSmall);
        this.region128 = new TextureAtlas.AtlasRegion(texLarge, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(texSmall, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    @Override
    public void stackPower(int stackAmount) {
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        if (this.owner == null || this.owner.isDeadOrEscaped() || this.owner.halfDead) {
            return;
        }

        Set<String> currentDaoTags = new HashSet<>();

        for (AbstractCard.CardTags tag : card.tags) {
            if (tag.name().endsWith("_DAO")) {
                if (RuiYiPower.isActive) {
                    currentDaoTags.add("JIAN_DAO");
                } else {
                    currentDaoTags.add(tag.name());
                }
            }
        }

        if (!currentDaoTags.isEmpty() && !this.lastDaoTags.isEmpty()) {
            boolean hasMatch = false;
            for (String dao : currentDaoTags) {
                if (this.lastDaoTags.contains(dao)) {
                    hasMatch = true;
                    break;
                }
            }

            if (hasMatch) {
                this.flash();
                this.amount += 5;
                this.updateDescription();
            }
        }

        this.lastDaoTags.clear();
        this.lastDaoTags.addAll(currentDaoTags);
    }

    @Override
    public void atStartOfTurn() {
        if (this.owner == null || this.owner.isDeadOrEscaped() || this.owner.halfDead) {
            return;
        }

        if (this.amount > 0) {
            this.flash();

            Color white = new Color(1.0F, 1.0F, 1.0F, 1.0F);

            AbstractDungeon.actionManager.addToBottom(new VFXAction(new BorderFlashEffect(white, true)));
            AbstractDungeon.actionManager.addToBottom(new VFXAction(new ShockWaveEffect(this.owner.hb.cX, this.owner.hb.cY, white, ShockWaveEffect.ShockWaveType.CHAOTIC), 0.05F));

            AbstractDungeon.actionManager.addToBottom(new DamageAction(
                    AbstractDungeon.player,
                    new DamageInfo(this.owner, this.amount, DamageInfo.DamageType.THORNS),
                    AbstractGameAction.AttackEffect.NONE
            ));

            AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
                @Override
                public void update() {
                    XiaoJiaZiQiPower.this.amount = 0;
                    XiaoJiaZiQiPower.this.updateDescription();
                    this.isDone = true;
                }
            });
        }

        this.lastDaoTags.clear();
    }
}