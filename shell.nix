{ pkgs ? import ./nixpkgs.nix { config = {}; overlays = []; } }:

with pkgs;

let
  /* Mastering time & space, improving our customers' lives */
  project_name = "gallifrey";

in mkShell {
  buildInputs = [
    bazel_3
    bazel-buildtools
    cacert
    coreutils
    gcc
    gitFull
    nix
    openjdk8
    python3
    which
  ] ++ lib.optionals stdenv.isDarwin [
    #(xcbuild.override { sdkVer = "10.15"; })  # No longer present in newer XCodes
    (xcbuild.override { sdkVer = "11.1"; })
  ];

  name = "${project_name}-build-nix-shell";

  IN_NIXIFIED_BUILD_ENV = project_name;

  shellHook = ''
    echo >&2 "Entered the ${project_name} nix development (and bazel execution) environment"

    alias l="ls -la"
    alias ls="ls --color"
  '';
}
