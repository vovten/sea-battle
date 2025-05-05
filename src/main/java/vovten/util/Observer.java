package vovten.util;


/**
 * A class can implement the Observer interface when it
 * wants to be informed of changes in observable objects.
 */
public interface Observer {
    void update(Observable o, Object arg);
}
