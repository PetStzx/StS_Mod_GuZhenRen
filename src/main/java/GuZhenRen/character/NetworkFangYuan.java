package GuZhenRen.character;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import skindex.skins.player.PlayerSkin;
import spireTogether.monsters.CharacterEntity;
import spireTogether.monsters.playerChars.NetworkCharPreset;
import spireTogether.patches.StanceSwitchRenderPatches;
import spireTogether.ui.elements.presets.Nameplate;

public class NetworkFangYuan extends NetworkCharPreset {

    public NetworkFangYuan() {
        super(new FangYuan("FangYuan"));
    }

    @Override
    public String GetThreeLetterID() {
        return "FYN";
    }

    @Override
    public CharacterEntity CreateNew() {
        return new NetworkFangYuan();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(this.tint.color);

        this.source.drawX = this.drawX;
        this.source.drawY = this.drawY;
        this.source.animX = this.animX;
        this.source.animY = this.animY;
        this.source.flipHorizontal = this.flipHorizontal;
        this.source.flipVertical = this.flipVertical;
        this.source.tint.color.set(this.tint.color);

        this.source.renderPlayerImage(sb);

        StanceSwitchRenderPatches.renderOn = this.source;
        this.stance.render(sb);
        StanceSwitchRenderPatches.renderOn = null;
        this.hb.render(sb);
        this.healthHb.render(sb);
        this.RenderName(sb);
    }

    @Override
    public PlayerSkin GetGhostSkin() {
        return null;
    }

    @Override
    public Texture GetNameplateIcon(String s) {
        return GetDefaultIcon();
    }

    @Override
    public Texture GetDefaultIcon() {
        return ImageMaster.loadImage(GuZhenRen.assetPath("img/character/FangYuan/TisIcon.png"));
    }

    @Override
    public Color GetCharColor() {
        return Color.GRAY;
    }

    @Override
    public Texture GetWhiteSpecialIcon() {
        return GetDefaultIcon();
    }

    @Override
    public Nameplate GetNameplateUnlock() {
        return null;
    }
}