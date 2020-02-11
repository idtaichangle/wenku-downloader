package com.cvnavi.downloader;

import com.teamdev.jxbrowser.ui.KeyCode;
import com.teamdev.jxbrowser.ui.Point;
import com.teamdev.jxbrowser.ui.event.KeyPressed;
import com.teamdev.jxbrowser.ui.event.KeyReleased;
import com.teamdev.jxbrowser.ui.event.MouseWheel;

public class DangDangDownloader extends AbstractDownloader {
    @Override
    public String getDocType() {
        return null;
    }

    @Override
    public String getPageName() {
        return "book";
    }

    @Override
    public int getPageCount() {
        return 1;
    }

    @Override
    public void download() throws Exception {
        prepareDownload(null);
        browser.dispatch(MouseWheel.newBuilder(Point.of(0,0)).deltaY(-windowHeight).build());
        browser.dispatch(KeyPressed.newBuilder(KeyCode.KEY_CODE_DOWN).build());
//        Thread.sleep(100);
//        browser.dispatch(KeyReleased.newBuilder(KeyCode.KEY_CODE_DOWN).build());
    }
}
