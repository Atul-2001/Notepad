package org.signature.util;

import javafx.beans.property.IntegerProperty;

import javax.swing.*;
import java.awt.geom.AffineTransform;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;

public final class Zoom {

    private static final BigDecimal DEFAULT_ZOOM = new BigDecimal("1.0");
    private static final BigDecimal ZOOM_RATIO = new BigDecimal("0.2");
    private static final BigDecimal MINIMUM_ZOOM = new BigDecimal("0.1");
    private static final BigDecimal MAXIMUM_ZOOM = new BigDecimal("9.0");

    private static JTextArea writingPad;
    private static final AffineTransform transformTx = new AffineTransform();
    private static BigDecimal zoomValue = new BigDecimal("1.0");
    private static BigDecimal zoomInResult = new BigDecimal("1.2");
    private static BigDecimal zoomOutResult = new BigDecimal("0.8");
    private static IntegerProperty zoomLevel;

    private Zoom() {}

    public static void setControls(JTextArea control, IntegerProperty zoomLevel) {
        Objects.requireNonNull(control);
        Objects.requireNonNull(zoomLevel);
        writingPad = control;
        Zoom.zoomLevel = zoomLevel;
    }

    public static AffineTransform getTransform() {
        return transformTx;
    }

    public static void in() {
        if (zoomInResult.doubleValue() <= MAXIMUM_ZOOM.doubleValue()) {
            if (zoomValue.doubleValue() == MINIMUM_ZOOM.doubleValue()) {
                zoomInResult = new BigDecimal("0.2");
            }

            BigDecimal zoomIn = zoomInResult.divide(zoomValue, MathContext.DECIMAL32);
            transformTx.scale(zoomIn.doubleValue(), zoomIn.doubleValue());
            writingPad.setFont(writingPad.getFont().deriveFont(transformTx));

            zoomLevel.set(zoomLevel.get() + 10);

            zoomOutResult = new BigDecimal(zoomValue.toString());
            zoomValue = new BigDecimal(zoomInResult.toString());
            if (zoomValue.doubleValue() < 1.0) {
                zoomInResult = new BigDecimal(zoomInResult.add(MINIMUM_ZOOM, MathContext.DECIMAL32).toString());
            } else {
                zoomInResult = new BigDecimal(zoomInResult.add(ZOOM_RATIO, MathContext.DECIMAL32).toString());
            }
        }
    }

    public static void out() {
        if (zoomOutResult.doubleValue() >= MINIMUM_ZOOM.doubleValue()) {
            if (zoomValue.doubleValue() == DEFAULT_ZOOM.doubleValue()) {
                zoomOutResult = new BigDecimal("0.9");
            }
writingPad.getFont().getTransform();
            BigDecimal zoomOut = zoomOutResult.divide(zoomValue, MathContext.DECIMAL32);
            transformTx.scale(zoomOut.doubleValue(), zoomOut.doubleValue());
            writingPad.setFont(writingPad.getFont().deriveFont(transformTx));

            zoomLevel.set(zoomLevel.get() - 10);

            zoomInResult = new BigDecimal(zoomValue.toString());
            zoomValue = new BigDecimal(zoomOutResult.toString());

            if (zoomOutResult.doubleValue() == MINIMUM_ZOOM.doubleValue()) {
                zoomOutResult = new BigDecimal("0.0");
            } else if (zoomValue.doubleValue() < DEFAULT_ZOOM.doubleValue()) {
                zoomOutResult = new BigDecimal(zoomOutResult.subtract(MINIMUM_ZOOM, MathContext.DECIMAL32).toString());
            } else if (zoomOutResult.subtract(ZOOM_RATIO).doubleValue() <= MINIMUM_ZOOM.doubleValue()) {
                zoomOutResult = new BigDecimal(MINIMUM_ZOOM.toString());
            } else {
                zoomOutResult = new BigDecimal(zoomOutResult.subtract(ZOOM_RATIO, MathContext.DECIMAL32).toString());
            }
        }
    }

    public static void restore() {
        BigDecimal zoomDefault = DEFAULT_ZOOM.divide(zoomValue, MathContext.DECIMAL32);
        transformTx.scale(zoomDefault.doubleValue(), zoomDefault.doubleValue());
        writingPad.setFont(writingPad.getFont().deriveFont(transformTx));
        zoomLevel.set(100);
        zoomValue = new BigDecimal("1.0");
        zoomInResult = new BigDecimal("1.2");
        zoomOutResult = new BigDecimal("0.8");
    }
}
