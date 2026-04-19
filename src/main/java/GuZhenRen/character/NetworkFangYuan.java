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
        if (this.source.img == null) {
            this.source.img = ImageMaster.loadImage(GuZhenRen.assetPath("img/character/FangYuan/Idle.png"));
        }
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
        if (this.source.img != null) {
            float unifiedScale = Settings.scale * this.getScale();
            sb.draw(this.source.img,
                    this.drawX - (float) this.source.img.getWidth() * unifiedScale / 2.0F + this.animX,
                    this.drawY + this.animY,
                    (float) this.source.img.getWidth() * unifiedScale,
                    (float) this.source.img.getHeight() * unifiedScale,
                    0, 0,
                    this.source.img.getWidth(), this.source.img.getHeight(),
                    this.flipHorizontal, this.flipVertical);
        }

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