rootProject.name = "DevOps"
buildCache {
    remote<HttpBuildCache> {
        url = uri(System.getenv("GRADLE_REMOTE_CACHE_URL") ?: "http://192.168.0.189:5071/cache/")
        isAllowInsecureProtocol = true
        isAllowUntrustedServer = true
        val isPushString = System.getenv("GRADLE_REMOTE_CACHE_USH")
        isPush = isPushString.toBoolean() ?: true
        credentials {
            username = "user009"
            password = "ulpuugz7vyymeweqamr2daiide"
        }

    }
}
