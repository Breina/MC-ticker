package presentation.gui.editor.entity;

import presentation.controllers.MainController;
import presentation.controllers.WorldController;
import presentation.gui.editor.Editor;
import presentation.objects.Entity;

import java.util.*;

/**
 * Manages entities used by Editor
 */
public class EntityManager {

    /**
     * Every Entity has a EntityPanel per Editor
     */
    private Map<Entity, HashMap<Editor, EntityPanel>> entities;

    private final MainController mainController;
    private final WorldController worldController;

    private List<Editor> editors;

    public EntityManager(WorldController worldController) {

        this.worldController = worldController;
        this.mainController = worldController.getMainController();

        entities = new HashMap<>(0);
        editors = new ArrayList<>(0);
    }

    /**
     * Update the entities, will check which ones can be created, updated and destroyed.
     * Reuses existing panels as much as possible.
     */
    public void updateEntities() {

        Entity[] inputEntities = worldController.getWorldData().getEntities();

        HashMap<Entity, HashMap<Editor, EntityPanel>> newEntities = new HashMap<>(inputEntities.length);

        for (Entity inputEntity : inputEntities) {

            if (entities.containsKey(inputEntity))
                moveEntity(inputEntity, newEntities);
            else
                createEntity(inputEntity, newEntities);
        }

        Entity[] entitiesToBeRemoved = entities.keySet().toArray(new Entity[entities.size()]);

//        Iterator<Entity> entitiesToBeRemoved = entities.keySet().iterator();
//        Collection<Entity> entitiesToBeRemoved = entities.keySet();

//        while (entitiesToBeRemoved.hasNext())
//            removeEntity(entitiesToBeRemoved.next());

        for (Entity entityToBeRemoved : entitiesToBeRemoved)
            removeEntity(entityToBeRemoved);

        entities = newEntities;
    }

    /**
     * Updates the EntityPanels for every Editor to the new entity, removes them from the entities list
     * and adds them to the newEntities hashMap
     * @param entity The entity to move
     */
    private void moveEntity(Entity entity, HashMap<Entity, HashMap<Editor, EntityPanel>> newEntities) {

        HashMap<Editor, EntityPanel> panelHashMap = entities.remove(entity);
        newEntities.put(entity, panelHashMap);

        for (EntityPanel entityPanel : panelHashMap.values()) entityPanel.setEntity(entity);
    }

    /**
     * Creates EntityPanels for every Editor and adds them to the newEntities hashmap
     * @param entity The entity to be added
     * @param newEntities The hashmap where the new entities are added to
     */
    private void createEntity(Entity entity, HashMap<Entity, HashMap<Editor, EntityPanel>> newEntities) {

        HashMap<Editor, EntityPanel> panelHashMap = new HashMap<>(editors.size());
        newEntities.put(entity, panelHashMap);

        for (Editor editor : editors) {

            EntityPanel panel = new EntityPanel(mainController, editor, entity);
            editor.setLayer(panel, Editor.ENTITY_INDEX);
            editor.add(panel);

            panelHashMap.put(editor, panel);
        }
    }

    /**
     * Removes an entity from the entities hashMap and from every Editor
     * @param entity The uuid of the entity to be removed
     */
    private void removeEntity(Entity entity) {

        HashMap<Editor, EntityPanel> panelHashMap = entities.remove(entity);

        Iterator<Editor> editorIterator = panelHashMap.keySet().iterator();
        Iterator<EntityPanel> entityPanelIterator = panelHashMap.values().iterator();

        while (editorIterator.hasNext())
            editorIterator.next().remove(entityPanelIterator.next());
    }

    /**
     * Adds every entity to the given editor
     * @param editor The editor to which all entities are added
     */
    public void addEditor(Editor editor) {

        editors.add(editor);

        Iterator<Entity> entityIterator = entities.keySet().iterator();
        Iterator<HashMap<Editor, EntityPanel>> entityPanelIterator = entities.values().iterator();

        while (entityIterator.hasNext()) {

            Entity entity = entityIterator.next();

            EntityPanel panel = new EntityPanel(mainController, editor, entity);
            editor.setLayer(panel, Editor.ENTITY_INDEX);
            editor.add(panel);

            entityPanelIterator.next().put(editor, panel);
        }

        checkVisibility(editor);
    }

    public void removeEditor(Editor editor) {

        editors.remove(editor);

        Iterator<HashMap<Editor, EntityPanel>> entityPanelIterator = entities.values().iterator();

        while (entityPanelIterator.hasNext())
            entityPanelIterator.next().remove(editor);
    }

    /**
     * Checks whether the entities shoiuld be displayed in the current layer
     * @param editor The editor that needs its entities checked (typically a layer change)
     */
    public void checkVisibility(Editor editor) {
        for (HashMap<Editor, EntityPanel> panelHashMap : entities.values())
            panelHashMap.get(editor).checkVisibility();
    }
}
