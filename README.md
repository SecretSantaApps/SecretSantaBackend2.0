# Бекенд для игры «Тайный Санта»

__Стек__: Kotlin, Ktor, Ktorm, Docker, Docker Compose

---

### Подготовка к запуску

```shell
cp .env.sample .env

# отредактировать файл, сконфигурировать JWT-секрет и настройки БД
vim .env
```

---

## Регистрация в OneSignal

1. Зарегистрироваться на сайте [OneSignal](https://onesignal.com/)
2. Создать приложение
3. Получить API-ключ и ID приложения
4. Заполнить соответствующие поля в файле `.env`

---

### Запуск сервера в Docker compose:

```shell
    docker-compose up --build -d
```

---

### Пересборка сервера при необходимости:

```shell
./gradlew shadowJar
```

---

### Остановка сервера:

```shell
docker-compose down
```

### Логи

```shell
docker-compose logs
```

---

## API

```http request
GET /swagger

GET /openapi
```

## WebSockets


### Подписка на обновления комнаты
  
ROOM_ID - идентификатор комнаты
```
  ws://BASE_URL/api/v1/game?id=ROOM_ID
```

---
### Подписка на обновления в списке комнат игрока

  ```
    ws://BASE_URL/api/v1/game/all
  ```