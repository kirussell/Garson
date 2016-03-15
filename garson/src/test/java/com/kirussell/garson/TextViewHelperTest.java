package com.kirussell.garson;

import android.graphics.Point;
import android.graphics.Rect;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;

/**
 * Created by russellkim on 15/03/16.
 * Text helper tests
 */
@RunWith(GarsonRobolectricUnitTestRunner.class)
public class TextViewHelperTest {

    TextViewHelper textHelper;
    private Executor executor = Executors.newSingleThreadExecutor();
    private Rect rect;

    @Before
    public void setup() {
        textHelper = new TextViewHelper(
                GarsonRobolectricUnitTestRunner.appContext(),
                executor
        );
    }

    @Test
    public void checkTextPositioning() {
        // left-top
        checkPosition(
                128, 128, // area
                0, 0, 10, 10, // mask bounds
                10, // padding
                new CheckState() {
                    @Override
                    public boolean isValid(Rect margins) {
                        return margins.top >= 10 + 10;
                    }
                }
        );
        // right-top
        checkPosition(
                128, 128, // area
                118, 118, 118 + 10, 118 + 10, // mask bounds
                10, //padding
                new CheckState() {
                    @Override
                    public boolean isValid(Rect margins) {
                        return margins.top >= 10 + 10;
                    }
                }
        );
        // left-bottom
        checkPosition(
                128, 128, // area
                118, 118, 128, 128, // mask bounds
                10, // padding
                new CheckState() {
                    @Override
                    public boolean isValid(Rect margins) {
                        return margins.top <= 128 - 10 - 10;
                    }
                }
        );
        // right-bottom
        checkPosition(
                128, 128, // area
                118, 118, 128, 128, // mask bounds
                10, // padding
                new CheckState() {
                    @Override
                    public boolean isValid(Rect margins) {
                        return margins.top <= 128 - 10 - 10;
                    }
                }
        );
        // center
        checkPosition(
                128, 128, // area
                60, 60, 68, 68, // mask bounds
                10, // padding
                new CheckState() {
                    @Override
                    public boolean isValid(Rect margins) {
                        return margins.top <= 60 - 10;
                    }
                }
        );
        // center - bit up
        checkPosition(
                128, 128, // area
                59, 59, 68, 68, // mask bounds
                10, // padding
                new CheckState() {
                    @Override
                    public boolean isValid(Rect margins) {
                        return margins.top >= 68 + 10;
                    }
                }
        );
        // center - bit down
        checkPosition(
                128, 128, // area
                60, 60, 69, 69, // mask bounds
                10, // padding
                new CheckState() {
                    @Override
                    public boolean isValid(Rect margins) {
                        return margins.top <= 60 - 10;
                    }
                }
        );
        // left-full height
        checkPosition(
                128, 128, // area
                0, 0, 10, 128, // mask bounds
                10, // padding
                new CheckState() {
                    @Override
                    public boolean isValid(Rect margins) {
                        return margins.left >= 10 + 10;
                    }
                }
        );
        // right-full height
        checkPosition(
                128, 128, // area
                118, 0, 128, 128, // mask bounds
                10, // padding
                new CheckState() {
                    @Override
                    public boolean isValid(Rect margins) {
                        return margins.left <= 128 - 10 - 10;
                    }
                }
        );
        // top - full width
        checkPosition(
                128, 128, // area
                0, 0, 128, 10, // mask bounds
                10, // padding
                new CheckState() {
                    @Override
                    public boolean isValid(Rect margins) {
                        return margins.top >= 10 + 10;
                    }
                }
        );
        // bottom - full width
        checkPosition(
                128, 128, // area
                0, 118, 128, 128, // mask bounds
                10, // padding
                new CheckState() {
                    @Override
                    public boolean isValid(Rect margins) {
                        return margins.top <= 128 - 10 - 10;
                    }
                }
        );
    }

    private void checkPosition(int areaWidth, int areaHeight,
                               int viewLeft, int viewTop, int viewRight, int viewBottom,
                               int padding, CheckState check) {
        Rect bounds = obtainRect(viewLeft, viewTop, viewRight, viewBottom);
        TextViewHelper.TextMetrics metrics = textHelper.createTextMetrics(
                new Point(areaWidth, areaHeight),
                bounds,
                "a", 1, null, 0, 0, padding
        );
        assertNotNull(metrics);
        String log = "area(" + areaWidth + "," + areaHeight + ")"
                + ";margins=" + metrics.margins + ";view=" + bounds;
        assertTrue(
                "text out of " + log,
                metrics.margins.left >= padding
                        || metrics.margins.top >= padding
                        || metrics.margins.right <= areaWidth - padding
                        || metrics.margins.bottom <= areaHeight - padding
        );
        assertTrue(log, check.isValid(metrics.margins));
    }

    private Rect obtainRect(int viewLeft, int viewTop, int viewRight, int viewBottom) {
        if (rect == null) {
            rect = new Rect();
        }
        rect.set(viewLeft, viewTop, viewRight, viewBottom);
        return rect;
    }

    private interface CheckState {
        boolean isValid(Rect margins);
    }
}
