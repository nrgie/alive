/*
 * Copyright (C) 2015 takahirom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blueobject.app.alive.helper;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

import com.blueobject.app.alive.Global;

public class NestedWebView extends WebView implements NestedScrollingChild {
    private int mLastY;
    private final int[] mScrollOffset = new int[2];
    private final int[] mScrollConsumed = new int[2];
    private int mNestedOffsetY;
    private NestedScrollingChildHelper mChildHelper;

    public NestedWebView(Context context) {
        this(context, null);
    }

    public NestedWebView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.webViewStyle);
    }

    public NestedWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean returnValue = false;

        MotionEvent event = MotionEvent.obtain(ev);
        final int action = MotionEventCompat.getActionMasked(event);
        if (action == MotionEvent.ACTION_DOWN) {
            mNestedOffsetY = 0;
        }
        int eventY = (int) event.getY();
        event.offsetLocation(0, mNestedOffsetY);
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                int deltaY = mLastY - eventY;
                // NestedPreScroll
                if (dispatchNestedPreScroll(0, deltaY, mScrollConsumed, mScrollOffset)) {
                    deltaY -= mScrollConsumed[1];
                    mLastY = eventY - mScrollOffset[1];
                    event.offsetLocation(0, -mScrollOffset[1]);
                    mNestedOffsetY += mScrollOffset[1];
                }
                returnValue = super.onTouchEvent(event);

                // NestedScroll
                if (dispatchNestedScroll(0, mScrollOffset[1], 0, deltaY, mScrollOffset)) {
                    event.offsetLocation(0, mScrollOffset[1]);
                    mNestedOffsetY += mScrollOffset[1];
                    mLastY -= mScrollOffset[1];
                }
                break;
            case MotionEvent.ACTION_DOWN:
                returnValue = super.onTouchEvent(event);
                mLastY = eventY;
                // start NestedScroll
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                returnValue = super.onTouchEvent(event);
                // end NestedScroll
                stopNestedScroll();
                break;
        }
        return returnValue;
    }

    // Nested Scroll implements
    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed,
                                        int[] offsetInWindow) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }
    
    public static String prepareHeadlessView(String html) {
        return prepareHeadlessView(html, "");
    }

    public static String prepareHeadlessView(String html, String customcss) {

        /*
        html = html.replaceAll("href=\"/", "href=\""+ DataManager.wpapi + "/");
        html = html.replaceAll("href='/", "href='"+ DataManager.wpapi + "/");


        String css = "#masthead,footer { display:none !important; } #page { top:0 !important; overflow-x:hidden; } ";
        css += ".mentor-in-post, .invite-widget { display:none; }";
        css += ".calculators-widget .headline-container svg.ribbon-left { left: 7% !important; } .calculators-widget .headline-container svg.ribbon-right { right: 7%!important; }";
        css += ".mentor .video-container { width:70vw!important; padding:0!important; position:inherit!important; overflow:auto!important; height:auto!important; }";
        css += ".mentor iframe { position:inherit!important; width:69vw!important; margin:0 auto!important; }";
        css += ".facebook-widget, .cafe-blog-post-widget, .invite-widget, .recipe-reload-wrapper { display:none!important; }";

        css +=  ".appheader { position:relative; display:block; width:100%; margin:60px 0px;height:50px;font-family:'Anton', sans-serif; font-size:32px; line-height:50px; color:#FFF; text-align:left !important;}" +
                ".appheader .program_icon { width:100px !important; height:100px !important; margin-left:15px;margin-right:15px; margin-top:-25px; float:left; }" +
                ".appheader .program_sponsor { width:auto; height:35px; margin-top:7px; position:absolute; right:0px; }" +
                ".appcontent { padding:0px 15px; } " +
                ".appcontent img, .appcontent  iframe { max-width:100% !important; width:100% !important; height:auto !important; }";

        String domain = DataManager.wpapi;

        String re = "";
        re += "<!DOCTYPE html>";
        re += "<html>";
        re += "<head>";
        re += "<meta charset='UTF-8'/>";
        re += "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />";
        re += "<meta name='viewport' content='width=device-width, initial-scale=1'/>";
        re += "<link rel='stylesheet' id='nep-bootstrap-css'  href='"+domain+"/themes/nep/assets/css/bootstrap.min.css' type='text/css' media='all' />";
        re += "<link rel='stylesheet' id='nep-bootstrap-theme-css'  href='"+domain+"/themes/nep/assets/css/bootstrap-theme.min.css' type='text/css' media='all' />";
        re += "<link rel='stylesheet' id='google-fonts-css'  href='https://fonts.googleapis.com/css?family=Anton%7CCourgette%7CRoboto+Condensed&#038;' type='text/css' media='all' />";
        re += "<link rel='stylesheet' id='nep-style-css'  href='"+domain+"/themes/nep/assets/css/style.css' type='text/css' media='all' />";

        re += "<style>"+css+"</style>";


        //re += "<script>function load(){ var myScroll = new IScroll('#momentum'); }</script>";

        re += "</head>";
        re += "<body>";
        re += "<div id='momentum'>";
        re += html;
        re += "</div>";
        re += "<script type='text/javascript' src='"+domain+"/themes/nep/assets/js/jquery-3.2.0.min.js?ver=3.2.0'></script>";
        re += "<script type='text/javascript' src='"+domain+"/themes/nep/assets/js/bootstrap.min.js?ver=3.3.7'></script>";
        re += "<script type='text/javascript' src='"+domain+"/themes/nep/assets/js/bootstrap.min.js?ver=3.3.7'></script>";
        re += "<script type='text/javascript' src='"+domain+"/themes/nep/assets/js/iscroll-lite.js'></script>";
        re += "<script type='text/javascript' src='"+domain+"/themes/nep/assets/js/search_ui.js?ver=4.7.2'></script>";
        re += "<script type='text/javascript' src='"+domain+"/themes/nep/assets/js/mobil-navigation.js?ver=4.7.2'></script>";
        re += "<script type='text/javascript' src='https://use.fontawesome.com/7f76403dce.js?ver=4.7.2'></script>";
        re += "</body>";
        re += "</html>";
    */
        return html;

    }

}