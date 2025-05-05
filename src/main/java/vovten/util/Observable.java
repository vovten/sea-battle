package vovten.util;


/**
 * Created by Admin on 09.02.2015.
 */
public interface Observable {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers();
    void notifyObservers(Object arg);
}
