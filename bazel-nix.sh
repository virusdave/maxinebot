#! /usr/bin/env nix-shell
#! nix-shell -i bash --pure ./shell.nix

echo >&2 'Entering nixified bazel environment'

./bazel-final.sh "$@"
