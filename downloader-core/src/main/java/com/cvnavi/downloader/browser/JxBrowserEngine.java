package com.cvnavi.downloader.browser;

import com.cvnavi.downloader.util.ResourceReader;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.engine.RenderingMode;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

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
            Properties p= ResourceReader.readProperties("jxbrowser.properties");
            System.setProperty("jxbrowser.license.key",p.getProperty("jxbrowser.license.key"));
        } catch (IOException e) {
            e.printStackTrace();
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
