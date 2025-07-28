package suike.suikecherry.config.gui;

import java.util.List;
import java.util.Collections;
import java.util.regex.Pattern;
import java.lang.reflect.Field;
import java.util.function.Consumer;

import suike.suikecherry.config.ConfigValue;
import suike.suikecherry.config.CreateConfigFile;

import net.minecraft.client.resources.I18n;

import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries;
import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.IConfigElement;

public class SimpleConfigElement implements IConfigElement {
    private final String name;
    private final Object value;
    private final Object minValue;
    private final Object maxValue;
    private final Object defaultValues;
    private final boolean hasTooltip;

    public SimpleConfigElement(String name, Object value, Object defaultValues) {
        this(name, value, defaultValues, false);
    }
    public SimpleConfigElement(String name, Object value, Object defaultValues, boolean hasTooltip) {
        this(name, value, defaultValues, null, null, hasTooltip);
    }
    public SimpleConfigElement(String name, Object value, Object defaultValues, Object min, Object max) {
        this(name, value, defaultValues, min, max, false);
    }

    public SimpleConfigElement(String name, Object value, Object defaultValues, Object min, Object max, boolean hasTooltip) {
        this.name = name;
        this.value = value;
        this.minValue = min;
        this.maxValue = max;
        this.defaultValues = defaultValues;
        this.hasTooltip = hasTooltip;
    }

    public static class RequiresConfigElement extends SimpleConfigElement {
        public RequiresConfigElement(String name, Object value, Object defaultValues) {
            this(name, value, defaultValues, false);
        }
        public RequiresConfigElement(String name, Object value, Object defaultValues, boolean hasTooltip) {
            this(name, value, defaultValues, null, null, hasTooltip);
        }
        public RequiresConfigElement(String name, Object value, Object defaultValues, Object min, Object max) {
            this(name, value, defaultValues, min, max, false);
        }
        public RequiresConfigElement(String name, Object value, Object defaultValues, Object min, Object max, boolean hasTooltip) {
            super(name, value, defaultValues, min, max, hasTooltip);
        }

        @Override
        public boolean requiresMcRestart() {
            return true;
        }
    }

    @Override
    public void set(Object value) { 
        try {
            Object convertedValue = value;
            Field field = ConfigValue.class.getDeclaredField(this.name);
            if (value instanceof Boolean) {
                convertedValue = Boolean.valueOf(String.valueOf(value));
            } else if (value instanceof Integer) {
                convertedValue = Integer.valueOf(String.valueOf(value));
            } else if (field.getType() == float.class || field.getType() == Float.class) {
                convertedValue = Float.valueOf(String.valueOf(value));
            } else {return;}
            field.set(null, convertedValue);
            this.updateConfigFile(value);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void updateConfigFile(Object value) {
        String configKey = getConfigKey();
        String configValue = String.valueOf(value);
        CreateConfigFile.upConfigValue(configKey, configValue);
    }

    private String getConfigKey() {
        if (value instanceof Boolean) return "B:" + this.name;
        if (value instanceof Integer) return "D:" + this.name;
        if (value instanceof Float) return "D:" + this.name;
        return "S:" + this.name;
    }

    @Override
    public ConfigGuiType getType() { 
        if (value instanceof Boolean) return ConfigGuiType.BOOLEAN;
        if (value instanceof Integer) return ConfigGuiType.INTEGER;
        return ConfigGuiType.STRING;
    }

    @Override
    public String getName() {
        return I18n.format("suikecherry.config." + this.name); 
    }

    @Override
    public String getComment() {
        if (this.hasTooltip) {
            return I18n.format("suikecherry.config.tooltip." + this.name);
        }
        return "";
    }

    @Override 
    public Object getMinValue() { 
        if (value instanceof Boolean) return null;
        return minValue != null ? minValue : 1;
    }

    @Override 
    public Object getMaxValue() { 
        if (value instanceof Boolean) return null;
        return maxValue != null ? maxValue : 100;
    }

    @Override
    public Object getDefault() {
        return this.defaultValues;
    }

    @Override
    public void set(Object[] values) {
        for (Object value : values) {
            this.set(value);
        }
    }

    @Override
    public boolean requiresMcRestart() {
        return false;
    }

    @Override
    public Object get() {
        if (value instanceof Float) {
            return formatFloatForDisplay((Float) value);
        }
        return value;
    }

    private String formatFloatForDisplay(float value) {
        return String.format("%.2f", value);
    }

    @Override public boolean isList() { return false; }
    @Override public int getMaxListLength() { return 0; }
    @Override public String[] getValidValues() { return null; }
    @Override public boolean isListLengthFixed() { return false; }
    @Override public Pattern getValidationPattern() { return null; }
    @Override public boolean hasSlidingControl() { return value instanceof Integer; }
    @Override public Object[] getList() { return new Object[]{value}; }
    @Override public boolean isProperty() { return true; }
    @Override public boolean isDefault() { return false; }
    @Override public Object[] getDefaults() { return new Object[]{value}; }
    @Override public void setToDefault() { set(value); }
    @Override public boolean requiresWorldRestart() { return false; }
    @Override public boolean showInGui() { return true; }
    @Override public String getLanguageKey() { return this.getName(); }
    @Override public String getQualifiedName() { return this.getName(); }
    @Override public List<IConfigElement> getChildElements() { return Collections.emptyList(); }
    @Override public Class<? extends GuiConfigEntries.IConfigEntry> getConfigEntryClass() { return null; }
    @Override public Class<? extends GuiEditArrayEntries.IArrayEntry> getArrayEntryClass() { return null; }
}