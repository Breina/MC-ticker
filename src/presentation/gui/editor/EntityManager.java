package presentation.gui.editor;

import logging.Log;
import presentation.objects.Entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

/**
 * Manages entities used by Editor
 */
public class EntityManager {

    /**
     * The entities that will be drawn
     */
    private HashMap<UUID, EntityPanel> entities;

    private Editor editor;

    public EntityManager(Editor editor) {

        this.editor = editor;

        entities = new HashMap<>(0);
    }

    /**
     * Update the entities, will check which ones can be created, updated and destroyed
     */
    public void updateEntities() {

        Entity[] inputEntities = editor.getWorldController().getWorldData().getEntities();

        HashMap<UUID, EntityPanel> newEntities = new HashMap<>(inputEntities.length);

        for (UUID uuid : newEntities.keySet())
            Log.i(uuid.toString());

        for (Entity e : inputEntities) {

            UUID uuid = e.getUUID();
            EntityPanel panel;

            if (entities.containsKey(uuid)) {

                // Panels to be moved
                panel = entities.remove(uuid);
                panel.setEntity(e);

            } else {

                // Panels to be created
                panel = new EntityPanel(editor, e);
                editor.setLayer(panel, Editor.ENTITY_INDEX);
                editor.add(panel);
            }

            newEntities.put(uuid, panel);
        }

        // Panels to be removed
        Iterator<EntityPanel> entitiesToBeRemoved = entities.values().iterator();

        while (entitiesToBeRemoved.hasNext())
            editor.remove(entitiesToBeRemoved.next());

        entities = newEntities;
    }

    /**
     * Checks whether the entities shoiuld be displayed in the current layer
     */
    public void checkVisibility() {

        Iterator<EntityPanel> panelIterator = entities.values().iterator();

        while (panelIterator.hasNext())
            panelIterator.next().checkVisibility();
    }
}
