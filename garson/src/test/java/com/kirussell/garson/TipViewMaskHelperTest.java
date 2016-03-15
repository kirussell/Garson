package com.kirussell.garson;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Created by russellkim on 09/03/16.
 * Mask Helper tests
 */
@RunWith(GarsonRobolectricUnitTestRunner.class)
public class TipViewMaskHelperTest {

    private TipViewMaskHelper maskHelper;
    private View area;
    private Rect rect;

    @Before
    public void setup() {
        maskHelper = new TipViewMaskHelper();
        maskHelper.setInset(0);
        area = mock(View.class);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                int[] loc = (int[]) invocation.getArguments()[0];
                loc[0] = 0;
                loc[1] = 0;
                return loc;
            }
        }).when(area).getLocationOnScreen(any(int[].class));
    }

    Func<Rect> noInsetsBoundsFunc = new Func<Rect>() {
        @Override
        public Rect calc(int... vals) {
            return expectedBoundsNoInsets(vals[0], vals[1], vals[2], vals[3], vals[4], vals[5]);
        }

        private Rect expectedBoundsNoInsets(int areaLeft, int areaTop, int left, int top, int width, int height) {
            int expectedLeft = left - areaLeft;
            int expectedTop = top - areaTop;
            return obtainRect(expectedLeft, expectedTop, expectedLeft + width, expectedTop + height);
        }
    };

    Func<Rect> withInsetsBoundsFunc = new Func<Rect>() {
        @Override
        public Rect calc(int... vals) {
            return expectedBoundsWithInsets(vals[0], vals[1], vals[2], vals[3], vals[4], vals[5],
                    vals[6]);
        }

        private Rect expectedBoundsWithInsets(int areaLeft, int areaTop, int left, int top, int width, int height,
                                              int inset) {
            int expectedLeft = left - areaLeft;
            int expectedTop = top - areaTop;
            Rect rect = obtainRect(expectedLeft, expectedTop, expectedLeft + width, expectedTop + height);
            rect.inset(-inset, -inset);
            return rect;
        }
    };

    @Test
    public void checkMaskBounds() throws Exception {
        checkBounds(0, 0, 0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE, noInsetsBoundsFunc, area);
        checkBounds(0, 0, 10, 20, 128, 256, noInsetsBoundsFunc, area);
        checkBounds(0, 0, 0, 20, 128, 256, noInsetsBoundsFunc, area);
        checkBounds(0, 0, 10, 0, 128, 256, noInsetsBoundsFunc, area);
        checkBounds(0, 0, 0, 0, 128, 256, noInsetsBoundsFunc, area);
        checkBounds(0, 0, 0, 0, 1, 1, noInsetsBoundsFunc, area);
        checkBounds(0, 0, 0, 0, 0, 0, noInsetsBoundsFunc, area);
        checkBounds(0, 0, 128, 128, 128, 128, noInsetsBoundsFunc, area);
        checkBounds(0, 0, -10, -20, 128, 256, noInsetsBoundsFunc, area);
        checkBounds(10, 15, 10, 0, 128, 256, noInsetsBoundsFunc, area);
        checkBounds(256, 256, 10, 20, 128, 256, noInsetsBoundsFunc, area);
        checkBounds(-20, -15, 0, 20, 128, 256, noInsetsBoundsFunc, area);
        checkBounds(10, 15, -10, -20, 128, 256, noInsetsBoundsFunc, area);
        checkBounds(0, 0, 0, 0, 0, 0, noInsetsBoundsFunc, area);
    }

    private void checkBounds(int areaLeft, int areaTop, int left, int top, int width, int height,
                             Func<Rect> expectedBounds, View area) {
        View viewToTip = createViewToTip(left, top, width, height);
        assertEquals(
                String.format("al=%d,at=%d,l=%d,t=%d,w=%d,h=%d", areaLeft, areaTop, left, top, width, height),
                expectedBounds.calc(areaLeft, areaTop, left, top, width, height),
                maskHelper.getMaskBounds(viewToTip, doLocationInWindow(areaLeft, areaTop, area))
        );
    }

    @Test
    public void checkMaskBoundsWithInsets() throws Exception {
        int maxI = Integer.MAX_VALUE;
        maskHelper.setInset(12);
        checkBounds(12, 0, 0, 0, 0, maxI, maxI, withInsetsBoundsFunc, area);
        maskHelper.setInset(maxI);
        checkBounds(maxI, 0, 0, 0, 0, maxI, maxI, withInsetsBoundsFunc, area);
        maskHelper.setInset(-255);
        checkBounds(-255, 0, 0, 10, 20, 128, 256, withInsetsBoundsFunc, area);
        maskHelper.setInset(-128);
        checkBounds(-128, 10, 20, 0, 20, 128, 256, withInsetsBoundsFunc, area);
        maskHelper.setInset(129);
        checkBounds(129, 10, 15, 10, 0, 128, 256, withInsetsBoundsFunc, area);
        maskHelper.setInset(-129);
        checkBounds(-129, 0, 0, 10, 0, 128, 256, withInsetsBoundsFunc, area);
    }

    private void checkBounds(int inset,
                             int areaLeft, int areaTop,
                             int left, int top, int width, int height,
                             Func<Rect> expectedBounds, View area) {
        View viewToTip = createViewToTip(left, top, width, height);
        assertEquals(
                String.format("inset=%d,al=%d,at=%d,l=%d,t=%d,w=%d,h=%d", inset,
                        areaLeft, areaTop, left, top, width, height),
                expectedBounds.calc(areaLeft, areaTop, left, top, width, height, inset),
                maskHelper.getMaskBounds(viewToTip, doLocationInWindow(areaLeft, areaTop, area))
        );
    }

    @Test
    public void checkGenerateMask() throws Exception {
        generateAndCheckSizes(10, 10, 0);
        generateAndCheckSizes(10, 10, 8);
        generateAndCheckSizes(128, 128, 0);
        try {
            generateAndCheckSizes(0, 0, 0);
            assertTrue("Zero-size bitmap can not be created", false);
        } catch (IllegalArgumentException ex){
            // as expected
        }
    }

    private void generateAndCheckSizes(int width, int height, int inset) {
        maskHelper.setInset(inset);
        Bitmap bmp = maskHelper.generateMask(createViewToTip(0, 0, width, height), null, Color.WHITE);
        assertNotNull(bmp);
        assertEquals(width + 2 * inset, bmp.getWidth());
        assertEquals(height + 2 * inset, bmp.getHeight());
    }

    @NonNull
    private Rect obtainRect(int l, int t, int r, int b) {
        if (rect == null) {
            rect = new Rect();
        }
        rect.set(l, t, r, b);
        return rect;
    }

    @NonNull
    private View createViewToTip(final int left, final int top, int width, int height) {
        View viewToTip = mock(View.class);
        when(viewToTip.getWidth()).thenReturn(width);
        when(viewToTip.getHeight()).thenReturn(height);
        doLocationInWindow(left, top, viewToTip);
        return viewToTip;
    }

    private View doLocationInWindow(final int left, final int top, View view) {
        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                int[] loc = (int[]) invocation.getArguments()[0];
                loc[0] = left;
                loc[1] = top;
                return null;
            }
        }).when(view).getLocationInWindow(any(int[].class));
        return view;
    }

    interface Func<T> {
        T calc(int... vals);
    }
}
