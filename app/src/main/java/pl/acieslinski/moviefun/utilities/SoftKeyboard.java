/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.acieslinski.moviefun.utilities;

/*
 * Author: Felipe Herranz (felhr85@gmail.com)
 * Contributors:Francesco Verheye (verheye.francesco@gmail.com)
 * Israel Dominguez (dominguez.israel@gmail.com)
 */

import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SoftKeyboard implements View.OnFocusChangeListener {
    private static final int CLEAR_FOCUS = 0;

    private ViewGroup layout;
    private int layoutBottom;
    private InputMethodManager im;
    private int[] coords;
    private boolean isKeyboardShow;
    private SoftKeyboardChangesThread softKeyboardThread;
    private List<EditText> editTextList;


    private View tempView; // reference to a focused EditText

    public SoftKeyboard(ViewGroup layout, InputMethodManager im) {
        this.layout = layout;
        keyboardHideByDefault();
        initEditTexts(layout);
        this.im = im;
        this.coords = new int[2];
        this.isKeyboardShow = false;
        this.softKeyboardThread = new SoftKeyboardChangesThread();
        this.softKeyboardThread.start();
    }


    public void openSoftKeyboard(SoftKeyboardShowCallback callback) {
        if (!isKeyboardShow) {
            softKeyboardThread.setShowCallback(callback);
            layoutBottom = getLayoutCoordinates();
            im.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT);
            softKeyboardThread.keyboardOpened();
            isKeyboardShow = true;
        } else {
            callback.onSoftKeyboardShow();
        }
    }

    public void closeSoftKeyboard(SoftKeyboardHideCallback callback) {
        if (isKeyboardShow) {
            softKeyboardThread.setHideCallback(callback);
            im.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            isKeyboardShow = false;
        } else {
            callback.onSoftKeyboardHide();
        }
    }

    public void unRegisterSoftKeyboardCallback() {
        softKeyboardThread.stopThread();
    }

    public interface SoftKeyboardShowCallback {
        void onSoftKeyboardShow();
    }

    public interface SoftKeyboardHideCallback {
        void onSoftKeyboardHide();
    }

    private int getLayoutCoordinates() {
        layout.getLocationOnScreen(coords);
        return coords[1] + layout.getHeight();
    }

    private void keyboardHideByDefault() {
        layout.setFocusable(true);
        layout.setFocusableInTouchMode(true);
    }

    /*
     * InitEditTexts now handles EditTexts in nested views
     * Thanks to Francesco Verheye (verheye.francesco@gmail.com)
     */
    private void initEditTexts(ViewGroup viewgroup) {
        if (editTextList == null)
            editTextList = new ArrayList<EditText>();

        int childCount = viewgroup.getChildCount();
        for (int i = 0; i <= childCount - 1; i++) {
            View v = viewgroup.getChildAt(i);


            if (v instanceof ViewGroup) {
                initEditTexts((ViewGroup) v);
            }


            if (v instanceof EditText) {
                EditText editText = (EditText) v;
                editText.setOnFocusChangeListener(this);
                editText.setCursorVisible(true);
                editTextList.add(editText);
            }
        }
    }


    /*
     * OnFocusChange does update tempView correctly now when keyboard is still shown
     * Thanks to Israel Dominguez (dominguez.israel@gmail.com)
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            tempView = v;
            if (!isKeyboardShow) {
                layoutBottom = getLayoutCoordinates();
                softKeyboardThread.keyboardOpened();
                isKeyboardShow = true;
            }
        }
    }

    // This handler will clear focus of selected EditText
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message m) {
            switch (m.what) {
                case CLEAR_FOCUS:
                    if (tempView != null) {
                        tempView.clearFocus();
                        tempView = null;
                    }
                    break;
            }
        }
    };

    private class SoftKeyboardChangesThread extends Thread {
        private AtomicBoolean started;
        @Nullable
        private SoftKeyboardShowCallback mShowCallback;
        @Nullable
        private SoftKeyboardHideCallback mHideCallback;

        public SoftKeyboardChangesThread() {
            started = new AtomicBoolean(true);
        }

        public void setShowCallback(SoftKeyboardShowCallback mCallback) {
            this.mShowCallback = mCallback;
        }

        public void setHideCallback(SoftKeyboardHideCallback callback) {
            mHideCallback = callback;
        }

        @Override
        public void run() {
            while (started.get()) {
                // Wait until keyboard is requested to open
                synchronized (this) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                int currentBottomLocation = getLayoutCoordinates();

                // There is some lag between open soft-keyboard function and when it really appears.
                while (currentBottomLocation == layoutBottom && started.get()) {
                    currentBottomLocation = getLayoutCoordinates();
                }

                if (mShowCallback != null && started.get()) {
                    mShowCallback.onSoftKeyboardShow();
                    mShowCallback = null;
                }

                // When keyboard is opened from EditText, initial bottom location is greater than layoutBottom
                // and at some moment equals layoutBottom.
                // That broke the previous logic, so I added this new loop to handle this.
                while (currentBottomLocation >= layoutBottom && started.get()) {
                    currentBottomLocation = getLayoutCoordinates();
                }

                // Now Keyboard is shown, keep checking layout dimensions until keyboard is gone
                while (currentBottomLocation != layoutBottom && started.get()) {
                    synchronized (this) {
                        try {
                            wait(500);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    currentBottomLocation = getLayoutCoordinates();
                }

                if (mHideCallback != null && started.get()) {
                    mHideCallback.onSoftKeyboardHide();
                    mHideCallback = null;
                }

                // if keyboard has been opened clicking and EditText.
                if (isKeyboardShow && started.get())
                    isKeyboardShow = false;

                // if an EditText is focused, remove its focus (on UI thread)
                if (started.get())
                    mHandler.obtainMessage(CLEAR_FOCUS).sendToTarget();
            }
        }

        public void keyboardOpened() {
            synchronized (this) {
                notify();
            }
        }

        public void stopThread() {
            synchronized (this) {
                started.set(false);
                notify();
            }
        }

    }
}