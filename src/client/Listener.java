package client;

/**
 * Project: Messenger FX
 *
 * @author Егор Ивков
 * @since 10.11.2017
 */
public interface Listener<T> {
    void receive(T message);
}
