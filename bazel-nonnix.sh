#! /usr/bin/env bash

# Here, user had better have installed bazel (and anything it requires,
# like python3) themselves, i guess.
echo >&2 'Using nonnixified bazel'
if [[ -n ${IN_NIXIFIED_BUILD_ENV:-} ]]; then
  echo >&2 '...because we already appear to be in the appropriate nix shell.'
else
  # TODO(Dave): Fix this BS.
  echo >&2 "If you're running this from IntelliJ on Mac, try launching IJ via 'idea' from Terminal"
  echo >&2 'to pick up the appropriate environment.'
fi

./bazel-final.sh "$@"
