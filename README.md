# ZKM-Gradle

## Install the plugin

Add before the plugins block in your build.gradle file:

```groovy
buildscript {
    repositories {
        mavenLocal()
        maven {
            url = uri("https://jitpack.io")
        }
    }
    dependencies {
        classpath "com.github.LucaFontanot:ZKM-Gradle:<version>"
    }
}
```
If you are using gradle kotlin:
```kotlin
buildscript {
    repositories {
        mavenLocal()
        maven {
            url = uri("https://jitpack.io")
        }
    }
    dependencies {
        classpath("com.github.LucaFontanot:ZKM-Gradle:<version>")
    }
}
```

Add after the plugin block, but before the dependencies block in your build.gradle file:

```groovy
apply plugin: "com.lucaf.zkm"
```
If you are using gradle kotlin:
```kotlin
apply(plugin = "com.lucaf.zkm")
```

## Classpath generation

The plugin will automatically generate the classpath for the ZKM obfuscator, you can also add your own classpath.

The collectAllClasspath setting does the following:
- If set to true, the plugin will collect all the gradle downloaded dependencies and add them to the classpath.
- If set to false, the plugin will only add the dependencies that are used in the project.

## Create the configuration

You can create a ZKM block in your build.gradle file to configure the plugin:

```groovy
zkm {
    //Plugin specific configuration
    zkmPath = "${System.getProperty('user.home')}/ZKM/ZKM.jar" // Path to the ZKM jar file
    inputJar = "${project.buildDir}/libs/${project.name}-${project.version}-all.jar" // Path to the input jar file
    outputJar = "${project.buildDir}/libs/${project.name}-${project.version}-obf.jar" // Path to the output jar file
    obfuscatePackages = ["com.app.example"] // List of packages to obfuscate, this will be applied as a filter in the open statement
    obfuscatePackagesExclude = ["com.app.example.exclude"] // List of packages to exclude from the obfuscation
    collectAllClasspath = false
    version = 23 //your licensed ZKM major version
    //ZKM exclusions configuration
    exclude = [ //List of exclusions as of the ZKM documentation
        "@*.Table *.*^ *", 
        ...
    ]
    unexlude = [ //List of unexclusions as of the ZKM documentation
        "@*.Table *.*^ *", 
        ...
    ]
    classPath = [ //List of class paths as of the ZKM documentation
        (String) "${Jvm.current().javaHome}/jmods"
        ...
    ]
    ignoreMissingReferences = [
        ...
    ]
    trimExclude = [
        ...
    ]
    trimUnexclude = [
        ...
    ]
    removeMethodCallsInclude = [
        ...
    ]
    removeMethodCallsExclude = [
        ...
    ]
    obfuscateFlowExclude = [
        ...
    ]
    obfuscateFlowUnexclude = [
        ...
    ]
    obfuscateExceptionsExclude = [
        ...
    ]
    obfuscateExceptionsUnexclude = [
        ...
    ]
    obfuscateExceptionsUnexclude = [
        ...
    ]
    stringEncryptionExclude = [
        ...
    ]
    stringEncryptionUnexclude = [
        ...
    ]
    integerEncryptionExclude = [
        ...
    ]
    integerEncryptionUnexclude = [
        ...
    ]
    longEncryptionExclude = [
        ...
    ]
    longEncryptionUnexclude = [
        ...
    ]
    groupings = [
        ...
    ]
    existingSerializedClasses = [
        ...
    ]
    classInitializationOrder = [
        [ "com.app.example.Class1", "com.app.example.Class2" ],
        ...
    ]
    accessedByReflection = [
        ...
    ]
    accessedByReflectionExclude = [
        ...
    ]
    obfuscateReferencesInclude = [
        ...
    ]
    methodParameterChangesInclude = [
        ...
    ]
    methodParameterChangesExclude = [
        ...
    ]
    methodParameterObfuscationInclude = [
        ...
    ]
    methodParameterObfuscationExclude = [
        ...
    ]
    
    //Obfuscation configuration
    changeLogFileIn = ""
    changeLogFileOut = ""
    aggressiveMethodRenaming = false
    newNameCharacters = "ASCII"
    keepInnerClassInfo = "true"
    keepGenericsInfo = true
    obfuscateFlow = "normal"
    exceptionObfuscation = "light"
    encryptStringLiterals = "enhanced"
    encryptIntegerConstants = "normal"
    encryptLongConstants = "normal"
    obfuscateReferences = "none"
    obfuscateReferenceStructures = "inSpecialClass"
    obfuscateReferencesPackage = ""
    autoReflectionPackage = ""
    autoReflectionHash = ""
    collapsePackagesWithDefault = ""
    mixedCaseClassNames = "ifInArchive"
    newPackageNameFile = ""
    newClassNameFile = ""
    newFieldNameFile = ""
    newMethodNameFile = ""
    lineNumbers = "delete"
    localVariables = "delete"
    methodParameters = "delete"
    newNamesPrefix = ""
    randomize = true
    uniqueClassNames = false
    uniqueMethodNames = false
    methodParameterChanges = "none"
    methodParameterChangesPackage = ""
    obfuscateParameters = "none"
    makeClassesPublic = false
    allClassesOpened = false
    deriveGroupingsFromInputChangeLog = false
    keepBalancedLocks = false
    preverify = true
    legalIdentifiers = true
    assumeRuntimeVersion = "1.8"
    hideFieldNames = true
    hideStaticMethodNames = true
    autoReflectionHandling = "normal"
}
```

If you are using kotlin just change the array syntax:
```kotlin
zkm {
    ...
    obfuscatePackages = listOf("com.app.example")
    ...
    exclude = listOf(
        "@*.Table *.*^ *", 
        ...
    )
}
```


## Run the obfuscation on the shadowJar

You can create a custom task that will run the obfuscation after the jar building:
Example:
```groovy
task obfuscate() {
    dependsOn shadowJar, zkmObfuscate
}
```
```kotlin
tasks.register("obfuscate") {
    dependsOn("shadowJar", "zkmObfuscate")
}
```
Then you can run the task with:
```shell
./gradlew obfuscate
```
With the configuration above, the plugin will obfuscate the jar file generated by the shadowJar task and output the obfuscated jar file in the same directory with the name `project.name-project.version-obf.jar`

## Use the KZM Annotations
Note, this only works on ZKM major version 23 and above.
Add to the dependencies block in your build.gradle file:
```groovy
dependencies {
    compileOnly "com.github.LucaFontanot:ZKM-Gradle:<version>"
}
```
Then you can use the annotations in your code:
```java
@ZKMMethodLevel(obfuscateFlow = FlowObfuscationPolicy.NONE, exceptionObfuscation = ExceptionObfuscationPolicy.HEAVY)
public void yourMethod(String arg1, int arg2) {
    ...
}
```
```java
@ZKMClassLevel(obfuscateFlow = FlowObfuscationPolicy.NONE, exceptionObfuscation = ExceptionObfuscationPolicy.HEAVY)
public class YourClass {
    ...
}
```