set -e
set -o pipefail

SCRIPT_NAME=$0
TEST_NAME=${1:-ALL}

function TEST_BUILDING_in_zip {
  version=$(get_junit_version)
  ant zip
  unzip -l junit${version}/junit${version}.zip | grep BUILDING >/dev/null
}

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

function TEST_jars {
  version=$(get_junit_version)
  binjar=junit${version}/junit-${version}.jar
  srcjar=junit${version}/junit-${version}-src.jar
  depjar=junit${version}/junit-dep-${version}.jar

  ant clean
  ant jars

  jar tf $binjar | grep -q class \
    && jar tf $srcjar | grep -q java \
    && jar tf $depjar | grep -q class \
    && jar tf $depjar | not grep hamcrest
}

function TEST_all_maven_jars {
  version=$(get_junit_version)
  binjar=junit${version}/junit-${version}.jar
  srcjar=junit${version}/junit-${version}-src.jar
  docjar=junit${version}/junit-${version}-javadoc.jar
  depbin=junit${version}/junit-dep-${version}.jar
  depsrc=junit${version}/junit-dep-${version}-src.jar
  depdoc=junit${version}/junit-dep-${version}-javadoc.jar

  ant clean
  ant all.maven.jars

  jar tf $binjar | grep -q class \
    && jar tf $srcjar | grep -q java \
    && jar tf $docjar | grep -q html \
    && jar tf $depbin | grep -q class \
    && jar tf $depsrc | grep -q java \
    && jar tf $depdoc | grep -q html \
    && jar tf $depbin | not grep hamcrest \
    && jar tf $depsrc | not grep hamcrest \
    && jar tf $depdoc | not grep hamcrest
}

function not {
  ! "$@"
}

function get_junit_version {
  ant print.version | grep echo | sed 's/.*echo..\([1-9].*\)/\1/'
}

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
