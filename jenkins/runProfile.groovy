/**
 * Загрузка профиля из config/run-profiles/{id}.properties для Jenkins pipeline.
 *
 * Использует readProperties (sandbox-safe). Ключи с точками в allure.properties —
 * через дефис (desktop-web), lookup через resolveDottedKey().
 */

Map fromProfile(String profileId, Map overrides = [:]) {
    def profileFile = "config/run-profiles/${profileId}.properties"
    if (!fileExists(profileFile)) {
        error "Run profile not found: ${profileFile}"
    }

    def props = flattenConfigMap(readProperties([file: profileFile]))
    overrides.each { key, value ->
        if (value != null && value.toString().trim()) {
            props[key.toString()] = value.toString().trim()
        }
    }

    def module = props.get('profile.module', 'web-tests')
    def flags = []
    props.each { key, value ->
        if (!key.startsWith('profile.') && !key.startsWith('maven.')) {
            flags << "-D${key}=${value}"
        }
    }

    return [
            id              : profileId,
            module          : module,
            description     : props.get('profile.description', profileId),
            props           : props,
            mavenModuleArgs : "-pl ${module} -am",
            mavenFlags      : flags.join(' '),
    ]
}

Map loadAllureCi() {
    return flattenConfigMap(readProperties([file: 'config/ci/allure.properties']))
}

Map loadPipelinesCi() {
    return flattenConfigMap(readProperties([file: 'config/ci/pipelines.properties']))
}

/** readProperties иногда вкладывает ключи a.b.c в nested map — разворачиваем в flat. */
Map flattenConfigMap(Map map, String prefix = '') {
    def result = [:]
    map.each { key, value ->
        def fullKey = prefix ? "${prefix}.${key}" : key.toString()
        if (value instanceof Map) {
            result.putAll(flattenConfigMap(value, fullKey))
        } else {
            result[fullKey] = value?.toString()
        }
    }
    return result
}

/**
 * Ищет значение по dotted key в flat или nested map.
 */
String resolveDottedKey(Map config, String dottedKey) {
    if (config.containsKey(dottedKey)) {
        return config.get(dottedKey)?.toString()
    }
    def parts = dottedKey.split('\\.')
    def current = config
    for (String part : parts) {
        if (!(current instanceof Map)) {
            return null
        }
        current = current.get(part)
    }
    return current?.toString()
}

String resolveLaunchName(String template, Map bindings = [:]) {
    if (!template) {
        return ''
    }
    def resolved = template.toString()
    bindings.each { key, value ->
        resolved = resolved.replace("\${${key}}", value?.toString() ?: '')
    }
    return resolved
}

String allureProjectId(Map allureCi, String projectKey) {
    def key = "allure.project.${projectKey}"
    def id = resolveDottedKey(allureCi, key)
    if (!id) {
        error "Allure project key not found in config/ci/allure.properties: ${key}"
    }
    return id
}

String allureLaunchKey(String projectKey) {
    return "allure.launch.${projectKey}"
}

String allureLaunchTemplate(Map allureCi, String projectKey) {
    def key = allureLaunchKey(projectKey)
    def template = resolveDottedKey(allureCi, key)
    if (!template) {
        error "Allure launch key not found in config/ci/allure.properties: ${key}"
    }
    return template
}

String mobileAllureProjectKey(String profileId) {
    if (profileId?.contains('android')) {
        return 'mobile-android'
    }
    if (profileId?.contains('ios')) {
        return 'mobile-ios'
    }
    error "Cannot resolve mobile Allure project key from profile: ${profileId}"
}

String mobilePlatform(Map props) {
    def platform = props.get('platform.name', '').toString().trim()
    if (platform in ['android', 'ios']) {
        return platform
    }
    error "Mobile profile must define platform.name=android or ios, got: '${platform}'"
}

String desktopAgentLabel(String platform, Map pipelinesCi) {
    switch (platform?.toString()?.trim()?.toLowerCase()) {
        case 'linux':
            return pipelinesCi['pipeline.desktop.linux.agent'] ?: 'linux-desktop'
        case 'mac':
            return pipelinesCi['pipeline.desktop.mac.agent'] ?: 'mac-mobile'
        case 'windows':
            return pipelinesCi['pipeline.desktop.windows.agent'] ?: 'windows-desktop'
        default:
            error "Unknown desktop PLATFORM: '${platform}'. Use linux, mac or windows."
    }
}

String defaultDesktopProfile(String platform, Map pipelinesCi) {
    switch (platform?.toString()?.trim()?.toLowerCase()) {
        case 'linux':
            return pipelinesCi['pipeline.desktop.linux.profile']
        case 'mac':
            return pipelinesCi['pipeline.desktop.mac.profile']
        default:
            error "No default desktop profile for platform: ${platform}"
    }
}

void validateDesktopProfilePlatform(String profileId, String platform) {
    def p = platform?.toString()?.trim()?.toLowerCase()
    def id = profileId?.toString()?.trim()?.toLowerCase() ?: ''
    if (p == 'linux' && !id.contains('linux') && !id.contains('playwright')) {
        error "PROFILE '${profileId}' не для PLATFORM=linux"
    }
    if (p == 'mac' && !id.contains('mac')) {
        error "PROFILE '${profileId}' не для PLATFORM=mac"
    }
    if (p == 'windows' && !id.contains('windows')) {
        error "PROFILE '${profileId}' не для PLATFORM=windows"
    }
}

return this
