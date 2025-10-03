rootProject.name = "DevOps"
buildCache {
    remote<HttpBuildCache> {
        url = uri(System.getenv("GRADLE_REMOTE_CACHE_URL") ?: "http://192.168.0.189:5071/cache/")
        isAllowInsecureProtocol = true
        isAllowUntrustedServer = true
        isPush = System.getenv("GRADLE_REMOTE_CACHE_PUSH" ?: "true").toBoolean()
        credentials {
            username = System.getenv("GRADLE_REMOTE_CACHE_USERNAME" ?: "user009")
            password = System.getenv("GRADLE_REMOTE_CACHE_PASSWORD" ?: "ulpuugz7vyymeweqamr2daiide")
        }
    }
}
