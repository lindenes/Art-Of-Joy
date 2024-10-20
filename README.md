# Art Of Joy

## Описание
Art Of Joy — это интернет магазин. 
Он предназначен для использование в качестве backend сервера для интернет магазина.
Автоматически создает нужные таблицы.

## Требования

1.**База данных PostgreSQL**

## Установка

1. **Клонируйте репозиторий:**

    ```bash
    git clone https://github.com/lindenes/Art-Of-Joy.git
    ```

2. **Перейдите в директорию проекта:**

    ```bash
    cd Art-Of-Joy
    ```

3. **Соберите Docker образ:**

    ```bash
    docker build -t art_of_joy_back .
    ```

4. **Запустите контейнер:**

   Используйте следующую команду для запуска контейнера. Замените все значения переменных окружения на ваши реальные данные.

    ```bash
    docker run -d --name art_of_joy_back --network host \
        --label io.portainer.accesscontrol.teams=developer \
        -e JOY_DB_URL=db_url \
        -e JOY_DB_PORT=db_port \
        -e JOY_DB_NAME=db_name \
        -e JOY_DB_USER=db_user \
        -e JOY_DB_PASSWORD=db_password \
        -e JOY_HOST=server_ip \
        -e JOY_THREAD_SIZE=server_max_thread \
        -e JOY_PORT=server_port \
        -e JOY_TIMEOUT=server_response_timeout \
        -e JOY_SMTP_URL=smtp_url \
        -e JOY_SMTP_PORT=smtp_port \
        -e JOY_SMTP_USER=smtp_user \
        -e JOY_SMTP_PASSWORD=smtp_password \
        -e JOY_SMTP_AUTH=true \
        -e JOY_SMTP_STARTTLS=true \
        -e JOY_SMTP_EMAIL=smtp_email \
        art_of_joy_back
    ```

## Переменные окружения
Вот список всех переменных окружения, которые вы можете настроить:

- `JOY_DB_URL`: URL вашей базы данных.
- `JOY_DB_PORT`: Порт базы данных.
- `JOY_DB_NAME`: Имя базы данных.
- `JOY_DB_USER`: Имя пользователя для доступа к базе данных.
- `JOY_DB_PASSWORD`: Пароль для доступа к базе данных.
- `JOY_HOST`: IP адрес вашего сервера.
- `JOY_THREAD_SIZE`: Максимальное количество потоков выделяемое сервером
- `JOY_PORT`: Порт, на котором будет работать приложение.
- `JOY_TIMEOUT`: Тайм-аут для соединений.
- `JOY_SMTP_URL`: URL вашего SMTP сервера.
- `JOY_SMTP_PORT`: Порт вашего SMTP сервера.
- `JOY_SMTP_USER`: Имя пользователя для SMTP.
- `JOY_SMTP_PASSWORD`: Пароль для SMTP.
- `JOY_SMTP_AUTH`: Использовать ли аутентификацию SMTP (true/false).
- `JOY_SMTP_STARTTLS`: Использовать ли STARTTLS (true/false).
- `JOY_SMTP_EMAIL`: Email для отправки.

## Использование
После успешного запуска контейнера, вы сможете получить доступ к приложению по адресу `http://server_ip:server_port`.