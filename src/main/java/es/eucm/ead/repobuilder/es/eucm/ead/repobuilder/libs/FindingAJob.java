package es.eucm.ead.repobuilder.es.eucm.ead.repobuilder.libs;

import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader;
import es.eucm.ead.engine.mock.MockApplication;
import es.eucm.ead.repobuilder.EAd1XLibBuilder;

/**
 * Created by Javier Torrente on 30/09/14.
 */
public class FindingAJob extends EAd1XLibBuilder{
    public FindingAJob() {
        super("findingajob");
        setCommonProperty(RESOURCES,"");
        setCommonProperty(THUMBNAILS,"");
        setCommonProperty(MAX_WIDTH,"800");
        setCommonProperty(MAX_HEIGHT,"600");
        setCommonProperty(AUTHOR_NAME, "CATEDU/Héctor Montoya Camacho");
        setCommonProperty(AUTHOR_URL, "http://www.catedu.es/webcateduantigua/index.php/descargas/e-adventures");
        setCommonProperty(TAGS, "eAdventure;eAdventure,CATEDU;CATEDU,Finding a job;Finding a job");
    }

    public static void main (String[]args){
        LwjglNativesLoader.load();
        MockApplication.initStatics();

        FindingAJob findingAJob = new FindingAJob();
        findingAJob.export("D:\\Documents\\TRABAJO\\E-ADVENTURE\\Mockup\\Test");
    }
}
