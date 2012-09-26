	SHELL = /bin/sh
	ROOT=$(shell pwd)
	
	RELEASE_VERSION=#(e.g. 4.11) release version without user's prompt if non-empty property value; otherwise prompt
	LOCAL_REPO=${user.home}/.m2/repository# Use only single configuration file src/main/config/settings.xml for the build process.
	MVN_LOG=$(ROOT)/mvn.log
	
	RELEASE_VERSION_ARG=-DreleaseVersion=$(RELEASE_VERSION)
	LOCAL_REPO_ARG=# -Dmaven.repo.local=$(LOCAL_REPO)# Don't use since src/main/config/settings.xml already specified local repo path.
	MVN_ARGS=$(RELEASE_VERSION_ARG) $(LOCAL_REPO_ARG) --errors -s src/main/config/settings.xml# --log-file $(MVN_LOG)
	
	
	MVN_DIR=apache-maven-3.0.4
	MVN_BIN_URL=http://www.us.apache.org/dist/maven/maven-3/3.0.4/binaries
	MVN_BIN_EXT=bin.tar.gz
	MVN_BIN=$(MVN_DIR)-$(MVN_BIN_EXT)
	MVN_URL=$(MVN_BIN_URL)/$(MVN_BIN)
	M2_HOME=$(ROOT)/build/maven/$(MVN_DIR)
	M2=$(M2_HOME)/bin
	#export PATH=$M2:$PATH
	export MAVEN_OPTS=-Xms16m -Xmx128m -Dfile.encoding=ISO-8859-1

.PHONY: all

# target: all (currently configure release install)
all:	configure release install

# target: help - Display callable targets.
help:
	egrep "^# target:" [Mm]akefile

# target: configure - Configure build process.
configure:
	if [ ! -d $(ROOT)/build/maven/$(MVN_DIR) ] ;then \
		cd $(ROOT)/build/maven; wget $(MVN_URL); \
		cd $(ROOT)/build/maven; tar xvzf $(MVN_BIN); \
		cd $(ROOT)/build/maven; rm -rf $(MVN_BIN); \
		cd $(ROOT)/build/maven; chmod -R a+rX $(MVN_DIR); \
		cd $(ROOT); \
	fi; \
	cd $(ROOT); touch $(MVN_LOG); \
	cd $(ROOT); exec $(M2)/mvn $(MVN_ARGS) --version; \
	cd $(ROOT)

# target: release - Prompts the user to modify *-SNAPSHOT version to a new release, and updates junit.runner.Version#id().
release:
	#git checkout KentBeck pom.xml
	cd $(ROOT); exec $(M2)/mvn $(MVN_ARGS) -P checkSnapshot -f pom.xml release:update-versions; \     # update junit:junit from SNAPSHOT version
	cd $(ROOT)
	
# target: deploy - Deploys artifacts junit:junit and junit:junit-dep to remote repository Maven central and Sourceforge ftp server.
deploy:
	cd $(ROOT); exec $(M2)/mvn $(MVN_ARGS) -P checkRelease -f pom.xml clean deploy; \		# junit:junit deploy
	cd $(ROOT)
	
# target: install - Installs (and compiles if necessary) the artifacts to local repository including running unit and integration tests, etc.
install:
	cd $(ROOT); exec $(M2)/mvn $(MVN_ARGS) -f pom.xml clean install; \	# junit:junit install
	cd $(ROOT)
