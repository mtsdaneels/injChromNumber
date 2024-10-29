JAVAC = javac

JAVA_COMPILE_OPTIONS = 
JAVA_OPTIONS = 

JAVA_MAIN_CLASS = src.Main
JAVA_SOURCES = $(wildcard src/*.java)
JAVA_CLASSES = $(patsubst src/%.java, target/classes/%.class, $(JAVA_SOURCES))
JAR_NAME = injChromNumber
MANIFEST = META-INF/MANIFEST.MF

default: compile jar

# Compile the Java source files
compile: $(JAVA_CLASSES)
	$(info Java source files: $(JAVA_SOURCES))
	$(info Java class files: $(JAVA_CLASSES))

# Create jar file
jar: 
	jar -cvfm $(JAR_NAME).jar $(MANIFEST)  target/classes/src/Main.class

# Clean the target directory
clean:
	rm -rf target
	rm -f $(JAR_NAME).jar

# Compile the Java source files
target/classes/%.class: src/%.java
	$(JAVAC) $(JAVA_COMPILE_OPTIONS) -d target/classes $<

# Create the target directory
target/classes:
	mkdir -p target/classes

# Make the target directory a dependency of the Java class files
$(JAVA_CLASSES): target/classes
compile: target/classes
clean: target/classes
default: target/classes

