package es.eucm.ead.repobuilder;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import es.eucm.ead.editor.importer.EAdventure1XGame;
import es.eucm.ead.editor.importer.EAdventure1XLoader;
import es.eucm.ead.schema.editor.components.repo.I18NStrings;
import es.eucm.ead.schema.editor.components.repo.RepoLicense;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.Frames;
import es.eucm.ead.schema.renderers.Image;
import es.eucm.ead.schema.renderers.State;
import es.eucm.ead.schema.renderers.States;
import es.eucm.eadventure.common.data.animation.Animation;
import es.eucm.eadventure.common.data.animation.Frame;
import es.eucm.eadventure.common.data.chapter.Chapter;
import es.eucm.eadventure.common.data.chapter.elements.NPC;
import es.eucm.eadventure.common.data.chapter.resources.Resources;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Javier Torrente on 29/09/14.
 */
public class EAd1XLibBuilder extends RepoLibraryBuilder{
    private String version;

    public static final String EMPTY_ANIMATION = "EmptyAnimation";

    /**
     * Creates the object but does not actually build the game. Just creates the
     * temp folder and unzips the the contents of the file specified by the
     * relative path {@code root}
     *
     * @param root
     */
    public EAd1XLibBuilder(String root) {
        super(root);
    }

    @Override
    protected void doBuild() {
        // Load game
        EAdventure1XLoader loader = new EAdventure1XLoader();
        EAdventure1XGame game = loader.load(rootFolder.path());
        // Process chapters
        for (Chapter chapter: game.getAdventureData().getChapters()){
            // Process npcs
            /*for (NPC npc:chapter.getCharacters()){
                for (int i=0; i<npc.getResources().size(); i++){
                    for (int j=0; j<npc.getResources().get(i).getAssetCount(); j++){
                        repoNPC(game, npc,i,j );
                    }
                }
            }*/
            // Process player
            for (int i=0; i<chapter.getPlayer().getResources().size(); i++){
                for (int j=0; j<chapter.getPlayer().getResources().get(i).getAssetCount(); j++){
                    repoNPC(game, chapter.getPlayer(),i,j );
                }
            }
        }
    }

