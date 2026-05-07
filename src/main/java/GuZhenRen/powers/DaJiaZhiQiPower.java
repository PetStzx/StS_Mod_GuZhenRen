package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractGuZhenRenCard;
import GuZhenRen.patches.GuZhenRenTags;
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

public class DaJiaZhiQiPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("DaJiaZhiQiPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private Set<String> playedDaos = new HashSet<>();

    public DaJiaZhiQiPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = 0;

        this.type = PowerType.BUFF;
        this.isTurnBased = false;

        String pathLarge = GuZhenRen.assetPath("img/powers/DaJiaZhiQiPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/DaJiaZhiQiPower.png");
        Texture texLarge = ImageMaster.loadImage(pathLarge);
        Texture texSmall = ImageMaster.loadImage(pathSmall);
        this.region128 = new TextureAtlas.AtlasRegion(texLarge, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(texSmall, 0, 0, 32, 32);

        updateDescription();
    }

    private String getLocalizedDaoName(String tagString) {
        try {
            AbstractGuZhenRenCard.Dao daoEnum = AbstractGuZhenRenCard.Dao.valueOf(tagString);
            int textIndex = 10 + daoEnum.ordinal();

            if (textIndex < AbstractGuZhenRenCard.TEXT.length) {
                return AbstractGuZhenRenCard.TEXT[textIndex];
            }
        } catch (IllegalArgumentException e) {
        }
        return tagString.replace("_DAO", "道");
    }

    @Override
    public void updateDescription() {
        if (this.playedDaos.isEmpty()) {
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(DESCRIPTIONS[0]).append(this.amount).append(DESCRIPTIONS[1]);
            sb.append(DESCRIPTIONS[2]);

            boolean isFirst = true;
            for (String daoTag : this.playedDaos) {
                if (!isFirst) {
                    sb.append("、");
                }
                // 动态获取本地化名称并标黄
                sb.append(" #y").append(getLocalizedDaoName(daoTag)).append(" ");
                isFirst = false;
            }
            sb.append("。");
            this.description = sb.toString();
        }
    }

    @Override
    public void stackPower(int stackAmount) {
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        boolean isNewDaoPlayed = false;

        for (AbstractCard.CardTags tag : card.tags) {
            if (AbstractGuZhenRenCard.isDaoTag(tag)) {
                String currentTag = tag.name();

                if (RuiYiPower.isActive) {
                    currentTag = GuZhenRenTags.JIAN_DAO.name();
                }

                if (!this.playedDaos.contains(currentTag)) {
                    this.playedDaos.add(currentTag);
                    isNewDaoPlayed = true;
                }
            }
        }

        if (isNewDaoPlayed) {
            this.flash();
            this.amount += 5;
            this.updateDescription();
        }
    }

    @Override
    public void atStartOfTurn() {
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
                    DaJiaZhiQiPower.this.amount = 0;
                    DaJiaZhiQiPower.this.playedDaos.clear();
                    DaJiaZhiQiPower.this.updateDescription();
                    this.isDone = true;
                }
            });
        } else {
            this.playedDaos.clear();
            this.updateDescription();
        }
    }
}