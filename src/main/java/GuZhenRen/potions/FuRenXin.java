package GuZhenRen.potions;

import GuZhenRen.GuZhenRen;
import basemod.ReflectionHacks;
import basemod.abstracts.CustomPotion;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.PoisonPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import java.util.ArrayList;

public class FuRenXin extends CustomPotion {

    public static final String POTION_ID = GuZhenRen.makeID("FuRenXin");
    private static final PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString(POTION_ID);

    public static final String NAME = potionStrings.NAME;
    public static final String[] DESCRIPTIONS = potionStrings.DESCRIPTIONS;

    private static final Texture POTION_IMG = ImageMaster.loadImage(GuZhenRen.assetPath("img/potions/FuRenXin.png"));
    private static final Texture POTION_OUTLINE = ImageMaster.loadImage(GuZhenRen.assetPath("img/potions/FuRenXin_outline.png"));
    private static Texture TRANSPARENT_IMG = null;

    public static ArrayList<Integer> bonusList = new ArrayList<>();
    static {
        for (int i = 0; i < 20; i++) {
            bonusList.add(0);
        }
    }

    public FuRenXin() {
        super(NAME, POTION_ID, PotionRarity.RARE, PotionSize.HEART, PotionColor.POISON);

        this.isThrown = true;
        this.targetRequired = true;
        this.labOutlineColor = Color.PURPLE.cpy();

        if (TRANSPARENT_IMG == null) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(0, 0, 0, 0);
            pixmap.fill();
            TRANSPARENT_IMG = new Texture(pixmap);
            pixmap.dispose();
        }

        ReflectionHacks.setPrivate(this, AbstractPotion.class, "containerImg", POTION_IMG);
        ReflectionHacks.setPrivate(this, AbstractPotion.class, "outlineImg", POTION_OUTLINE);
        ReflectionHacks.setPrivate(this, AbstractPotion.class, "liquidImg", TRANSPARENT_IMG);
        ReflectionHacks.setPrivate(this, AbstractPotion.class, "hybridImg", null);
        ReflectionHacks.setPrivate(this, AbstractPotion.class, "spotsImg", null);
    }

    @Override
    public void initializeData() {
        this.potency = getPotency();
        this.description = DESCRIPTIONS[0] + this.potency + DESCRIPTIONS[1];
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.tips.add(new PowerTip(
                com.megacrit.cardcrawl.helpers.GameDictionary.POISON.NAMES[0],
                com.megacrit.cardcrawl.helpers.GameDictionary.POISON.DESCRIPTION
        ));
    }


    @Override
    public boolean canUse() {
        if (AbstractDungeon.getCurrRoom() == null) return false;
        if (AbstractDungeon.getCurrRoom().phase != AbstractRoom.RoomPhase.COMBAT) return false;
        if (AbstractDungeon.getCurrRoom().monsters == null || AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) return false;
        if (AbstractDungeon.actionManager.turnHasEnded) return false;
        return true;
    }

    @Override
    public void use(AbstractCreature target) {
        if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && target != null) {
            this.addToBot(new ApplyPowerAction(target, AbstractDungeon.player, new PoisonPower(target, AbstractDungeon.player, this.potency), this.potency));
        }
    }

    @Override
    public int getPotency(int ascensionLevel) {
        int extra = 0;
        if (this.slot >= 0 && this.slot < bonusList.size()) {
            extra = bonusList.get(this.slot);
        }
        return 6 + extra;
    }

    @Override
    public AbstractPotion makeCopy() {
        return new FuRenXin();
    }


    @SpirePatch(clz = AbstractMonster.class, method = "die", paramtypez = {boolean.class})
    public static class FuRenXinMonsterDeathPatch {
        @SpirePrefixPatch
        public static void Prefix(AbstractMonster __instance, boolean triggerRelics) {
            if (AbstractDungeon.player == null) return;
            if (!__instance.halfDead && !__instance.hasPower("Minion")) {
                if (AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
                    for (AbstractPotion p : AbstractDungeon.player.potions) {
                        if (p instanceof FuRenXin && p.slot >= 0 && p.slot < bonusList.size()) {
                            int currentBonus = bonusList.get(p.slot);
                            bonusList.set(p.slot, currentBonus + 3);
                            p.initializeData();
                        }
                    }
                }
            }
        }
    }

    @SpirePatch(clz = AbstractPotion.class, method = "setAsObtained", paramtypez = {int.class})
    public static class PotionObtainPatch {
        @SpirePrefixPatch
        public static void Prefix(AbstractPotion __instance, int potionSlot) {
            if (!CardCrawlGame.loadingSave && potionSlot >= 0 && potionSlot < FuRenXin.bonusList.size()) {
                FuRenXin.bonusList.set(potionSlot, 0);
            }
        }

        @SpirePostfixPatch
        public static void Postfix(AbstractPotion __instance, int potionSlot) {
            if (__instance instanceof FuRenXin) {
                __instance.initializeData();
            }
        }
    }


    public static class SaveData implements CustomSavable<ArrayList<Integer>> {
        @Override
        public ArrayList<Integer> onSave() {
            return FuRenXin.bonusList;
        }

        @Override
        public void onLoad(ArrayList<Integer> data) {
            if (data != null) {
                FuRenXin.bonusList = data;
                while (FuRenXin.bonusList.size() < 20) {
                    FuRenXin.bonusList.add(0);
                }
            } else {
                for (int i = 0; i < 20; i++) {
                    FuRenXin.bonusList.set(i, 0);
                }
            }

            if (AbstractDungeon.player != null && AbstractDungeon.player.potions != null) {
                for (AbstractPotion p : AbstractDungeon.player.potions) {
                    if (p instanceof FuRenXin) {
                        p.initializeData();
                    }
                }
            }
        }
    }
}