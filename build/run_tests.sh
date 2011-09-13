# See maven/post_maven_tests.sh for an example use

SCRIPT_NAME=$0
TEST_NAME=${1:-ALL}

function get_tests() {
  if [ $TEST_NAME == "ALL" ]; then
    part1=function
    part2=TEST_
    grep "$part1 $part2" $SCRIPT_NAME | sed 's/.*\(TEST_[A-Za-z0-9_]*\).*/\1/'
  else
    echo "TEST_${TEST_NAME}"
  fi
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
