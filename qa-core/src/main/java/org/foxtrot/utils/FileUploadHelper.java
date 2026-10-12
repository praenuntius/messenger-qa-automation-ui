package org.foxtrot.utils;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static com.codeborne.selenide.Selenide.$;

/**
 * Утилита для работы с загрузкой медиафайлов в контейнерах Selenoid.
 * Поддерживает несколько методов загрузки файлов:
 * 1. Генерация файлов программно в контейнере
 * 2. Загрузка через input[type="file"]
 * 3. Drag and Drop
 */
public class FileUploadHelper {

    /**
     * Создает временный файл в контейнере браузера через JavaScript.
     * Файл создается в /tmp директории контейнера.
     *
     * @param fileName имя файла
     * @param content  содержимое файла (байты)
     * @return путь к созданному файлу в контейнере
     */
    @Step("Создать временный файл '{fileName}' в контейнере")
    public String createTempFileInContainer(String fileName, byte[] content) {
        JavascriptExecutor js = (JavascriptExecutor) WebDriverRunner.getWebDriver();

        // Конвертируем байты в base64 для передачи через JavaScript
        String base64Content = Base64.getEncoder().encodeToString(content);

        // Создаем файл через JavaScript File API (работает только в браузере)
        // Альтернативно можно использовать Selenoid file manager API
        String script = String.format(
                "var blob = new Blob([atob('%s')], {type: 'application/octet-stream'});" +
                        "var file = new File([blob], '%s');" +
                        "return file;",
                base64Content, fileName
        );

        // Для реальной работы в контейнере лучше использовать другой подход
        // См. метод uploadFileViaInput ниже
        return fileName;
    }

    /**
     * Загружает файл через input[type="file"] элемент.
     * Ищет файл в resources, если не найден - выбрасывает исключение.
     *
     * @param inputElement селектор input элемента для загрузки файла
     * @param filePath     путь к файлу относительно src/main/resources/test-files/ или полный путь от resources
     * @return File объект загруженного файла
     * @throws RuntimeException если файл не найден
     */
    @Step("Загрузить файл '{filePath}' через input элемент")
    public File uploadFileViaInput(By inputElement, String filePath) {
        File file = getFileFromResources(filePath);
        if (file == null || !file.exists()) {
            throw new RuntimeException(
                    String.format("Файл '%s' не найден в resources. " +
                            "Поместите файл в src/main/resources/test-files/ или укажите полный путь от resources.", filePath)
            );
        }
        $(inputElement).uploadFile(file);
        return file;
    }

    /**
     * Загружает файл через input[type="file"] элемент, используя Selenide uploadFile.
     * Ищет файл в resources, если не найден - выбрасывает исключение.
     *
     * @param inputElement SelenideElement для input[type="file"]
     * @param filePath     путь к файлу относительно src/main/resources/test-files/ или полный путь от resources
     * @throws RuntimeException если файл не найден
     */
    @Step("Загрузить файл '{filePath}' через SelenideElement")
    public void uploadFileViaSelenideElement(SelenideElement inputElement, String filePath) {
        File file = getFileFromResources(filePath);
        if (!file.exists()) {
            throw new RuntimeException(
                    String.format("Файл '%s' не найден. Искомый путь: %s",
                            filePath, file.getAbsolutePath())
            );
        }
        inputElement.uploadFile(file);
    }

    /**
     * Выполняет Drag and Drop файла на целевой элемент.
     * Создает DataTransfer объект через JavaScript и эмулирует drag and drop событие.
     *
     * @param targetElement целевой элемент для drop
     * @param filePath      путь к файлу относительно src/main/resources/test-files/ или полный путь от resources
     * @throws RuntimeException если файл не найден
     */
    @Step("Скопировать изображение '{filePath}' в буфер обмена браузера")
    public void copyImageToBrowserClipboard(String filePath) {
        File file = getFileFromResources(filePath);
        if (file == null || !file.exists()) {
            throw new RuntimeException(String.format("Файл '%s' не найден в resources.", filePath));
        }

        try {
            ByteArrayOutputStream pngBytes = new ByteArrayOutputStream();
            ImageIO.write(ImageIO.read(file), "png", pngBytes);
            String base64 = Base64.getEncoder().encodeToString(pngBytes.toByteArray());

            JavascriptExecutor js = (JavascriptExecutor) WebDriverRunner.getWebDriver();
            Object error = js.executeAsyncScript(
                    "const cb = arguments[arguments.length - 1];" +
                            "const base64 = arguments[0];" +
                            "const bytes = Uint8Array.from(atob(base64), c => c.charCodeAt(0));" +
                            "const blob = new Blob([bytes], {type: 'image/png'});" +
                            "navigator.clipboard.write([new ClipboardItem({'image/png': blob})])" +
                            "  .then(() => cb(null)).catch(e => cb(e.message || String(e)));",
                    base64
            );
            if (error != null) {
                throw new RuntimeException(String.valueOf(error));
            }
        } catch (IOException e) {
            throw new RuntimeException("Не удалось прочитать файл: " + filePath, e);
        }
    }

