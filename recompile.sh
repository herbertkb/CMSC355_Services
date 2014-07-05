# recompile.sh

for folder in $(find . -type d -maxdepth 1 -not -name .git)
do
    echo $folder
    javac 
    #javac *.java
    #jar vmcf MANFIEST.MF $folder.jar *.class
    #cp $folder.jar ..
done
