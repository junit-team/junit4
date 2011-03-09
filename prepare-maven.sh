# Configure variables
VERSION=$1
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
cd junit$VERSION/javadoc
jar -cf ../../$JUNIT_DIR/junit-$VERSION-javadoc.jar *
jar -cf ../../$JUNIT_DEP_DIR/junit-$VERSION-javadoc.jar *
cd ../../

