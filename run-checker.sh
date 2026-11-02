#!/usr/bin/env bash
# Runs the Checker Framework (checkerframework Maven profile) on JUnit 4.
# Full Maven output goes to build.log; the compiler errors are extracted
# into checker-errors.txt.
#
# Optional environment variables (see checker_framework_setup_manual.txt):
#   CF_JAVA_HOME      JDK used to run Maven and javac (JDK 17 or newer).
#                     Default: JDK 21 from /usr/libexec/java_home (macOS),
#                     otherwise the JDK of the `java` on PATH.
#   CF_VERSION        Checker Framework version to download from Maven Central.
#                     Default: the version in pom.xml (4.2.3).
#   CHECKERFRAMEWORK  Path to a local Checker Framework installation (the
#                     directory that contains checker/dist/checker.jar).
#                     When set, its jars are used instead of a download.
#   CF_MVN            Maven command. Default: mvn

cd "$(dirname "$0")" || exit 1

MVN=${CF_MVN:-mvn}
mvn_props=()

# --- JDK -------------------------------------------------------------------
if [ -n "${CF_JAVA_HOME:-}" ]; then
    JAVA_HOME=$CF_JAVA_HOME
elif [ -x /usr/libexec/java_home ] && /usr/libexec/java_home -v 21 >/dev/null 2>&1; then
    JAVA_HOME=$(/usr/libexec/java_home -v 21)
elif command -v java >/dev/null 2>&1; then
    JAVA_HOME=$(java -XshowSettings:properties -version 2>&1 | sed -n 's/^ *java.home = //p')
else
    echo "No JDK found. Set CF_JAVA_HOME to a JDK 17+ installation." >&2
    exit 1
fi
if [ ! -x "$JAVA_HOME/bin/javac" ]; then
    echo "$JAVA_HOME is not a JDK (no bin/javac). Set CF_JAVA_HOME to a JDK 17+ installation." >&2
    exit 1
fi
java_major=$("$JAVA_HOME/bin/java" -XshowSettings:properties -version 2>&1 \
    | sed -n 's/^ *java.specification.version = //p' | sed 's/^1\.//')
if [ "${java_major:-0}" -lt 17 ]; then
    echo "JDK at $JAVA_HOME is version $java_major; the Checker Framework 4.x needs 17 or newer." >&2
    exit 1
fi
export JAVA_HOME

# --- Checker Framework -----------------------------------------------------
if [ -n "${CHECKERFRAMEWORK:-}" ]; then
    checker_jar=$CHECKERFRAMEWORK/checker/dist/checker.jar
    qual_jar=$CHECKERFRAMEWORK/checker/dist/checker-qual.jar
    for jar in "$checker_jar" "$qual_jar"; do
        if [ ! -f "$jar" ]; then
            echo "CHECKERFRAMEWORK is set but $jar does not exist." >&2
            exit 1
        fi
    done
    # Maven resolves the processor by coordinates, so register the local jars
    # in the local Maven repository under a version that cannot clash with a
    # real release. The checker pom declares checker-qual so that the
    # annotation classes are on the processor path too.
    local_version=local-dist
    echo "Registering $CHECKERFRAMEWORK in the local Maven repository as version $local_version ..."
    checker_pom=$(mktemp)
    cat > "$checker_pom" <<EOF
<project xmlns="http://maven.apache.org/POM/4.0.0">
  <modelVersion>4.0.0</modelVersion>
  <groupId>org.checkerframework</groupId>
  <artifactId>checker</artifactId>
  <version>$local_version</version>
  <dependencies>
    <dependency>
      <groupId>org.checkerframework</groupId>
      <artifactId>checker-qual</artifactId>
      <version>$local_version</version>
    </dependency>
  </dependencies>
</project>
EOF
    if ! "$MVN" -q install:install-file -Dfile="$qual_jar" -DgroupId=org.checkerframework \
            -DartifactId=checker-qual -Dversion="$local_version" -Dpackaging=jar > build.log 2>&1 \
        || ! "$MVN" -q install:install-file -Dfile="$checker_jar" -DpomFile="$checker_pom" \
            >> build.log 2>&1; then
        rm -f "$checker_pom"
        echo "Could not install the local jars into the local Maven repository; see build.log" >&2
        exit 1
    fi
    rm -f "$checker_pom"
    mvn_props+=("-Dcheckerframework.version=$local_version")
    cf_desc="local Checker Framework at $CHECKERFRAMEWORK"
elif [ -n "${CF_VERSION:-}" ]; then
    mvn_props+=("-Dcheckerframework.version=$CF_VERSION")
    cf_desc="Checker Framework $CF_VERSION"
else
    cf_desc="Checker Framework version from pom.xml"
fi

# --- Run -------------------------------------------------------------------
echo "Running $cf_desc with JDK $java_major ($JAVA_HOME) ..."
"$MVN" -P checkerframework "${mvn_props[@]}" clean compile > build.log 2>&1
mvn_status=$?

awk '/COMPILATION ERROR :/{f=1;next} f&&/^\[INFO\] [0-9]+ errors?/{print;exit} f&&!/^\[INFO\] ---/' \
    build.log > checker-errors.txt

if [ "$mvn_status" -eq 0 ]; then
    echo "Build succeeded: no errors."
elif [ -s checker-errors.txt ]; then
    echo "$(tail -n 1 checker-errors.txt | sed 's/^\[INFO\] //; s/ *$//') written to checker-errors.txt"
else
    echo "Build failed before compilation; see build.log" >&2
fi

exit "$mvn_status"
