set -e

function TEST_ant_dist {
  version=$(ant print.version)
  ant dist
  ls junit${version}/junit-${version}.jar
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
      exit_code=1
    fi
  done
  return $exit_code
}

run_tests
