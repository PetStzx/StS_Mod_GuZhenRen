package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;

public class ShuiWenGu extends CustomRelic {
    public static final String ID = GuZhenRen.makeID("ShuiWenGu");
    private static final String IMG = "ShuiWenGu.png";
    private static final String OUTLINE = "ShuiWenGu.png";

    private static final int TARGET_WORDS = 300;
    private static final int GOLD_AMOUNT = 250;

    public ShuiWenGu() {
        super(ID,
                ImageMaster.loadImage(GuZhenRen.assetPath("img/relics/" + IMG)),
                new Texture(GuZhenRen.assetPath("img/relics/outline/" + OUTLINE)),
                RelicTier.COMMON,
                LandingSound.CLINK);

        this.counter = -1;
    }

    @Override
    public String getUpdatedDescription() {
        if (this.counter <= -2) {
            return DESCRIPTIONS[1];
        }
        return DESCRIPTIONS[0];
    }

    private void updateDescriptionAndTips() {
        this.description = getUpdatedDescription();
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
    }

    @Override
    public void onEquip() {
        if (this.counter == -1) {
            this.counter = 0;
            updateDescriptionAndTips();
        }
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (this.counter >= 0 && this.counter < TARGET_WORDS) {
            int wordCount = calculateWordCount(card.name);

            if (wordCount > 0) {
                this.counter += wordCount;

                if (this.counter >= TARGET_WORDS) {
                    this.flash();
                    AbstractDungeon.player.gainGold(GOLD_AMOUNT);
                    AbstractDungeon.effectList.add(new RainingGoldEffect(GOLD_AMOUNT));
                    CardCrawlGame.sound.play("GOLD_JINGLE");

                    this.counter = -2;
                    this.grayscale = true;
                    this.usedUp();
                    updateDescriptionAndTips();
                }
            }
        }
    }

    private int calculateWordCount(String name) {
        if (name == null || name.isEmpty()) return 0;

        String rawName = name.replace("+", "").trim();

        boolean isChinese = (Settings.language == Settings.GameLanguage.ZHS || Settings.language == Settings.GameLanguage.ZHT);

        if (isChinese) {
            String cleanName = rawName.replaceAll("[\\p{P}\\p{S}\\s]", "");
            return cleanName.length();
        } else {
            String cleanName = rawName.replaceAll("[\\p{P}\\p{S}]", " ");
            String[] words = cleanName.trim().split("\\s+");
            return words[0].isEmpty() ? 0 : words.length;
        }
    }

    @Override
    public void setCounter(int setCounter) {
        this.counter = setCounter;

        if (this.counter <= -2 || this.counter >= TARGET_WORDS) {
            this.counter = -2;
            this.grayscale = true;
            this.usedUp();
        } else {
            this.grayscale = false;
        }
        updateDescriptionAndTips();
    }
}