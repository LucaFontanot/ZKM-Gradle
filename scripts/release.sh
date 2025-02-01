#!/usr/bin/env bash

echo "Releasing version $VERSION"

if [ "$(gh release list | grep -F "$VERSION")" != "" ]; then
  gh release delete "$VERSION" && gh api repos/:owner/:repo/git/refs/tags/"$VERSION" -X DELETE
fi

# Parse COMMIT_MESSAGE variables
string=$COMMIT_MESSAGE
COMMIT_MESSAGE=""

len=${#string}
finalString=""
i=0
while [ $i -lt "$len" ]; do
  char=${string:i:1}
  i=$((i + 1))
  if [ "$char" == "$" ]; then
    var=""
    for j in $(seq $i "$len"); do
      var="$var${string:j:1}"
      for invalid in invalid var string i j finalString len tmp val COMMIT_MESSAGE; do
        test "$var" == "$invalid" && continue 2
      done
      val=${!var}
      if [ "$val" != "" ]; then
        i=$((j + 1))
        finalString="$finalString$val"
        continue 2
      fi
    done
  fi
  finalString="$finalString$char"
done

COMMIT_MESSAGE=$finalString

# shellcheck disable=SC2086
gh release create "$VERSION" \
  --repo="$GITHUB_REPOSITORY" \
  --title="$REPOSITORY_NAME $VERSION" \
  --notes="$COMMIT_MESSAGE" \
  --latest $FILES