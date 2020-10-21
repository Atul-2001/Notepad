package org.signature.preferences;

import java.awt.*;
import java.io.File;
import java.util.Objects;
import java.util.prefs.Preferences;

public final class UserPreferences {

    private final Preferences pref;

    public static final String DEFAULT_FILE_DIRECTORY = System.getProperty("user.home").concat(File.separator).concat("Documents");
    public static final boolean DEFAULT_WORD_WRAP = false;
    public static final boolean DEFAULT_SHOW_STATUS_BAR = true;
    public static final String DEFAULT_FONT_FAMILY = "Roboto";
    public static final int DEFAULT_FONT_STYLE = Font.PLAIN;
    public static final int DEFAULT_FONT_SIZE = 14;
    public static final String DEFAULT_FIND_TEXT = "";
    public static final boolean DEFAULT_IS_UP_DIRECTION = false;
    public static final boolean DEFAULT_IS_DOWN_DIRECTION = true;
    public static final boolean DEFAULT_IS_WRAP_AROUND = false;
    public static final boolean DEFAULT_IS_MATCH_CASES = false;

    private final static UserPreferences instance = new UserPreferences();

    private UserPreferences() {
        pref = Preferences.userNodeForPackage(this.getClass());
    }

    public static UserPreferences getInstance() {
        return instance;
    }

    public String get(Key key, String value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        return pref.get(key.name(), value);
    }

    public boolean getBoolean(Key key, boolean value) {
        Objects.requireNonNull(key);
        return pref.getBoolean(key.name(), value);
    }

    public int getInt(Key key, int value) {
        Objects.requireNonNull(key);
        return pref.getInt(key.name(), value);
    }

    public long getLong(Key key, long value) {
        Objects.requireNonNull(key);
        return pref.getLong(key.name(), value);
    }

    public float getFloat(Key key, float value) {
        Objects.requireNonNull(key);
        return pref.getFloat(key.name(), value);
    }

    public double getDouble(Key key, double value) {
        Objects.requireNonNull(key);
        return pref.getDouble(key.name(), value);
    }

    public Font getFont() {
        return new Font(pref.get(Key.FONT_FAMILY.name(), DEFAULT_FONT_FAMILY),
                pref.getInt(Key.FONT_STYLE.name(), DEFAULT_FONT_STYLE),
                pref.getInt(Key.FONT_SIZE.name(), DEFAULT_FONT_SIZE));
    }

    public void set(Key key, String value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        pref.put(key.name(), value);
    }

    public void setBoolean(Key key, boolean value) {
        Objects.requireNonNull(key);
        pref.putBoolean(key.name(), value);
    }

    public void setInt(Key key, int value) {
        Objects.requireNonNull(key);
        pref.putInt(key.name(), value);
    }

    public void setLong(Key key, long value) {
        Objects.requireNonNull(key);
        pref.putLong(key.name(), value);
    }

    public void setFloat(Key key, float value) {
        Objects.requireNonNull(key);
        pref.putFloat(key.name(), value);
    }

    public void setDouble(Key key, double value) {
        Objects.requireNonNull(key);
        pref.putDouble(key.name(), value);
    }

    public void setFont(Font font) {
        Objects.requireNonNull(font);
        pref.put(Key.FONT_FAMILY.name(), font.getFamily());
        pref.putInt(Key.FONT_STYLE.name(), font.getStyle());
        pref.putInt(Key.FONT_SIZE.name(), font.getSize());
    }

    public enum  Key {

        FILE_DIRECTORY,
        WORD_WRAP,
        STATUS_BAR,
        FONT_FAMILY,
        FONT_STYLE,
        FONT_SIZE,
        FIND_TEXT,
        UP_DIRECTION,
        DOWN_DIRECTION,
        WRAP_AROUND,
        MATCH_CASES
    }
}
