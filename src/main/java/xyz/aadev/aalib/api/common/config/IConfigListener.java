package xyz.aadev.aalib.api.common.config;

public interface IConfigListener {
    /**
     * Notify a listener that if should re-acquire it's own configuration options from the configuration handler
     */
    void onConfigChanged();
}
