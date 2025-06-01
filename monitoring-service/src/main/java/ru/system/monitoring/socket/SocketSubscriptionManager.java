package ru.system.monitoring.socket;

import org.springframework.stereotype.Component;
import reactor.core.Disposable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SocketSubscriptionManager {
    // Храним подписки: sessionId -> Disposable (отписка)
    private final Map<String, Map<String, Disposable>> subscriptions = new ConcurrentHashMap<>();


    /**
     * Добавить подписку для сессии и топика.
     * Если подписка на этот топик уже есть — заменяет её.
     */
    public void subscribe(String sessionId, String topic, Disposable subscription) {
        subscriptions
                .computeIfAbsent(sessionId, k -> new ConcurrentHashMap<>())
                .compute(topic, (key, oldSubscription) -> {
                    if (oldSubscription != null && !oldSubscription.isDisposed()) {
                        oldSubscription.dispose();
                    }
                    return subscription;
                });
    }

    /**
     * Удалить подписку на топик для сессии.
     */
    public void unsubscribe(String sessionId, String topic) {
        Map<String, Disposable> sessionSubs = subscriptions.get(sessionId);
        if (sessionSubs == null) {
            return;
        }
        Disposable subscription = sessionSubs.remove(topic);
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
        if (sessionSubs.isEmpty()) {
            subscriptions.remove(sessionId);
        }
    }

    /**
     * Очистить все подписки для сессии.
     */
    public void cleanupSession(String sessionId) {
        Map<String, Disposable> sessionSubs = subscriptions.remove(sessionId);
        if (sessionSubs != null) {
            sessionSubs.values().forEach(sub -> {
                if (!sub.isDisposed()) {
                    sub.dispose();
                }
            });
        }
    }

    /**
     * Проверить, есть ли подписки у сессии.
     */
    public boolean hasSubscriptions(String sessionId) {
        Map<String, Disposable> sessionSubs = subscriptions.get(sessionId);
        return sessionSubs != null && !sessionSubs.isEmpty();
    }
}
