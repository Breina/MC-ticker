package presentation.gui.windows.main.options;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class OptionsController {

    private Multimap<String, IPreferenceChangedListener> listeners;

    public OptionsController() {
        listeners = ArrayListMultimap.create();
    }

    public void registerPreferenceListener(String preference, IPreferenceChangedListener listener) {
        listeners.put(preference, listener);
    }

    public void notifyListeners(String preference) {
        for (IPreferenceChangedListener listener : listeners.get(preference))
            listener.preferenceChanged(preference);
    }
}
