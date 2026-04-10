package GuZhenRen.potions;

import GuZhenRen.GuZhenRen;
import basemod.abstracts.CustomPotion;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class ShengJiYe extends CustomPotion {

    public static final String POTION_ID = GuZhenRen.makeID("ShengJiYe");
    private static final PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString(POTION_ID);

    public static final String NAME = potionStrings.NAME;
    public static final String[] DESCRIPTIONS = potionStrings.DESCRIPTIONS;

    private static final Texture POTION_IMG = ImageMaster.loadImage(GuZhenRen.assetPath("img/potions/ShengJiYe.png"));
    private static final Texture POTION_OUTLINE = ImageMaster.loadImage(GuZhenRen.assetPath("img/potions/ShengJiYe_outline.png"));

    // 用于防空指针崩溃的透明隐形贴图
    private static Texture TRANSPARENT_IMG = null;

    public ShengJiYe() {
        super(NAME, POTION_ID, PotionRarity.PLACEHOLDER, PotionSize.T, PotionColor.GREEN);

        this.isThrown = false;

        this.labOutlineColor = Color.GREEN.cpy();

        // 生成一张 1x1 的全透明图片，用于顶替默认的液体贴图，避开图鉴渲染报错
        if (TRANSPARENT_IMG == null) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(0, 0, 0, 0);
            pixmap.fill();
            TRANSPARENT_IMG = new Texture(pixmap);
            pixmap.dispose();
        }

        // 替换各层贴图
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
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public void use(AbstractCreature target) {
        AbstractCreature p = AbstractDungeon.player;
        if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
            this.addToBot(new HealAction(p, p, this.potency));
        } else {
            p.heal(this.potency);
        }
    }

    @Override
    public int getPotency(int ascensionLevel) {
        return 6;
    }

    @Override
    public AbstractPotion makeCopy() {
        return new ShengJiYe();
    }
}