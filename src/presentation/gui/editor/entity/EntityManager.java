package presentation.gui.editor.entity;

import presentation.controllers.WorldController;
import presentation.gui.editor.Editor;
import presentation.objects.Entity;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Manages entities used by Editor
 */
public class EntityManager {

    /**
     * Every Entity has a EntityPanel per Editor
     */
    private HashMap<Entity, HashMap<Editor, EntityPanel>> entities;

    private WorldController worldController;

    public EntityManager(WorldController worldController) {

        this.worldController = worldController;

        entities = new HashMap<>(0);
    }

    /**
     * Update the entities, will check which ones can be created, updated and destroyed.
     * Reuses existing panels as much as possible.
     */
    public synchronized void updateEntities() {

        Entity[] inputEntities = worldController.getWorldData().getEntities();
        HashMap<Entity, HashMap<Editor, EntityPanel>> newEntities = new HashMap<>(inputEntities.length);

        for (Entity inputEntity : inputEntities) {

            if (entities.containsKey(inputEntity))
                moveEntity(inputEntity, newEntities);
            else
                createEntity(inputEntity, newEntities);
        }

        Iterator<Entity> entitiesToBeRemoved = entities.keySet().iterator();

        while (entitiesToBeRemoved.hasNext())
            removeEntity(entitiesToBeRemoved.next());

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

        Iterator<EntityPanel> entityPanelIterator = panelHashMap.values().iterator();

        while (entityPanelIterator.hasNext())
            entityPanelIterator.next().setEntity(entity);
    }

    /**
     * Creates EntityPanels for every Editor and adds them to the newEntities hashmap
     * @param entity The entity to be added
     * @param newEntities The hashmap where the new entities are added to
     */
    private void createEntity(Entity entity, HashMap<Entity, HashMap<Editor, EntityPanel>> newEntities) {

        HashMap<Editor, EntityPanel> panelHashMap = new HashMap<>(worldController.getEditors().size());
        newEntities.put(entity, panelHashMap);
        Iterator<Editor> editorIterator = worldController.getEditors().iterator();

        while (editorIterator.hasNext()) {

            Editor editor = editorIterator.next();

            EntityPanel panel = new EntityPanel(editor, entity);
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

        Iterator<Entity> entityIterator = entities.keySet().iterator();
        Iterator<HashMap<Editor, EntityPanel>> entityPanelIterator = entities.values().iterator();

        while (entityIterator.hasNext()) {

            Entity entity = entityIterator.next();

            EntityPanel panel = new EntityPanel(editor, entity);
            editor.setLayer(panel, Editor.ENTITY_INDEX);
            editor.add(panel);

            entityPanelIterator.next().put(editor, panel);
        }

        checkVisibility(editor);
    }

    /**
     * Checks whether the entities shoiuld be displayed in the current layer
     * @param editor The editor that needs its entities checked (typically a layer change)
     */
    public void checkVisibility(Editor editor) {

        Iterator<HashMap<Editor, EntityPanel>> panelHashMapIterator = entities.values().iterator();

        while (panelHashMapIterator.hasNext()) {

//            HashMap<Editor, EntityPanel> panelHashMap = panelHashMapIterator.next();

//            if (panelHashMap.containsKey(editor))
                panelHashMapIterator.next().get(editor).checkVisibility();
        }
    }
}
