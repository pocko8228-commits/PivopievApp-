# Pivopiev Android App

Приложение за Android, което показва видеата от YouTube канала **Pivopiev** в grid формат (най-новите отгоре).

---

## Стъпка 1 — Вземи YouTube Data API v3 ключ (безплатно)

1. Отиди на https://console.cloud.google.com
2. Влез с Google акаунт → **New Project** → дай му произволно име
3. В менюто вляво: **APIs & Services → Library**
4. Търси "YouTube Data API v3" → **Enable**
5. **APIs & Services → Credentials → Create Credentials → API Key**
6. Копирай ключа (изглежда така: `AIzaSy...`)

> ⚠️ Безплатният quota е 10 000 unit/ден — напълно достатъчен за лична употреба.

---

## Стъпка 2 — Постави API ключа в кода

Отвори файла:
```
app/src/main/java/com/pivopiev/app/MainActivity.kt
```

Намери реда:
```kotlin
private val API_KEY = "PASTE_YOUR_API_KEY_HERE"
```

Замени `PASTE_YOUR_API_KEY_HERE` с твоя ключ:
```kotlin
private val API_KEY = "AIzaSyXXXXXXXXXXXXXXXXXX"
```

---

## Стъпка 3 — Инсталирай Android Studio

1. Изтегли от https://developer.android.com/studio
2. Инсталирай нормално

---

## Стъпка 4 — Отвори проекта

1. Android Studio → **Open** → избери папката `PivopievApp`
2. Изчакай Gradle sync (1-2 мин при първото отваряне)

---

## Стъпка 5 — Пусни на телефона си

### Включи Developer Mode на телефона:
1. Настройки → За телефона → Номер на версията (натискай 7 пъти)
2. Настройки → Опции за програмисти → **USB debugging** → ON

### Свържи телефона с USB кабел

### В Android Studio:
- Избери телефона от падащото меню горе (до бутона ▶)
- Натисни **▶ Run** (или Shift+F10)

Приложението се инсталира и стартира автоматично!

---

## Функции

- ✅ Grid с 2 колони (3 на таблет)
- ✅ Най-новите видеа отгоре
- ✅ Безкрайно скролване (зарежда още 50 видеа)
- ✅ Pull-to-refresh (дръпни надолу за обновяване)
- ✅ Клик → отваря YouTube app (или браузър ако няма YT)
- ✅ Тъмен дизайн в стил YouTube

---

## Структура на проекта

```
app/src/main/java/com/pivopiev/app/
├── MainActivity.kt       ← главен екран + конфигурация
├── MainViewModel.kt      ← логика за зареждане
├── VideoRepository.kt    ← YouTube API заявки
├── YouTubeApiService.kt  ← Retrofit интерфейс
├── VideoAdapter.kt       ← RecyclerView адаптер
└── Models.kt             ← data класове
```
