rootProject.name = "DevOps"
buildCache {
    remote<HttpBuildCache> {
        url = uri("http://192.168.0.189:5071/cache/")
        isAllowInsecureProtocol = true
        isAllowUntrustedServer = true
        isPush = true
        credentials {
            username = "user009"
            password = "ulpuugz7vyymeweqamr2daiide"
        }

    }
}
