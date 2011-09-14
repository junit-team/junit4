set -e
set -o pipefail

function TEST_junit_dep_49_plays_not_nicely_with_later_hamcrest {
  # Make sure our system notices the bug (this broke because of a bad push)
  ! runs_with_newer_hamcrest junit-dep 4.9
}

function TEST_junit_dep_snapshot_plays_nicely_with_later_hamcrest {
  runs_with_newer_hamcrest junit-dep LATEST
}

function TEST_junit_snapshot_plays_not_nicely_with_later_hamcrest {
  ! runs_with_newer_hamcrest junit LATEST
}

function runs_with_newer_hamcrest {
  local artifact_id=$1
  local version=$2
  rm -rf ~/.m2/repository/junit
  rm -rf uses_junit
  cp -r sample_project_template uses_junit
  sed -i '' -e "s/___ARTIFACT_ID___/$artifact_id/" uses_junit/pom.xml
  sed -i '' -e "s/___VERSION___/$version/" uses_junit/pom.xml
  in_dir uses_junit mvn test
  finally rm -rf uses_junit
}

### <copied src="https://gist.github.com/1206506">
function in_dir {
  local dir=$1
  shift
  if [ ! -e $dir ]; then
    echo "$dir does not exist"
    return 1
  fi
  pushd $dir >/dev/null
  "$@"
  finally popd >/dev/null
}

function finally {
  local return_this=$?
  "$@"
  return $return_this
}
### </copied>

source ../run_tests.sh