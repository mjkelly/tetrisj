# Makefile for TetrisJ
# Fri Sep 23 20:54:23 PDT 2005

CLASSPATH="."
NAME=TetrisJ
#JAVAC=/opt/blackdown-jdk-1.4.2.02/bin/javac

# this is 1.4
JAVAC=/usr/bin/javac

COMPILE=${JAVAC} -classpath ${CLASSPATH}
FILES=TetrisJ/README TetrisJ/ChangeLog TetrisJ/COPYING TetrisJ/FEATURES
SRC_FILES=TetrisJ/Makefile TetrisJ/Manifest

# the jar depends on ALL the compiled sources, plus the manifest
${NAME}.jar: ${NAME}.class *.java Manifest
	cd .. && jar cmf ${NAME}/Manifest ${NAME}/${NAME}.jar ${NAME}/*.class

# this is just like all the .class dependencies below, the name is just abstracted
${NAME}.class: ${NAME}.java
	cd .. && ${COMPILE} ${NAME}/${NAME}.java

# make a fresh new set of class files
new:
	make clean
	make

# clear out the class files. not a full 'make clean', because it doesn't remove the JAR.
classclean:
	rm -f *.class

# run the application (via the jar file)
run:
	java -jar ${NAME}.jar

# clear out existing bytecode (class files, jar file).
clean:
	rm -f *.class ${NAME}.jar

# make source and jar distributions
dist:
	make srcdist
	make jardist

# I use this for preparing the source for uploading
srcdist:
	cd .. && tar zcvf TetrisJ-source.tgz TetrisJ/*.java ${FILES} ${SRC_FILES}

# and this to prepare the jar for uploading
# we make both tar.gz and .zip archives
jardist:
	cd .. && tar zcvf TetrisJ-jar.tgz TetrisJ/TetrisJ.jar ${FILES}
	cd .. && zip TetrisJ-jar.zip TetrisJ/TetrisJ.jar ${FILES}
