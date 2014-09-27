package es.eucm.ead.repobuilder.es.eucm.ead.repobuilder.libs;

        import es.eucm.ead.repobuilder.RepoLibraryBuilder;
        import es.eucm.ead.schema.editor.components.repo.RepoLicense;

/**
 * Created by Javier Torrente on 25/09/14.
 */
public class VectorCharacters extends RepoLibraryBuilder {
    public VectorCharacters() {
        super("vectorcharacters");
    }

    @Override
    protected void doBuild() {
        setCommonProperty(MAX_WIDTH, "650");
        setCommonProperty(MAX_HEIGHT, "650");
        setCommonProperty(TAGS, "VectorCharacters,animated;animado,characters;personajes,cartoon");
        setCommonProperty(AUTHOR_NAME, "VectorCharacters");
        setCommonProperty(AUTHOR_URL, "http://vectorcharacters.net/");
        setCommonProperty(LICENSE, RepoLicense.LINK_AUTHOR.toString());

        float d1 = 0.125F;

        // Blue monster
        repoEntity("Funky monster", "Monstruo azul", "","","blue.png",null).tag("monster","monstruo").tag("ghost","fantasma").tag("sing","cantar").tag("dance","bailar").tag("music","música");
        for (int i=0; i<4; i++){
            frame("blue_01.png", d1).frame("blue_04.png", d1).frame("blue_02.png", d1).frame("blue_03.png", d1);
        }
        frame("blue_06.png", d1).frame("blue_05.png", d1).frame("blue_06.png", d1).frame("blue_08.png", d1).frame("blue_07.png", d1).frame("blue_08.png", d1);
        adjustEntity(getLastEntity());

        // Evil monster
        repoEntity("Evil monster", "Monstruo malvado", "", "", "evil.png",null).tag("monster", "monstruo").tag("blink","parpadear");
        float blink = 0.12F;
        float normal = 4;
        frame("evil_01.png", normal).frame("evil_02.png",blink).
                frame("evil_01.png", normal).frame("evil_02.png",blink).
                frame("evil_01.png", normal).frame("evil_02.png",blink).
                frame("evil_03.png", normal).frame("evil_02.png",blink).
                frame("evil_03.png", normal).frame("evil_02.png",blink).adjustEntity(getLastEntity());

        // Red monster
        repoEntity("Red monster", "Monstruo rojo", "", "", "red.png", null).tag("monster", "monstruo");
        frame("red_01.png",d1).frame("red_02.png",d1).frame("red_01.png",d1).frame("red_03.png",d1).frame("red_04.png",d1).frame("red_03.png",d1).frame("red_01.png",d1).frame("red_05.png",d1).frame("red_06.png",d1).frame("red_05.png",d1).frame("red_01.png",d1).adjustEntity(getLastEntity());

        repoLib("VectorCharacters.net", "VectorCharacters.net", "Cartoon characters from VectorCharacters", "Personajes cartoon de VectorCharacters", "vectorcharacters.png");

    }

}
