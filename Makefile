GRAALVM_HOME = $(HOME)/graalvm-ce-java11-20.1.0
ifneq (,$(findstring java11,$(GRAALVM_HOME)))
	JAVA_VERSION = 11
else
	JAVA_VERSION = 8
endif
VERSION = 0.1.0-SNAPSHOT
UNAME = $(shell uname)

all: build/lwjgl-example

analyse:
	$(GRAALVM_HOME)/bin/java -agentlib:native-image-agent=config-output-dir=config-dir \
		-jar target/uberjar/lwjgl-example-$(VERSION)-standalone.jar

build/lwjgl-example: target/uberjar/lwjgl-example-$(VERSION)-standalone.jar
	-mkdir build
	$(GRAALVM_HOME)/bin/native-image \
		-jar target/uberjar/lwjgl-example-$(VERSION)-standalone.jar \
		-H:Name=build/lwjgl-example \
		-H:+ReportExceptionStackTraces \
		-J-Dclojure.spec.skip-macros=true \
		-J-Dclojure.compiler.direct-linking=true \
		-J-Djava.library.path=./ \
		-H:ConfigurationFileDirectories=graal-configs/ \
		--initialize-at-build-time \
		-H:Log=registerResource: \
		-H:EnableURLProtocols=http,https \
		--enable-all-security-services \
		-H:+JNI \
		--report-unsupported-elements-at-runtime \
		--verbose \
		--allow-incomplete-classpath \
		--no-fallback \
		--no-server \
		"-J-Xmx6500m"
	cp build/lwjgl-example lwjgl-example

JAR_FILE=target/uberjar/lwjgl-example-$(VERSION)-standalone.jar

jar: $(JAR_FILE)

$(JAR_FILE):
	GRAALVM_HOME_HOME=$(GRAALVM_HOME) lein with-profiles +native-image uberjar

clean:
	-rm -rf build target jpack
	lein clean

linux-jpackage:
	-mkdir jpack
	cp target/uberjar/lwjgl-example-$(VERSION)-standalone.jar jpack
	cp libglfw.so liblwjgl_opengl.so liblwjgl.so jpack
	jpackage --name lwjgl-example --input jpack --main-jar lwjgl-example-$(VERSION)-standalone.jar