    public RepoLibraryBuilder repoNPC(EAdventure1XGame game, NPC npc, int resourcesIndex, int assetIndex) {
        String nameEn = "", nameEs = "", descriptionEs = "", descriptionEn = "", thumbnail = null;
        List<I18NStrings> parsedTags = new ArrayList<I18NStrings>();
        States states = new States();

        // Generate names, descriptions, tags
        if (npc.getDescriptions().size()>0){
            String name = npc.getDescriptions().get(0).getName();
            String tags = npc.getDescriptions().get(0).getDescription();
            String description = npc.getDescriptions().get(0).getDetailedDescription();

            if (name==null || name.length()==0 || !name.contains(";")){
                System.err.println("[Error] Character "+npc.getId()+ " has no valid name: "+name);
            } else {
                nameEn = name.split(";")[0];
                nameEs = name.split(";")[1];
            }

            if (description==null || description.length()==0 || !description.contains(";")){
                System.err.println("[Error] Character "+npc.getId()+ " has no valid description: "+description);
            } else {
                descriptionEn = description.split(";")[0];
                descriptionEs = description.split(";")[1];
            }

            if (tags==null || tags.length()==0 || !tags.contains(";")){
                System.err.println("[Error] Character "+npc.getId()+ " has no valid tags: "+tags);
            } else {
                parsedTags = parseI18NTag(tags);
            }
        }

        // Add resources index to name to differentiate entities from the same npc
        if (resourcesIndex>=0 && npc.getResources().size()>1){
            nameEn+= " "+(resourcesIndex+2);
            nameEs+= " "+(resourcesIndex+2);
        }

        if (assetIndex>=0){
            String assetType = npc.getResources().get(resourcesIndex).getAssetTypes()[assetIndex];
            if (assetType.contains("stand")){
                nameEn+= " standing";
                nameEs+= " parado";
            } else if (assetType.contains("speak")){
                nameEn+= " speaking";
                nameEs+= " hablando";
            } else if (assetType.contains("walk")){
                nameEn+= " walking";
                nameEs+= " andando";
            } else if (assetType.contains("use")){
                nameEn+= " using or grabbing";
                nameEs+= " manipulando objetos";
            }

            if (assetType.contains("left")){
                nameEn+= " left";
                nameEs+= " hacia la izquierda";
            } else if (assetType.contains("right")){
                nameEn+= " right";
                nameEs+= " hacia la derecha";
            } else if (assetType.contains("up")){
                nameEn+= " back";
                nameEs+= " hacia atrás";
            } else if (assetType.contains("down")){
                nameEn+= " to front";
                nameEs+= " hacia el frente";
            }
        }

        // Additional tags from appearance name, if present
        // Also Pick representative image for thumbnail
        Animation selectedAnimation = null;
        int indexOfResource=Integer.MAX_VALUE;
        String[] priorizedResources = new String[]{NPC.RESOURCE_TYPE_STAND_RIGHT, NPC.RESOURCE_TYPE_STAND_LEFT, NPC.RESOURCE_TYPE_STAND_DOWN,
                NPC.RESOURCE_TYPE_SPEAK_RIGHT, NPC.RESOURCE_TYPE_SPEAK_LEFT, NPC.RESOURCE_TYPE_SPEAK_DOWN,
                NPC.RESOURCE_TYPE_WALK_RIGHT, NPC.RESOURCE_TYPE_WALK_LEFT, NPC.RESOURCE_TYPE_WALK_DOWN,
                NPC.RESOURCE_TYPE_USE_RIGHT, NPC.RESOURCE_TYPE_USE_LEFT,
                NPC.RESOURCE_TYPE_STAND_UP, NPC.RESOURCE_TYPE_SPEAK_UP, NPC.RESOURCE_TYPE_WALK_UP};
        int i1=0,i2=0,j1=0,j2=0;
        if (resourcesIndex == 0){
            parsedTags.add(enEsString("full", "completo"));
            parsedTags.add(enEsString("multi state", "multi estado"));
            i1=0;j1=0;
            i2=npc.getResources().size()-1;
            j2=10000;
        } else if (assetIndex == 0){
            parsedTags.add(enEsString("multi state", "multi estado"));
            i1=i2=resourcesIndex;
            j1=0; j2=10000;
        } else {
            i1=i2=resourcesIndex;
            j1=j2=assetIndex;
        }

        // Add tags and states if necessary
        for (int i=i1; i<=i2; i++){
            Resources resources = npc.getResources().get(i);
            Array<String> usedAnimations = new Array<String>();

            // Tags from appName
            String appearanceName = resources.getName();
            if (appearanceName!=null && appearanceName.length()>0 && appearanceName.contains(";")){
                for (I18NStrings parsedTag: parseI18NTag(appearanceName)){
                    parsedTags.add(parsedTag);
                }
            }

            // Tags from resources
            for (int j=j1; j<=Math.min(resources.getAssetCount() - 1, j2); j++){
                String assetType = resources.getAssetTypes()[assetIndex];
                String assetValue = resources.getAssetValues()[assetIndex];

                if (usedAnimations.contains(assetValue, false)){
                    continue;
                }

                State state = makeStateFromAnimation(game, assetValue);
                states.getStates().add(state);

                if (isValidAnimation(game, resources, assetType)){
                    indexOfResource = Math.min(indexOfResource, indexOf(priorizedResources, assetType));
                    selectedAnimation = game.getAnimations().get(resources.getAssetPath(priorizedResources[indexOfResource]));
                    break;
                }

                if (assetType.contains("stand")){
                    parsedTags.add(enEsString("standing", "parado"));
                    fillStateTags(state, "standing", "parado", "idle", "quieto", "stand");
                } else if (assetType.contains("speak")){
                    parsedTags.add(enEsString("speaking", "hablando"));
                    fillStateTags(state, "speaking", "hablando", "speak", "talk", "hablar", "conversar", "conversando", "talking", "conversation", "conversación");
                } else if (assetType.contains("walk")){
                    parsedTags.add(enEsString("walking", "andando"));
                    fillStateTags(state, "walking", "andando", "andar", "caminar", "walk");
                } else if (assetType.contains("use")){
                    parsedTags.add(enEsString("using", "usando"));
                    parsedTags.add(enEsString("grabbing", "agarrando"));
                    fillStateTags(state, "use", "use with", "give", "grab", "take", "usar con", "usar", "usando", "agarrando", "agarrar", "manipular");
                }

                if (assetType.contains("left")){
                    fillStateTags(state, "left", "izquierda");
                } else if (assetType.contains("right")){
                    fillStateTags(state, "right", "derecha");
                } else if (assetType.contains("up")){
                    fillStateTags(state, "up", "arriba", "back");
                } else if (assetType.contains("down")){
                    fillStateTags(state, "down", "abajo", "frente");
                }

            }
        }

        if (selectedAnimation!=null){
            thumbnail = selectedAnimation.getFrame(0).getUri();
        } else {
            System.err.println("[Error] Character "+npc.getId()+ " has no valid thumbnail: "+thumbnail);
        }

        // Create entity with data obtained
        repoEntity(nameEn, nameEs, descriptionEn, descriptionEs, thumbnail, null);
        // Add tags (if any)
        for (I18NStrings tag:parsedTags){
            lastElement.getTags().add(tag);
        }

        return this;
    }

    protected int indexOf (String[]array, String element){
        for (int i=0; i<array.length; i++){
            if (element.equals(array[i])){
                return i;
            }
        }
        return -1;
    }

    protected void fillStateTags(State state, String...tags){
        for (String tag:tags){
            state.getStates().add(tag);
        }
    }

    protected boolean isValidAnimation(EAdventure1XGame game, Resources resources, String assetType){
        if (!resources.existAsset(assetType)){
            return false;
        }

        String assetPath = resources.getAssetPath(assetType);
        return assetPath !=null && assetPath.length()>0 && !assetPath.contains(EMPTY_ANIMATION) &&
                game.getAnimations().get(assetPath)!= null && game.getAnimations().get(assetPath).getFrames().size()>0 &&
                game.getAnimations().get(assetPath).getFrame(0).getUri()!=null && game.getAnimations().get(assetPath).getFrame(0).getUri().length()>0  &&
                !game.getAnimations().get(assetPath).getFrame(0).getUri().contains(EMPTY_ANIMATION);
    }

    public State makeStateFromAnimation(EAdventure1XGame eAdventure1XGame, String animPath) {

        State state = new State();

        Frames frames = new Frames();
        frames.setSequence(Frames.Sequence.LINEAR);
        state.setRenderer(frames);

        // Create frames
        Animation animation = eAdventure1XGame.getAnimations().get(animPath);
        for (Frame frame : animation.getFrames()) {
            es.eucm.ead.schema.renderers.Frame newFrame = new es.eucm.ead.schema.renderers.Frame();
            newFrame.setTime(frame.getTime()/1000F);
            Image image = createImage(frame.getUri());
            newFrame.setRenderer(image);
            frames.getFrames().add(newFrame);
        }

        return state;
    }
}
