VERSION=$1
DIST_DIR=junit$VERSION

rm -Rf target/maven
mkdir target/maven
mkdir target/maven/junit
mkdir target/maven/junit/junit
mkdir target/maven/junit/junit/$VERSION
mkdir target/maven/junit/junit-dep
mkdir target/maven/junit/junit-dep/$VERSION

cp pom-template.xml target/maven/pom.xml
cp $DIST_DIR/junit-$VERSION.jar target/maven/junit/junit/junit-$VERSION.jar
cp $DIST_DIR/junit-dep-$VERSION.jar target/maven/junit/junit-dep/junit-dep-$VERSION.jar 

cd junit$VERSION/javadoc
jar -cf ../../target/maven/junit-$VERSION-javadoc.jar *
cd ../..

