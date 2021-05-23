package sample.Server;

/**
 * Сервис авторизации
 */
public interface AuthService {
    /**
     * Запустить сервис
     */
    void start();

    /**
     * Остановить сервис
     */
    void stop();

    /**
     * Получить name
     * @param login
     * @param pass
     * @return
     */
    String getNickByLoginAndPass(String login, String pass);
}
