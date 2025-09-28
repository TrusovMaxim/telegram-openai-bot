# OpenAiTelegramBot 🤖

> ⚠️ **UPD (01.10.2025)**  
> 🔴 Бот был **отключён**, так как не вышел на стабильную прибыль.  
> 📂 Теперь проект доступен как **открытый исходный код**.  
> 🚀 Развернуть можно без программирования — просто создайте своего бота в Telegram и введите ключи.  
> ⭐️ Если проект вам понравился, поставьте звезду на [GitHub](https://github.com/TrusovMaxim/OpenAiTelegramBot) — это будет лучшей поддержкой!

---

OpenAiTelegramBot — это телеграм-бот на Java (Spring Boot), объединяющий возможности OpenAI GPT и Whisper:

- Общение с ChatGPT
- Распознавание голосовых сообщений
- Генерация изображений по тексту
- Краткие выжимки из YouTube-видео
- Краткие выжимки из файлов
- Безопасно работает в России без VPN 🇷🇺

## 🛰 Живой пример

Реализованный проект на основе этого репозитория доступен в Telegram: [@AIntellioBot](https://t.me/aintelliobot)

---

## 🆓 Бесплатный доступ

- 💬 Чат с GPT (до 20 сообщений в день, без истории)
- 🎧 Перевод голосовых и видеокружков (до 10 голосовых и 1 видеокружок в день)
- ▶️ Обзор одного YouTube-видео в день
- 🖼 Генерация изображений (при наличии токенов)
- ✉️ Отправка отзыва

## 💎 Премиум-доступ (399₽ в месяц)

- 💬 Чат с GPT с сохранением истории (до 15 сообщений)
- 🎧 Безлимитный перевод голосовых и видеокружков
- ▶️ Неограниченная обработка YouTube-видео
- 📄 Безлимитная обработка файлов
- 🖼 Возможность покупки токенов на изображения

🔐 Оформить премиум: [https://t.me/aintelliobot?start=premium](https://t.me/aintelliobot?start=premium)

---

## 🔧 Технологии и версии

| Технология             | Версия   |
|------------------------|----------|
| Java                   | 21       |
| Spring Boot            | 3.4.5    |
| Spring Cloud OpenFeign | 4.2.1    |
| TelegramBots           | 6.9.7.1  |
| OpenAI Java SDK        | 0.18.2   |
| ModelMapper            | 3.2.3    |
| Apache PDFBox          | 2.0.27   |
| PostgreSQL Driver      | runtime  |
| Liquibase              | latest   |
| Lombok                 | optional |
| ffmpeg (net.bramp)     | 0.8.0    |

---

## 💡 Основной функционал

| Команда           | Назначение                                      |
|-------------------|-------------------------------------------------|
| `/gpt`            | Чат с GPT (до 15 сообщений в истории)           |
| `/gpt_reset`      | Сброс диалога GPT                               |
| `/translator`     | Перевод голосовых и видеокружков                |
| `/youtube`        | Обзор YouTube-видео (до 15 минут)               |
| `/image`          | Генерация изображений (1 изображение = 1 токен) |
| `/file_summarize` | Резюме из файла (до 10 страниц)                 |
| `/settings`       | Настройки изображения и перевода                |
| `/buy_images`     | Купить 5 токенов (5 генераций за 199₽)          |
| `/balance`        | Проверить баланс токенов                        |
| `/buy_premium`    | Купить премиум-доступ                           |
| `/info`           | О боте и подписке                               |
| `/feedback`       | Отправить отзыв                                 |
| `/commands`       | Показать команды                                |

---

## 🌐 Создание Telegram-бота

1. Перейди к [@BotFather](https://t.me/botfather).
2. Создай нового бота: `/newbot`
3. Задай имя и username (должен оканчиваться на `Bot`, например `MyChatGPTBot`)
4. Скопируй Token и вставь его в `APP_TELEGRAM_BOT_TOKEN`
5. Скопируй Username и вставь в `APP_TELEGRAM_BOT_USERNAME`
6. Добавь команды:

   ```
   gpt - 💬 Чат с GPT  
   gpt_reset - ♻️ Сброс диалога  
   translator - 🎧 Перевод голосовых и видеокружков  
   youtube - ▶️ Обзор YouTube-видео  
   image - 🖼 Генерация изображений
   file_summarize - 📄 Резюме из файла  
   settings - ⚙️ Настройки  
   buy_images - 💎 Купить 5 токенов  
   balance - 🧾 Баланс токенов  
   buy_premium - 🌟 Купить премиум-доступ  
   info - 💡 О боте и подписке  
   feedback - ✍️ Отправить отзыв  
   commands - 📜 Все команды
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
4. Убедись, что установлен ffmpeg и yt-dlp (**только для macOS**, если ещё не установлен):

### 🛠 Установка ffmpeg и yt-dlp на macOS

```bash
# Установи Command Line Tools (если ещё не установлены)
xcode-select --install

# Установи Homebrew (если ещё не установлен)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Добавь brew в PATH (только один раз)
echo 'eval "$(/opt/homebrew/bin/brew shellenv)"' >> ~/.zprofile
eval "$(/opt/homebrew/bin/brew shellenv)"

# Установи ffmpeg
brew install ffmpeg

# Установи yt-dlp
brew install yt-dlp
```

5. Запусти проект:

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

## 👨‍💻 О разработчике

Проект создан энтузиастом в свободное время.  
Контакты: [@Trusov_Maxim](https://t.me/Trusov_Maxim)

---

## 💸 Поддержать проект

Если бот оказался полезен и вы хотите поддержать его развитие:

**СБЕР:** +7 (999) 775‑70‑50  
(переводы по номеру телефона)

Спасибо за вашу поддержку!