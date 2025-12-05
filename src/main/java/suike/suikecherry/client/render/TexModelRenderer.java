package suike.suikecherry.client.render;

import java.util.function.Supplier;

import net.minecraft.util.EnumFacing;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class TexModelRenderer extends ModelRenderer {
    private final Runnable beforeRend;
    private final Supplier<Boolean> shouldRender;

    public TexModelRenderer(ModelBase model, int texOffX, int texOffY) {
        this(model, texOffX, texOffY, () -> {});
    }

    public TexModelRenderer(ModelBase model, int texOffX, int texOffY, Runnable beforeRend) {
        super(model, texOffX, texOffY);
        this.beforeRend = beforeRend;
        this.shouldRender = () -> true;
    }

    public TexModelRenderer(ModelBase model, int texOffX, int texOffY, Supplier<Boolean> shouldRender) {
        super(model, texOffX, texOffY);
        this.shouldRender = shouldRender;
        this.beforeRend = () -> {};
    }

    @Override
    public void render(float scale) {
        // 是否需要渲染模型
        if (!shouldRender.get()) return;

        // 渲染前执行
        beforeRend.run();

        super.render(scale);
    }
}