# Configure variables
if [ "$1" == "" ]; then
    echo "Usage: prepare-maven.sh JUNIT_VERSION <PASSPHRASE>"
	exit
fi
VERSION=$1

PASSPHRASE=$2
if [ "$PASSPHRASE" == "" ]; then
    read -s -p "Enter passphrase: " PASSPHRASE
fi

DIST_DIR=junit$VERSION
JUNIT_DIR=target/maven/junit-$VERSION
JUNIT_DEP_DIR=target/maven/junit-dep-$VERSION

# Create staging directories
rm -Rf target/maven
mkdir -p $JUNIT_DIR
mkdir -p $JUNIT_DEP_DIR

# Copy POM file template
cp pom-template.xml $JUNIT_DIR/pom.xml
cp pom-template.xml $JUNIT_DEP_DIR/pom.xml

# Copy binary JARs
cp $DIST_DIR/junit-$VERSION.jar $JUNIT_DIR/junit-$VERSION.jar
cp $DIST_DIR/junit-dep-$VERSION.jar $JUNIT_DEP_DIR/junit-dep-$VERSION.jar 

# Build Javadoc JARs
jar -cf $JUNIT_DIR/junit-$VERSION-javadoc.jar -C $DIST_DIR/javadoc .
jar -cf $JUNIT_DEP_DIR/junit-$VERSION-javadoc.jar -C $DIST_DIR/javadoc .

# Build Source JARs
jar -cf $JUNIT_DIR/junit-$VERSION-sources.jar -C $DIST_DIR/org . -C $DIST_DIR/junit . -C $DIST_DIR/temp.hamcrest.source .
jar -cf $JUNIT_DEP_DIR/junit-dep-$VERSION-sources.jar -C $DIST_DIR/org . -C $DIST_DIR/junit .

#Sign files
find $JUNIT_DIR -type f -exec gpg -ab --passphrase "$PASSPHRASE" {} \;
find $JUNIT_DEP_DIR -type f -exec gpg -ab --passphrase "$PASSPHRASE" {} \;

# Rename POM sig file to expeted convention
mv $JUNIT_DIR/pom.xml.asc $JUNIT_DIR/junit-$VERSION.pom.asc
mv $JUNIT_DEP_DIR/pom.xml.asc $JUNIT_DEP_DIR/junit-dep-$VERSION.pom.asc

# Creat bundled JARs
jar -Mcf $DIST_DIR/junit-$VERSION-bundle.jar -C $JUNIT_DIR .
jar -Mcf $DIST_DIR/junit-dep-$VERSION-bundle.jar -C $JUNIT_DEP_DIR .
