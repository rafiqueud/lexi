# Non Blocking Vert.x

Hexagonal architeture vert.x simple rest analyse text API.

## Lexicograph

I used the java compareTo() method that provides the lexical compareTo method:
documentation:
https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#compareTo(java.lang.String)


## Informations

Currently the persitence create a file word_file.txt

I'm starting to look for a more fit data structure to hold the information about character value or lexical order to gain some performance.

But in my tests with a 100k registers the performance goes well.

I focused in these initial solution to make a O(N) implementation, just to iterate one time in all words.

Tried to use must pure Java implementation to allows non blocking IO and do not block the vert.x event-loop because I don't know if its allowed to use external librarys.

Put just JUniter dependencies to create some simple tests.

## Running

```
mvn org.codehaus.mojo:exec-maven-plugin:exec -Dexec.executable=java -Dexec.args="-cp %classpath io.vertx.core.Launcher run test.Server"
```