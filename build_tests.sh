set -e

function TEST_get_junit_version {
  version=$(get_junit_version)
  if [[ ! ($version == 4.*) ]]; then
    echo "Bad version: $version"
    return 1
  fi
}

function TEST_ant_dist {
  version=$(get_junit_version)
  ant dist
  ls junit${version}/junit-${version}.jar
}

function TEST_ant_profile {
  rm -rf java.hprof.txt
  ant profile
  ls java.hprof.txt
}

function get_junit_version {
  ant print.version | grep echo | sed 's/.*echo..\([1-9].*\)/\1/'
}

SCRIPT_NAME=$0

function get_tests() {
  part1=function
  part2=TEST_
  grep "$part1 $part2" $SCRIPT_NAME | sed 's/.*\(TEST_[A-Za-z0-9_]*\).*/\1/'
}

function run_tests() {
  local exit_code=0

  for t in $(get_tests); do
    echo "RUNNING: $t"
    if "$t"; then
      echo "PASSED: $t"
    else
      echo "FAILED: $t"
      return 1
    fi
  done
}

if run_tests; then
  echo "ALL TESTS PASSED"
  exit 0
else
  echo "A TEST FAILED"
  exit 1
fi
