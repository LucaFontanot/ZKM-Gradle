package com.lucaf.zkm;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ZkmConfig {
    public String zkmPath = "";
    public String inputJar = "";
    public String outputJar = "";
    public boolean collectAllClasspath = false;
    public List<String> obfuscatePackages = new ArrayList<>();

    //Global Settings
    public List<String> exclude = new ArrayList<>();
    public List<String> unexclude = new ArrayList<>();
    public List<String> classPath = new ArrayList<>();
    public List<String> ignoreMissingReferences = new ArrayList<>();
    public List<String> trimExclude = new ArrayList<>();
    public List<String> trimUnexclude = new ArrayList<>();
    public List<String> removeMethodCallsInclude = new ArrayList<>();
    public List<String> removeMethodCallsExclude = new ArrayList<>();
    public List<String> obfuscateFlowExclude = new ArrayList<>();
    public List<String> obfuscateFlowUnexclude = new ArrayList<>();
    public List<String> obfuscateExceptionsExclude = new ArrayList<>();
    public List<String> obfuscateExceptionsUnexclude = new ArrayList<>();
    public List<String> stringEncryptionExclude = new ArrayList<>();
    public List<String> stringEncryptionUnexclude = new ArrayList<>();
    public List<String> integerEncryptionExclude = new ArrayList<>();
    public List<String> integerEncryptionUnexclude = new ArrayList<>();
    public List<String> longEncryptionExclude = new ArrayList<>();
    public List<String> longEncryptionUnexclude = new ArrayList<>();
    public List<String> groupings = new ArrayList<>();
    public List<String> existingSerializedClasses = new ArrayList<>();
    public List<List<String>> classInitializationOrder = new ArrayList<>();
    public List<String> accessedByReflection = new ArrayList<>();
    public List<String> accessedByReflectionExclude = new ArrayList<>();
    public List<String> obfuscateReferencesInclude = new ArrayList<>();
    public List<String> obfuscateReferencesExclude = new ArrayList<>();
    public List<String> methodParameterChangesInclude = new ArrayList<>();
    public List<String> methodParameterChangesExclude = new ArrayList<>();
    public List<String> methodParameterObfuscationInclude = new ArrayList<>();
    public List<String> methodParameterObfuscationExclude = new ArrayList<>();
    //Obfuscation settings
    public String changeLogFileIn = "";
    public String changeLogFileOut = "";
    public boolean aggressiveMethodRenaming = false;
    public String newNameCharacters = "ASCII";
    public String keepInnerClassInfo = "true";
    public boolean keepGenericsInfo = true;
    public String obfuscateFlow = "normal";
    public String exceptionObfuscation = "light";
    public String encryptStringLiterals = "enhanced";
    public String encryptIntegerConstants = "normal";
    public String encryptLongConstants = "normal";
    public String obfuscateReferences = "none";
    public String obfuscateReferenceStructures = "inSpecialClass";
    public String obfuscateReferencesPackage = "";
    public String autoReflectionPackage = "";
    public String autoReflectionHash = "";
    public String collapsePackagesWithDefault = "";
    public String mixedCaseClassNames = "ifInArchive";
    public String newPackageNameFile = "";
    public String newClassNameFile = "";
    public String newFieldNameFile = "";
    public String newMethodNameFile = "";
    public String lineNumbers = "delete";
    public String localVariables = "delete";
    public String methodParameters = "delete";
    public String newNamesPrefix = "";
    public boolean randomize = true;
    public boolean uniqueClassNames = false;
    public boolean uniqueMethodNames = false;
    public String methodParameterChanges = "none";
    public String methodParameterChangesPackage = "";
    public String obfuscateParameters = "none";
    public boolean makeClassesPublic = false;
    public boolean allClassesOpened = false;
    public boolean deriveGroupingsFromInputChangeLog = false;
    public boolean keepBalancedLocks = false;
    public boolean preverify = true;
    public boolean legalIdentifiers = true;
    public String assumeRuntimeVersion = "1.8";
    public boolean hideFieldNames = true;
    public boolean hideStaticMethodNames = true;
    public String autoReflectionHandling = "normal";
}
