#!/usr/bin/env sh
set -eu

repo_root=$(git rev-parse --show-toplevel)

git -C "$repo_root" config core.hooksPath scripts/git-hooks

printf 'Git hooks installed from scripts/git-hooks\n'
