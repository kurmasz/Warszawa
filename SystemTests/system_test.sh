#!/bin/sh

# The System Tests serve two primary purposes:
# (1) Verify that the .jar file contains all the necessary library code.  (In other words,
# make sure all the code runs outside the context of the IDE or ant configuration.)
#
# (2) To verify that methods with external interactions (e.g., stdin, stdout, System.exit()) work correctly.
#


#
# Variables
#
jar=../dist/warszawa-1.1.jar
actual_output=actual_output

#
# Set up output directory
#
mv ${actual_output} ${actual_output}.old.$$
mkdir ${actual_output}


#
# Test the default behavior
#
java -jar ${jar} > ${actual_output}/runJar

#
# Compile
#
mkdir -p out
rm -rf out/*
javac -cp ${jar} src/*/*.java -d out

#
#  Tests 1
#
java -cp out:${jar} warszawaTest.SystemTest  > ${actual_output}/stdout 2> ${actual_output}/stderr < input/stdin


#
# "Quit" tests
#
numQuitTests=`java -cp out:${jar} warszawaTest.VerifyQuit`
for ((i=0;i<${numQuitTests};++i))
do
 exit_value=`expr $i + 100`
 java -cp out:${jar} warszawaTest.VerifyQuit ${i} ${exit_value}>> ${actual_output}/quit_report 2>> ${actual_output}/quit_report_error
 if [[ $? != ${exit_value} ]] ; then
    echo "Exit value incorrect for VerifyQuit ${i}"
    exit
 fi
done


#
# Check the output
#
diff ${actual_output} expected_output