# OpenAiTelegramBot 🤖

OpenAiTelegramBot — это телеграм-бот, использующий OpenAI GPT и Whisper для общения, преобразования голосовых сообщений в текст и генерации изображений.
Бот реализован на Java с использованием Spring Boot, интеграции с Telegram Bot API, OpenAI API и ffmpeg для обработки аудио.

---

## 🔧 Технологии и версии

| Технология             | Версия   |
| ---------------------- | -------- |
| Java                   | 24       |
| Spring Boot            | 3.4.5    |
| Spring Cloud OpenFeign | 4.2.1    |
| TelegramBots           | 6.9.7.1  |
| OpenAI Java SDK        | 0.18.2   |
| ModelMapper            | 3.2.3    |
| PostgreSQL Driver      | runtime  |
| Liquibase              | latest   |
| Lombok                 | optional |
| ffmpeg (net.bramp)     | 0.8.0    |

---

## 💡 Основной функционал

| Команда          | Назначение                                                           |
| ---------------- | -------------------------------------------------------------------- |
| `/gpt`           | Чат с GPT. Сохраняет до 15 сообщений в истории. После 15 идёт сброс. |
| `/gpt_reset`     | Сброс диалога. Удаляет память GPT.                                   |
| `/translator`    | Перевод голосовых сообщений в текст.                                 |
| `/setting_voice` | Выбор режима: транскрипция или перевод (русский -> английский).      |
| `/image`         | Генерация изображения по текстовому запросу.                         |
| `/setting_image` | Выбор размера генерируемых изображений.                              |
| `/commands`      | Список доступных команд.                                             |
| `/info`          | Информация о проекте.                                                |
| `/feedback`      | Форма обратной связи. Сохранение в БД.                               |

---

## 🌐 Создание Telegram-бота

1. Перейди к [@BotFather](https://t.me/botfather).
2. Создай нового бота: `/newbot`
3. Задай имя и username (должен оканчиваться на `Bot`, например `MyChatGPTBot`)
4. Скопируй Token и вставь его в `APP_TELEGRAM_BOT_TOKEN`
5. Скопируй Username и вставь в `APP_TELEGRAM_BOT_USERNAME`
6. Добавь команды:

   ```
   /gpt - Чат с GPT
   /gpt_reset - Сброс диалога
   /translator - Голос в текст
   /setting_voice - Модель перевода голоса
   /image - Генерация изображений
   /setting_image - Размер изображения
   /commands - Список команд
   /info - О проекте
   /feedback - Обратная связь
   ```

---

## 🔐 Получение OpenAI API ключа

1. Перейди на [https://platform.openai.com/account/api-keys](https://platform.openai.com/account/api-keys)
2. Нажми **"Create new secret key"**
3. Скопируй ключ и вставь его в `APP_OPEN_AI_TOKEN`

---

## 📚 Конфигурация проекта

### Файлы с настройками

* `application-secret.properties` — приватные данные, должен быть в `.gitignore`
* `application-example.properties` — шаблон с полями для заполнения

### Создание `.secret` копии

```bash
cp src/main/resources/application-example.properties src/main/resources/application-secret.properties
```

---

## 📊 База данных

* Используется PostgreSQL
* Liquibase автоматически создаёт необходимые таблицы при первом запуске проекта

---

## ✅ Быстрый старт

1. Скопируй `application-example.properties` → `application-secret.properties`
2. Заполни нужные значения (ключи Telegram и OpenAI, пароль к БД)
3. Убедись, что PostgreSQL запущен и создана база `telegram_openai_bot`
4. Запусти проект:

```bash
./mvnw spring-boot:run
```

---

## ⚠️ Важно

Никогда не добавляй `application-secret.properties` в git.  
Файл **должен быть в `.gitignore`**:

```
src/main/resources/application-secret.properties
```

---

## 📫 Обратная связь

Пользователь может оставить отзыв с помощью команды `/feedback`.  
Форма обратной связи сохраняется в БД и может быть использована для улучшения функциональности.

---

## 💸 Поддержать проект

Если бот оказался полезен и вы хотите поддержать его развитие:

**СБЕР:** +7 (999) 775‑70‑50  
(переводы по номеру телефона)

Спасибо за вашу поддержку!