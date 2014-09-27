package es.eucm.ead.repobuilder;

import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import es.eucm.ead.engine.mock.MockApplication;
import es.eucm.ead.repobuilder.es.eucm.ead.repobuilder.libs.FreepikNature;
import es.eucm.ead.repobuilder.es.eucm.ead.repobuilder.libs.MockupIconsLib;
import es.eucm.ead.repobuilder.es.eucm.ead.repobuilder.libs.Monejos;
import es.eucm.ead.repobuilder.es.eucm.ead.repobuilder.libs.VectorCharacters;
import es.eucm.ead.schema.editor.components.repo.Library;
import es.eucm.ead.schema.editor.components.repo.Repo;

import java.util.Calendar;

/**
 * Created by Javier Torrente on 26/09/14.
 */
public class BuildRepoLibs {

    private static final String [] LIBRARIES_IN_REPO = {MockupIconsLib.class.getName(), FreepikNature.class.getCanonicalName(), VectorCharacters.class.getName(), Monejos.class.getName()};

    public static void main(String[]args){
        String outDir=null;
        String[] librariesToExport = null;

        for (int i=0;i<args.length;i++){
            if (args[i]==null){
                continue;
            }

            String arg = args[i].toLowerCase();
            if (arg.equals("/?") || arg.equals("/h") || arg.equals("-h") || arg.equals("-help")){
                usage();
            } else if (arg.equals("-out")){
                if (i+1<args.length && args[i+1]!=null){
                    outDir = args[i+1];
                } else {
                    System.err.println("[Error] Valid directory path expected after -out");
                }
            }  else if (arg.equals("-libs")){
                if (i+1<args.length && args[i+1]!=null){
                    librariesToExport = args [i+1].split(",");
                } else {
                    System.err.println("[Error] Comma-separated lib (classes) names expected after -libs");
                }
            }  else if (arg.equals("-all")){
                librariesToExport = LIBRARIES_IN_REPO;
            }
        }

        if (outDir==null){
            System.err.println("[Error] No valid directory path defined");
            usage();
            return;
        }
        else if (librariesToExport==null){
            System.err.println("[Error] No libraries selected to export");
            usage();
            return;
        }

        String version = version();

        if (!outDir.endsWith("/") && !outDir.endsWith("\\")){
            outDir+="/"+version+"/";
        } else {
            outDir+=version+"/";
        }

        LwjglNativesLoader.load();
        MockApplication.initStatics();
        System.out.println();

        Repo repo = new Repo();
        int nitems = 0;
        for (int i=0; i<librariesToExport.length; i++){
            String libName = librariesToExport[i];
            Class clazz = null;
            try {
                clazz = Class.forName(libName);
                RepoLibraryBuilder libBuilder = (RepoLibraryBuilder)(clazz.newInstance());
                libBuilder.setCommonProperty(RepoLibraryBuilder.VERSION, version);
                long time = System.currentTimeMillis();

                System.out.println("Exporting library "+(i+1)+"/"+librariesToExport.length+": "+libName+" ............");
                libBuilder.export(outDir);
                nitems+=libBuilder.getNumberOfItems();
                System.out.println("Library "+(i+1)+"/"+librariesToExport.length+": "+libName+" exported in "+(System.currentTimeMillis()-time)/1000F +" seconds.");
                System.out.println();

                Library lib=new Library();
                lib.setVersion(version);
                lib.setPath(libBuilder.getRoot());
                repo.getLibraries().add(lib);

            } catch (ClassNotFoundException e) {
                System.err.println("[Error] Could not load library: "+libName);
            } catch (InstantiationException e) {
                System.err.println("[Error] Could not load instantiate: "+libName);
            } catch (IllegalAccessException e) {
                System.err.println("[Error] Could not load instantiate: " + libName);
            }
        }

        System.out.println("Saving libraries.json");
        Json json = new Json();
        json.toJson(repo, Repo.class, new FileHandle(outDir).child("libraries.json"));
        System.out.println("Export complete!!! "+ nitems+ " elements in the repository");
    }

    private static void usage(){
        System.out.println("***********************");
        System.out.println("******* USAGE *********");
        System.out.println("***********************");

        System.out.println("BuildRepoLibs -out output/directory -libs LIBS_TO_GENERATE");
        System.out.println("\tWhere LIBS_TO_GENERATE:");
        System.out.println("\t\t-all\t\tGenerate the whole repo");
        System.out.println("\t\tClassName1,ClassName2...ClassNameN\t\tGenerate libs associated to those class names");
    }

    private static String version(){
        return fillDigits(Integer.toString(Calendar.getInstance().get(Calendar.YEAR)), 4)+
                fillDigits(Integer.toString(Calendar.getInstance().get(Calendar.MONTH)+1), 2)+
                fillDigits(Integer.toString(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)), 2)+
                fillDigits(Integer.toString(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)), 2);
    }

    private static String fillDigits(String str, int places){
        while (str.length()<places){
            str="0"+str;
        }
        return str;
    }
}
