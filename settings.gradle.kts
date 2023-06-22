rootProject.name = "ok-marketplace-202302"

//include("m1l1-hello")
//include("m1l2-basic")
//include("m1l3-oop")
//include("m1l4-dsl")
//include("m1l5-coroutines")
//include("m1l6-flows-and-channels")
//include("m1l7-kmp")
//include("m3l1-spring")
//include("m4l5-validation")

include("ok-marketplace-acceptance")

include("ok-marketplace-lib-logging-common")
include("ok-marketplace-lib-logging-kermit")
include("ok-marketplace-lib-logging-logback")

include("ok-marketplace-api-v1-jackson")
include("ok-marketplace-api-v2-kmp")
include("ok-marketplace-api-log1")

include("ok-marketplace-common")
include("ok-marketplace-mappers-v1")
include("ok-marketplace-mappers-v2")
include("ok-marketplace-mappers-log1")

include("ok-marketplace-lib-cor")
include("ok-marketplace-biz")
include("ok-marketplace-stubs")

include("ok-marketplace-app-spring")
include("ok-marketplace-app-ktor")
include("ok-marketplace-app-serverless")
include("ok-marketplace-app-rabbit")
include("ok-marketplace-app-kafka")
