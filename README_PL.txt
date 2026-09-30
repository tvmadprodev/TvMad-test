TvMad HDMI — test SKIP_SCREENSHOT

CEL:
- telefon: czarna nakładka "TvMad Galaxy / HDMI connected"
- TV HDMI: normalny pulpit Nova bez tej nakładki

WAŻNE:
To jest tylko test mechanizmu. Nie ma jeszcze automatycznego wykrywania HDMI.

PRZED TESTEM (Android 10+):
adb shell settings put global hidden_api_policy 1

PO TESTACH przywróć domyślną politykę:
adb shell settings delete global hidden_api_policy

OBSŁUGA:
1. Zbuduj APK przez GitHub Actions (.github/workflows/build.yml).
2. Zainstaluj TvMad-HDMI-Test.apk.
3. Uruchom aplikację.
4. Kliknij START TEST.
5. Przy pierwszym uruchomieniu zezwól na "Wyświetlanie nad innymi aplikacjami".
6. Kliknij START TEST ponownie.
7. Sprawdź telefon i TV.
8. Aby usunąć nakładkę, otwórz apkę ponownie i kliknij STOP.

LOG (opcjonalnie):
adb logcat -s TvMadHdmiTest
