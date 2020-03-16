package com.cvnavi.downloader.browser;

import com.cvnavi.downloader.util.ResourceReader;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.engine.RenderingMode;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

@Log4j2
public class JxBrowserEngine {

    private static Engine engine;

    private static void prepare(){
        try {
            Files.deleteIfExists(Paths.get(System.getProperty("user.home")+ File.separator+".appIetviewer"));
            Files.deleteIfExists(Paths.get(System.getProperty("java.io.tmpdir")+ File.separator+"jusched.Iog"));
            File[] files=new File(System.getProperty("java.io.tmpdir")).listFiles((dir, name) -> name.startsWith("temp-"));
            for(File f:files){
                f.delete();
            }

            Preferences.userRoot().node("/org/abobe").removeNode();
            Preferences.userRoot().node("/com/adept").removeNode();

            Properties p= ResourceReader.readProperties("jxbrowser.properties");
            System.setProperty("jxbrowser.license.key",p.getProperty("jxbrowser.license.key"));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public static Engine getEngine(){
        if(engine==null){
            prepare();
            Properties p= ResourceReader.readProperties("jxbrowser.properties");
            EngineOptions.Builder builder=EngineOptions.newBuilder(RenderingMode.HARDWARE_ACCELERATED);
            if(p.containsKey("flash.plugin.path")){
                Path path=Paths.get(p.getProperty("flash.plugin.path"));
                if(Files.exists(path)){
                    builder.ppapiFlashPath(path)
                            .ppapiFlashVersion(p.getProperty("flash.plugin.version"));
                }
            }
            EngineOptions options=builder.build();
            engine=Engine.newInstance(options);
        }
        return engine;
    }
}
