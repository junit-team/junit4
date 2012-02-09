set -e
set -o pipefail

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

source build/run_tests.sh