    @Step("Выполнить Drag and Drop файла '{filePath}' на элемент")
    public void dragAndDropFile(SelenideElement targetElement, String filePath) {
        File file = getFileFromResources(filePath);
        if (file == null || !file.exists()) {
            throw new RuntimeException(
                    String.format("Файл '%s' не найден в resources. " +
                            "Поместите файл в src/main/resources/test-files/ или укажите полный путь от resources.", filePath)
            );
        }

        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            String base64 = Base64.getEncoder().encodeToString(bytes);

            JavascriptExecutor js = (JavascriptExecutor) WebDriverRunner.getWebDriver();
            js.executeScript(
                    "const target = arguments[0];" +
                            "const name = arguments[1];" +
                            "const base64 = arguments[2];" +
                            "const byteChars = atob(base64);" +
                            "const byteNumbers = new Array(byteChars.length);" +
                            "for (let i = 0; i < byteChars.length; i++) byteNumbers[i] = byteChars.charCodeAt(i);" +
                            "const byteArray = new Uint8Array(byteNumbers);" +
                            "const blob = new Blob([byteArray]);" +
                            "const file = new File([blob], name);" +
                            "const dataTransfer = new DataTransfer();" +
                            "dataTransfer.items.add(file);" +
                            "['dragenter','dragover','drop'].forEach(type => {" +
                            "  const event = new DragEvent(type, {bubbles: true, cancelable: true, dataTransfer});" +
                            "  target.dispatchEvent(event);" +
                            "});",
                    targetElement,
                    file.getName(),
                    base64
            );
        } catch (IOException e) {
            throw new RuntimeException("Не удалось прочитать файл для Drag and Drop: " + filePath, e);
        }
    }

    /**
     * Создает временный файл для тестирования.
     * Файл создается в системной временной директории.
     *
     * @param fileName имя файла (может содержать расширение, например "test.jpg")
     * @return созданный File объект
     */
    public File createTempFile(String fileName) {
        try {
            // Определяем тип файла по расширению и создаем соответствующий контент
            String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
            byte[] content = generateFileContent(extension, fileName);

            Path tempDir = Files.createTempDirectory("selenoid_test_files_");
            Path tempFile = tempDir.resolve(fileName);
            Files.write(tempFile, content);

            // Удаляем директорию при завершении JVM
            tempFile.toFile().deleteOnExit();
            tempDir.toFile().deleteOnExit();

            return tempFile.toFile();
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать временный файл: " + fileName, e);
        }
    }

    /**
     * Генерирует содержимое файла в зависимости от типа.
     *
     * @param extension расширение файла
     * @param fileName  имя файла
     * @return байты содержимого файла
     */
    private byte[] generateFileContent(String extension, String fileName) {
        switch (extension) {
            case "jpg":
            case "jpeg":
                // Минимальный валидный JPEG (1x1 пиксель)
                return Base64.getDecoder().decode(
                        "/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH/2wBDAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH/wAARCAABAAEDASIAAhEBAxEB/8QAFQABAQAAAAAAAAAAAAAAAAAAAAv/xAAUEAEAAAAAAAAAAAAAAAAAAAAA/8QAFQEBAQAAAAAAAAAAAAAAAAAAAAX/xAAUEQEAAAAAAAAAAAAAAAAAAAAA/9oADAMBAAIRAxEAPwA/8A"
                );
            case "png":
                // Минимальный валидный PNG (1x1 пиксель, прозрачный)
                return Base64.getDecoder().decode(
                        "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg=="
                );
            case "gif":
                // Минимальный валидный GIF (1x1 пиксель)
                return new byte[]{
                        (byte) 0x47, (byte) 0x49, (byte) 0x46, (byte) 0x38, (byte) 0x39, (byte) 0x61, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x21, (byte) 0xF9, (byte) 0x04, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x2C, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00,
                        (byte) 0x00, (byte) 0x02, (byte) 0x02, (byte) 0x04, (byte) 0x01, (byte) 0x00, (byte) 0x3B
                };
            case "pdf":
                // Минимальный валидный PDF
                return "%PDF-1.4\n1 0 obj\n<<\n/Type /Catalog\n>>\nendobj\nxref\n0 1\ntrailer\n<<\n/Size 1\n/Root 1 0 R\n>>\n%%EOF".getBytes();
            case "txt":
                return ("Test file content: " + fileName).getBytes();
            case "mp4":
                // Минимальный валидный MP4 (пустой контейнер)
                // В реальности лучше использовать реальные тестовые файлы
                return new byte[100]; // Заглушка
            default:
                return ("Test file: " + fileName).getBytes();
        }
    }

    /**
     * Получает файл из resources директории проекта.
     * Поддерживает несколько вариантов путей:
     * 1. Относительный путь от test-files: "images/test-image.jpg" -> "test-files/images/test-image.jpg"
     * 2. Полный путь от resources: "test-files/images/test-image.jpg"
     * 3. Абсолютный путь к файлу
     *
     * @param filePath путь к файлу
     * @return File объект или null, если файл не найден
     */
    private File getFileFromResources(String filePath) {
        String resourcePath = toTestFilesResourcePath(filePath);

        File fromClasspath = loadFromClasspath(resourcePath);
        if (fromClasspath != null) {
            return fromClasspath;
        }

        for (Path baseDir : resolveTestFilesBaseDirs()) {
            Path candidate = baseDir.resolve(stripTestFilesPrefix(filePath));
            if (Files.isRegularFile(candidate)) {
                return candidate.toFile();
            }
        }

        return baseDirFallbackFile(filePath);
    }

    private static String toTestFilesResourcePath(String filePath) {
        String normalized = filePath.replace('\\', '/');
        if (normalized.startsWith("test-files/")) {
            return normalized;
        }
        return "test-files/" + normalized;
    }

    private static String stripTestFilesPrefix(String filePath) {
        String normalized = filePath.replace('\\', '/');
        if (normalized.startsWith("test-files/")) {
            return normalized.substring("test-files/".length());
        }
        return normalized;
    }

    private File loadFromClasspath(String resourcePath) {
        ClassLoader classLoader = FileUploadHelper.class.getClassLoader();
        URL url = classLoader.getResource(resourcePath);
        if (url == null) {
            return null;
        }

        try {
            if ("file".equals(url.getProtocol())) {
                return new File(url.toURI());
            }
            return copyClasspathResourceToTempFile(classLoader, resourcePath);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось прочитать resource: " + resourcePath, e);
        }
    }

    private File copyClasspathResourceToTempFile(ClassLoader classLoader, String resourcePath) throws IOException {
        try (InputStream inputStream = classLoader.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                return null;
            }
            String fileName = Paths.get(resourcePath).getFileName().toString();
            Path tempFile = Files.createTempFile("selenide-upload-", "-" + fileName);
            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            File file = tempFile.toFile();
            file.deleteOnExit();
            return file;
        }
    }

    /**
     * Поиск test-files на диске: web-tests, qa-core, корень мультимодуля (запуск из IDE / mvn -pl).
     */
    private List<Path> resolveTestFilesBaseDirs() {
        Set<Path> bases = new LinkedHashSet<>();
        Path cwd = Paths.get("").toAbsolutePath().normalize();

        bases.add(cwd.resolve("src/main/resources/test-files"));
        bases.add(cwd.resolve("target/classes/test-files"));
        bases.add(cwd.resolve("../qa-core/src/main/resources/test-files"));
        bases.add(cwd.resolve("../qa-core/target/classes/test-files"));

        Path dir = cwd;
        for (int i = 0; i < 6 && dir != null; i++) {
            bases.add(dir.resolve("qa-core/src/main/resources/test-files"));
            bases.add(dir.resolve("qa-core/target/classes/test-files"));
            bases.add(dir.resolve("web-tests/src/main/resources/test-files"));
            dir = dir.getParent();
        }

        List<Path> existing = new ArrayList<>();
        for (Path base : bases) {
            if (Files.isDirectory(base)) {
                existing.add(base);
            }
        }
        return existing;
    }

    /**
     * Для сообщения об ошибке — прежний путь относительно CWD.
     */
    private File baseDirFallbackFile(String filePath) {
        return Paths.get("src", "main", "resources", "test-files")
                .resolve(stripTestFilesPrefix(filePath))
                .toFile();
    }

    /**
     * Загружает файл через Selenoid File Manager API.
     * Этот метод использует специальный механизм Selenoid для загрузки файлов в контейнер.
     *
     * @param filePath путь к файлу
     * @return путь к файлу в контейнере Selenoid
     */
    @Step("Загрузить файл '{filePath}' через Selenoid File Manager")
    public String uploadFileViaSelenoid(String filePath) {
        // Selenoid предоставляет специальный endpoint для загрузки файлов
        // Обычно это POST /file к Selenoid hub
        // В реальной реализации нужно использовать HTTP клиент для загрузки файла

        File file = getFileFromResources(filePath);
        if (file == null) {
            file = createTempFile(filePath);
        }

        // TODO: Реализовать загрузку через Selenoid File Manager API
        // Пример: POST http://selenoid:4444/file с multipart/form-data
        // Возвращает путь к файлу в контейнере, который можно использовать в input[type="file"]

        return file.getAbsolutePath();
    }
}

