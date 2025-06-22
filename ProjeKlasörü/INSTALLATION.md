# 📥 Ghost Hunter - Kurulum Rehberi (INSTALLATION.md)

Bu belgede, **Ghost Hunter** Android mobil oyununun nasıl kurulacağı, çalıştırılacağı ve olası problemler karşısında nasıl çözümler uygulanabileceği detaylı olarak anlatılmıştır.

---

## 🔧 Sistem Gereksinimleri

| Bileşen              | Gereksinim                     |
|----------------------|--------------------------------|
| İşletim Sistemi      | Windows 10/11, macOS, Linux    |
| Bellek (RAM)         | Minimum 4 GB, önerilen 8 GB+   |
| Depolama Alanı       | Minimum 2 GB boş alan          |
| Java Sürümü          | JDK 17 veya üzeri              |
| Android Studio       | Arctic Fox (2020.3.1) veya üzeri |
| Android SDK          | API 31 (Android 12) veya üzeri |
| Donanım              | Gerçek Android cihaz veya emülatör |

---

## 🛠️ Detaylı Kurulum Adımları

### 1. Gerekli Yazılımları Yükleyin

- [Java JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- [Android Studio](https://developer.android.com/studio) (Arctic Fox veya daha güncel sürüm)

> Android Studio ilk açıldığında gerekli SDK ve Emulator paketleri kurulacaktır.

---

### 2. Projeyi İndirin

Projenin kaynak kodlarını aşağıdaki bağlantıdan indirin:

📦 [GhostHunter Kaynak Kodları (ZIP)](https://drive.google.com/file/d/1hSgrEtf9viSybgZsriOznflPY8lVHI3K/view?usp=sharing)

ZIP dosyasını bilgisayarınıza indirip çıkartın. Ardından Android Studio ile şu şekilde açın:

1. Android Studio’yu açın.
2. "Open an existing project" seçeneğini seçin.
3. Çıkarttığınız klasörün içindeki `ghosthunter/` dizinini seçin.
4. Android Studio, projeyi otomatik olarak yükleyecektir.

---

### 3. Gradle Ayarlarını Tamamlayın

- Android Studio projeyi açtığında otomatik olarak `Gradle` senkronizasyonu yapar.
- Eğer sorun yaşarsanız sol üstteki **“Sync Project with Gradle Files”** butonuna tıklayın.

---

### 4. Projeyi Çalıştırın

- Bir Android cihaz bağlayın (USB hata ayıklama açık olmalı) **veya** emülatör başlatın.
- Sağ üstteki yeşil **Run ▶** butonuna tıklayarak uygulamayı başlatın.

---

### Alternatif: APK Kurulumu

Android cihazınıza doğrudan APK’yı kurmak isterseniz:

📲 [GhostHunter APK Dosyasını İndir](https://drive.google.com/file/d/1261TXQGQg-HUo4_ZoyC_uo6GKD8FcAsF/view?usp=sharing)

1. APK’yı telefonunuza indirin.
2. Dosyayı çalıştırın ve "Bilinmeyen Kaynaklara İzin Ver" seçeneğini aktif edin.
3. Uygulama kurulacaktır.

---

## 🧯 Troubleshooting Rehberi

| Problem                                                                 | Çözüm                                                                                   |
|------------------------------------------------------------------------|------------------------------------------------------------------------------------------|
| `Gradle Build Failed` hatası                                           | Android Studio'da sağ üstte “Sync Project with Gradle Files” butonuna tıklayın.         |
| `Emulator is not running` veya cihaz bağlanamıyor                      | Fiziksel cihaz bağlıysa USB hata ayıklama açık mı kontrol edin. Emulator kullanıyorsanız AVD ayarlarını kontrol edin. |
| `INSTALL_FAILED_VERSION_DOWNGRADE` hatası (APK kurulurken)             | Telefonunuzda aynı uygulamanın eski sürümü varsa kaldırın, sonra tekrar kurmayı deneyin.|
| Uygulama açılmıyor veya aniden kapanıyor                               | Android Studio’da “Logcat” sekmesinden hata detaylarını inceleyin.                      |
| Arka plan resmi veya hayalet görselleri görünmüyor                     | `res/drawable/` klasöründe görsellerin eksik olup olmadığını kontrol edin.              |

---

## 🧑‍💻 Yardım & Destek

Proje ile ilgili herhangi bir sorun yaşarsanız GitHub Issues kısmından bize ulaşabilirsiniz ya da proje ekibiyle iletişime geçebilirsiniz.

---


