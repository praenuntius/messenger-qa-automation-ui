package org.foxtrot.desktop.build;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Метаданные скачанного билда.
 * Хранится рядом с артефактом в файле metadata.json.
 * Позволяет при следующем запуске проверить: нужно ли качать снова.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BuildMetadata {

    /** Платформа: mac / linux / windows */
    private String platform;

    /** Запрошенная версия: "3.52.27" или "latest" */
    private String requestedVersion;

    /** Реальная версия, определённая по имени файла или приложению */
    private String resolvedVersion;

    /** Имя скачанного файла */
    private String filename;

    /** SHA-256 скачанного файла */
    private String sha256;

    /** ETag из HTTP-ответа S3 (для проверки обновлений latest) */
    private String etag;

    /** Дата/время скачивания, ISO-8601 */
    private String downloadedAt;

    /** Полный путь к файлу в кеше */
    private String localPath;
}
