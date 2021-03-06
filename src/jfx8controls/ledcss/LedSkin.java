/*
 * Copyright (c) 2013. by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jfx8controls.ledcss;


import javafx.css.CssMetaData;
import javafx.css.ParsedValue;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;


public class LedSkin extends SkinBase<Led> implements Skin<Led> {
    private static final double PREFERRED_SIZE = 16;
    private static final double MINIMUM_SIZE   = 8;
    private static final double MAXIMUM_SIZE   = 1024;
    private double              size;
    private Region              frame;
    private Region              led;
    private Region              highlight;
    private InnerShadow         innerShadow;
    private DropShadow          glow;


    // ******************** Constructors **************************************
    public LedSkin(final Led CONTROL) {
        super(CONTROL);
        init();
        initGraphics();
        registerListeners();
    }


    // ******************** Initialization ************************************
    private void init() {
        if (Double.compare(getSkinnable().getPrefWidth(), 0.0) <= 0 ||
            Double.compare(getSkinnable().getPrefHeight(), 0.0) <= 0 ||
            Double.compare(getSkinnable().getWidth(), 0.0) <= 0 ||
            Double.compare(getSkinnable().getHeight(), 0.0) <= 0) {
            if (getSkinnable().getPrefWidth() > 0 && getSkinnable().getPrefHeight() > 0) {
                getSkinnable().setPrefSize(getSkinnable().getPrefWidth(), getSkinnable().getPrefHeight());
            } else {
                getSkinnable().setPrefSize(PREFERRED_SIZE, PREFERRED_SIZE);
            }
        }

        if (Double.compare(getSkinnable().getMinWidth(), 0.0) <= 0 ||
            Double.compare(getSkinnable().getMinHeight(), 0.0) <= 0) {
            getSkinnable().setMinSize(MINIMUM_SIZE, MINIMUM_SIZE);
        }

        if (Double.compare(getSkinnable().getMaxWidth(), 0.0) <= 0 ||
            Double.compare(getSkinnable().getMaxHeight(), 0.0) <= 0) {
            getSkinnable().setMaxSize(MAXIMUM_SIZE, MAXIMUM_SIZE);
        }
    }

    private void initGraphics() {
        frame = new Region();
        frame.getStyleClass().setAll("frame");
        frame.setOpacity(getSkinnable().isFrameVisible() ? 1 : 0);

        led = new Region();
        led.getStyleClass().setAll("main");
        //led.setStyle("-led-color: " + (getSkinnable().getLedColor()).toString().replace("0x", "#") + ";");
        getSkinnable().ledColorProperty().applyStyle(StyleOrigin.INLINE, getSkinnable().getLedColor());

        innerShadow = new InnerShadow(BlurType.TWO_PASS_BOX, Color.rgb(0, 0, 0, 0.65), 8, 0d, 0d, 0d);

        glow = new DropShadow(BlurType.TWO_PASS_BOX, (Color) getSkinnable().getLedColor(), 20, 0d, 0d, 0d);
        glow.setInput(innerShadow);

        highlight = new Region();
        highlight.getStyleClass().setAll("highlight");

        // Add all nodes
        getChildren().addAll(frame, led, highlight);
    }

    private void registerListeners() {
        getSkinnable().widthProperty().addListener(observable -> handleControlPropertyChanged("RESIZE") );
        getSkinnable().heightProperty().addListener(observable -> handleControlPropertyChanged("RESIZE") );
        getSkinnable().ledColorProperty().addListener(observable -> handleControlPropertyChanged("COLOR") );
        getSkinnable().onProperty().addListener(observable -> handleControlPropertyChanged("ON") );
        getSkinnable().frameVisibleProperty().addListener(observable -> handleControlPropertyChanged("FRAME_VISIBLE") );
    }


    // ******************** Methods *******************************************
    protected void handleControlPropertyChanged(final String PROPERTY) {
        if ("RESIZE".equals(PROPERTY)) {
            resize();
        } else if ("COLOR".equals(PROPERTY)) {
            //led.setStyle("-led-color: " + (getSkinnable().getLedColor()).toString().replace("0x", "#") + ";");
            getSkinnable().ledColorProperty().applyStyle(getSkinnable().ledColorProperty().getStyleOrigin(), getSkinnable().getLedColor());
            System.out.println(getSkinnable().ledColorProperty().getCssMetaData());
            resize();
        } else if ("ON".equals(PROPERTY)) {
            led.setEffect(getSkinnable().isOn() ? glow : innerShadow);
        } else if ("FRAME_VISIBLE".equals(PROPERTY)) {
            frame.setOpacity(getSkinnable().isFrameVisible() ? 1.0 : 0.0);
        }
    }

    @Override protected double computeMinWidth(final double HEIGHT, double TOP_INSET, double RIGHT_INSET, double BOTTOM_INSET, double LEFT_INSET) {
        return super.computeMinWidth(Math.max(MINIMUM_SIZE, HEIGHT - TOP_INSET - BOTTOM_INSET), TOP_INSET, RIGHT_INSET, BOTTOM_INSET, LEFT_INSET);
    }
    @Override protected double computeMinHeight(final double WIDTH, double TOP_INSET, double RIGHT_INSET, double BOTTOM_INSET, double LEFT_INSET) {
        return super.computeMinHeight(Math.max(MINIMUM_SIZE, WIDTH - LEFT_INSET - RIGHT_INSET), TOP_INSET, RIGHT_INSET, BOTTOM_INSET, LEFT_INSET);
    }

    @Override protected double computeMaxWidth(final double HEIGHT, double TOP_INSET, double RIGHT_INSET, double BOTTOM_INSET, double LEFT_INSET) {
        return super.computeMaxWidth(Math.min(MAXIMUM_SIZE, HEIGHT - TOP_INSET - BOTTOM_INSET), TOP_INSET, RIGHT_INSET, BOTTOM_INSET, LEFT_INSET);
    }
    @Override protected double computeMaxHeight(final double WIDTH, double TOP_INSET, double RIGHT_INSET, double BOTTOM_INSET, double LEFT_INSET) {
        return super.computeMaxHeight(Math.min(MAXIMUM_SIZE, WIDTH - LEFT_INSET - RIGHT_INSET), TOP_INSET, RIGHT_INSET, BOTTOM_INSET, LEFT_INSET);
    }

    @Override protected double computePrefWidth(final double HEIGHT, double TOP_INSET, double RIGHT_INSET, double BOTTOM_INSET, double LEFT_INSET) {
        double prefHeight = PREFERRED_SIZE;
        if (HEIGHT != -1) {
            prefHeight = Math.max(0, HEIGHT - TOP_INSET - BOTTOM_INSET);
        }
        return super.computePrefWidth(prefHeight, TOP_INSET, RIGHT_INSET, BOTTOM_INSET, LEFT_INSET);
    }
    @Override protected double computePrefHeight(final double WIDTH, double TOP_INSET, double RIGHT_INSET, double BOTTOM_INSET, double LEFT_INSET) {
        double prefWidth = PREFERRED_SIZE;
        if (WIDTH != -1) {
            prefWidth = Math.max(0, WIDTH - LEFT_INSET - RIGHT_INSET);
        }
        return super.computePrefHeight(prefWidth, TOP_INSET, RIGHT_INSET, BOTTOM_INSET, LEFT_INSET);
    }


    // ******************** Private Methods ***********************************
    private void resize() {
        size = getSkinnable().getWidth() < getSkinnable().getHeight() ? getSkinnable().getWidth() : getSkinnable().getHeight();
        if (size > 0) {
            innerShadow.setRadius(0.07 * size);
            glow.setRadius(0.36 * size);
            glow.setColor((Color) getSkinnable().getLedColor());

            frame.setMaxSize(size, size);

            led.setMaxSize(0.72 * size, 0.72 * size);
            led.relocate(0.14 * size, 0.14 * size);
            led.setEffect(getSkinnable().isOn() ? glow : innerShadow);

            highlight.setMaxSize(0.58 * size, 0.58 * size);
            highlight.relocate(0.21 * size, 0.21 * size);
        }
    }
}
