load("@rules_jvm_external//:defs.bzl", "maven_install")

# To update the pinned dependencies, use:
# $ ./bazel run @unpinned_thirdparty_jvm//:pin

# TODO(Dave): Would really like a much nicer way of specifying these, which would have the nicer
# effect of also giving me aesthetic aliases for complete bundles in a local BUILD file.  Would
# also really like a way to do automatic version updates / consistent version resolutions across
# the repository
#
# Something like this:
#  https://github.com/johnynek/bazel-deps
# which seems to be at least somewhat supported in the standard rules_scala
def artifacts():
    return [
        ##############################################
        # Simple logging
        #---------------------------------------------
        "org.slf4j:slf4j-api:1.7.5",
        "org.slf4j:slf4j-simple:1.7.5",
        ##############################################

        ##############################################
        # Enumeratum, of course
        #---------------------------------------------
        "com.beachape:enumeratum_2.12:1.6.1",
        # ... and supporting libraries
        "com.beachape:enumeratum-circe_2.12:1.6.1",
        ##############################################

        ##############################################
        # Kantan, for CSV
        #---------------------------------------------
        #"com.nrinaudo:kantan.csv_2.12:0.6.1",
        #"com.nrinaudo:kantan.csv-cats_2.12:0.6.1",
        #"com.nrinaudo:kantan.csv-enumeratum_2.12:0.6.1",
        #"com.nrinaudo:kantan.csv-generic_2.12:0.6.1",
        # (if performance becomes an issue)
        #"com.nrinaudo:kantan.csv-jackson_2.12:0.6.1",
        ##############################################

        ##############################################
        # ZIO
        #---------------------------------------------
        "dev.zio:zio_2.12:1.0.3",
        "dev.zio:zio-streams_2.12:1.0.3",
        "dev.zio:zio-interop-cats_2.12:2.2.0.1",
        ##############################################

        ##############################################
        # Sttp, for HTTP client
        #---------------------------------------------
        "com.softwaremill.sttp.client:async-http-client-backend-zio_2.12:2.2.9",
        "com.softwaremill.sttp.client:circe_2.12:2.2.9",
        "com.softwaremill.sttp.client:core_2.12:2.2.9",
        # Add automatic request tracing & metrics
        "com.softwaremill.sttp.client:prometheus-backend_2.12:2.2.9",
        "com.softwaremill.sttp.client:zio-telemetry-opentracing-backend_2.12:2.2.9",
        ##############################################

        ##############################################
        # UZHttp, for simple HTTP serving
        #---------------------------------------------
        #"org.polynote:uzhttp_2.12:0.2.5",
        ##############################################

        ##############################################
        # Tapir, for HTTP serving
        #---------------------------------------------
        #"com.softwaremill.sttp.tapir:tapir-core_2.12:0.16.1",
        ##############################################

        ##############################################
        # http4s for HTTP serving, of course
        #---------------------------------------------
        #"org.http4s:http4s-dsl_2.12:0.21.6",
        ## ...and supporting libraries
        #"org.http4s:http4s-blaze-client_2.12:0.21.6",
        #"org.http4s:http4s-blaze-server_2.12:0.21.6",
        #"org.http4s:http4s-circe_2.12:0.21.6",
        ##############################################

        ##############################################
        # Slick & friends, FRM for DB access
        #---------------------------------------------
        "com.typesafe.slick:slick_2.12:3.3.3",
        "com.typesafe.slick:slick-hikaricp_2.12:3.3.3",
        # slick-pg: https://github.com/tminglei/slick-pg
        "com.github.tminglei:slick-pg_2.12:0.19.4",
        "com.github.tminglei:slick-pg_circe-json_2.12:0.19.4",
        ##############################################

        ##############################################
        # Circe, for JSON handling
        #---------------------------------------------
        "io.circe:circe-generic_2.12:0.13.0",
        "io.circe:circe-parser_2.12:0.13.0",
        ##############################################

        ##############################################
        # Cats, of course.  We love FP.
        #---------------------------------------------
        "org.typelevel:cats-core_2.12:2.3.1",
        "org.typelevel:cats-effect_2.12:2.3.1",
        ##############################################

        ##############################################
        # ZIO Tracing
        #---------------------------------------------
        # Only one of these is really needed
        #"dev.zio:zio-opentracing_2.12:0.7.0",
        "dev.zio:zio-opentelemetry_2.12:0.7.0",
        ##############################################

        ##############################################
        # ZIO Metrics
        #---------------------------------------------
        "dev.zio:zio-metrics_2.12:1.0.1",
        ##############################################

        ##############################################
        # ZIO config
        #---------------------------------------------
        "dev.zio:zio-config_2.12:1.0.0-RC30-1",
        "dev.zio:zio-config-magnolia_2.12:1.0.0-RC30-1",
        "dev.zio:zio-config-typesafe_2.12:1.0.0-RC30-1",
        #"dev.zio:zio-config-yaml_2.12:1.0.0-RC30-1",
        ##############################################

        ##############################################
        # ZIO logging
        #---------------------------------------------
        "dev.zio:zio-logging_2.12:0.5.4",
        "dev.zio:zio-logging-slf4j_2.12:0.5.4",
        ##############################################

        ##############################################
        # ZIO slack client
        #---------------------------------------------
        "com.github.dapperware:zio-slack-api-realtime_2.12:0.7.2",
        "com.github.dapperware:zio-slack-api-web_2.12:0.7.2",
        ##############################################

        ##############################################
        # Google Calendar API
        #---------------------------------------------
        "com.google.api-client:google-api-client:1.30.10",
        ##############################################

        ##############################################
        # Placeholder
        #---------------------------------------------
        ##############################################
    ]

def test_artifacts():
    return [
        ##############################################
        # Scalacheck
        #---------------------------------------------
        "org.scalacheck:scalacheck_2.12:1.13.4",
        ##############################################

        ##############################################
        # Scalatest
        #---------------------------------------------
        "org.scalatest:scalatest_2.12:3.2.3",
        ##############################################

        ##############################################
        # ZIO test
        #---------------------------------------------
        "dev.zio:zio-test_2.12:1.0.3",
        ##############################################
    ]

def thirdparty_jvm_dependencies():
    maven_install(
        name = "thirdparty_jvm",
        # TODO(Dave): Make test_artifacts be marked testonly
        artifacts = artifacts() + test_artifacts(),
        repositories = [
            "https://repo.maven.apache.org/maven2",
            "https://maven-central.storage-download.googleapis.com/maven2",
            "https://mirror.bazel.build/repo1.maven.org/maven2",
        ],
        fetch_sources = True,
        maven_install_json = "@//3rdparty/jvm:pinned_thirdparty_jvm_install.json",
    )