load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

# TODO(Dave): Reorganize this file in some sensible fashion

####################################################################################################
# Scala support, via higherkindness
#--------------------------------------------------------------------------------------------------#
# Load rules scala annex
rules_scala_annex_version = "b772564a20eee9271068cfba55147191385343bd"
rules_scala_annex_sha256 = "0ad12d1a04086517a87012dee0ee863e8331f2330f92d7546a7fe62b6af179db"

http_archive(
    name = "rules_scala_annex",
    sha256 = rules_scala_annex_sha256,
    strip_prefix = "rules_scala-{}".format(rules_scala_annex_version),
    url = "https://github.com/higherkindness/rules_scala/archive/{}.zip".format(rules_scala_annex_version),
)

load("@rules_scala_annex//rules/scala:workspace.bzl", "scala_register_toolchains", "scala_repositories")
scala_repositories()
load("@annex//:defs.bzl", annex_pinned_maven_install = "pinned_maven_install")
annex_pinned_maven_install()
scala_register_toolchains()

load("@rules_scala_annex//rules/scalafmt:workspace.bzl", "scalafmt_default_config", "scalafmt_repositories")
scalafmt_repositories()
load("@annex_scalafmt//:defs.bzl", annex_scalafmt_pinned_maven_install = "pinned_maven_install")
annex_scalafmt_pinned_maven_install()
scalafmt_default_config()

load("@rules_scala_annex//rules/scala_proto:workspace.bzl", "scala_proto_register_toolchains", "scala_proto_repositories",)
scala_proto_repositories()
load("@annex_proto//:defs.bzl", annex_proto_pinned_maven_install = "pinned_maven_install")
annex_proto_pinned_maven_install()
scala_proto_register_toolchains()

# Specify the scala compiler we wish to use; in this case, we'll use the default one specified in rules_scala_annex
bind(
    name = "default_scala",
    actual = "@rules_scala_annex//src/main/scala:zinc_2_12_10",
)
####################################################################################################


#####################################################################################################
## Scala support, via rules_scala
##--------------------------------------------------------------------------------------------------#
## HEAD as of 2021-01-15.
#rules_scala_version = "5df8033f752be64fbe2cedfd1bdbad56e2033b15"
#
#http_archive(
#    name = "io_bazel_rules_scala",
#    sha256 = "58c9c3974f266860d838a306e6524ea5bdcded3073eac7181743afd07a60afdf",
#    strip_prefix = "rules_scala-%s" % rules_scala_version,
#    type = "zip",
#    url = "https://github.com/bazelbuild/rules_scala/archive/%s.zip" % rules_scala_version,
#)
#
## Stores Scala version and other configuration
## 2.12 is a default version, other versions can be use by passing them explicitly:
## scala_config(scala_version = "2.11.12")
#load("@io_bazel_rules_scala//:scala_config.bzl", "scala_config")
##scala_config()  # 2.12
#scala_config(scala_version = "2.13.3")
#
#load("@io_bazel_rules_scala//scala:scala.bzl", "scala_repositories")
#scala_repositories()
#
#load("@io_bazel_rules_scala//scala:toolchains.bzl", "scala_register_toolchains")
#scala_register_toolchains()
#
## optional: setup ScalaTest toolchain and dependencies
#load("@io_bazel_rules_scala//testing:scalatest.bzl", "scalatest_repositories", "scalatest_toolchain")
#scalatest_repositories()
#scalatest_toolchain()
#####################################################################################################


####################################################################################################
# Misc
#--------------------------------------------------------------------------------------------------#
# Load bazel skylib and google protobuf
bazel_skylib_tag = "1.0.2"
bazel_skylib_sha256 = "97e70364e9249702246c0e9444bccdc4b847bed1eb03c5a3ece4f83dfe6abc44"
http_archive(
    name = "bazel_skylib",
    sha256 = bazel_skylib_sha256,
    urls = [
        "https://mirror.bazel.build/github.com/bazelbuild/bazel-skylib/releases/download/{tag}/bazel-skylib-{tag}.tar.gz".format(tag = bazel_skylib_tag),
        "https://github.com/bazelbuild/bazel-skylib/releases/download/{tag}/bazel-skylib-{tag}.tar.gz".format(tag = bazel_skylib_tag),
    ],
)

protobuf_tag = "3.10.1"
protobuf_sha256 = "678d91d8a939a1ef9cb268e1f20c14cd55e40361dc397bb5881e4e1e532679b1"
http_archive(
    name = "com_google_protobuf",
    sha256 = protobuf_sha256,
    strip_prefix = "protobuf-{}".format(protobuf_tag),
    type = "zip",
    url = "https://github.com/protocolbuffers/protobuf/archive/v{}.zip".format(protobuf_tag),
)

load("@com_google_protobuf//:protobuf_deps.bzl", "protobuf_deps")
protobuf_deps()
####################################################################################################



