#! /usr/bin/env bash

# This is what _actually_ invokes bazel.  It'll be used whether we're in
# a nixified world or not.  The primary purpose is to DRY; the environment
# setup is handled by the invoking script.

BAZEL_COMMON_OPTS="--nohome_rc"

bazel $BAZEL_COMMON_OPTS "$@"
