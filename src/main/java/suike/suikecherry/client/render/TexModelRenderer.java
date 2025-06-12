package suike.suikecherry.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class TexModelRenderer extends ModelRenderer {
    private final Runnable textureBinder;

    public TexModelRenderer(ModelBase model, int texOffX, int texOffY, Runnable textureBinder) {
        super(model, texOffX, texOffY);
        this.textureBinder = textureBinder;
    }

    @Override
    public void render(float scale) {
        textureBinder.run();
        super.render(scale);
    }
}