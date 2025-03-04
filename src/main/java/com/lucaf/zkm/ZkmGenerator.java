package com.lucaf.zkm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ZkmGenerator {
    ObjectMapper mapper = new ObjectMapper();
    HashMap<String, String> obfuscationMap = new HashMap<>();
    List<String> classPaths = new ArrayList<>();
    String open;
    String save;

    final ZkmConfig config;

    @Getter
    List<String> blackListedFolders = new ArrayList<>();

    public String buildOpenFilters(List<String> packages) throws JsonProcessingException {
        blackListedFolders.clear();
        List<String> parsedPackages = new ArrayList<>();
        for (String s : packages) {
            String base = s.replace(".", "/");
            parsedPackages.add(mapper.writeValueAsString(base + "/**/*.class"));
            parsedPackages.add(mapper.writeValueAsString(base + "/*.class"));
            blackListedFolders.add(base);
        }

        return "{ " + String.join(" || ", parsedPackages) + " }";
    }

    public ZkmGenerator(ZkmConfig config) {
        this.config = config;
        try {
            classPaths.addAll(config.getClassPath());

            open = mapper.writeValueAsString(config.getInputJar());

            if (!config.getObfuscatePackages().isEmpty()) {
                open += " " + buildOpenFilters(config.getObfuscatePackages());
            }
            save = mapper.writeValueAsString(config.getOutputJar());

            obfuscationMap.put("changeLogFileIn", config.getChangeLogFileIn());
            obfuscationMap.put("changeLogFileOut", config.getChangeLogFileOut());
            obfuscationMap.put("aggressiveMethodRenaming", String.valueOf(config.isAggressiveMethodRenaming()));
            obfuscationMap.put("newNameCharacters", config.getNewNameCharacters());
            obfuscationMap.put("keepInnerClassInfo", config.getKeepInnerClassInfo());
            obfuscationMap.put("keepGenericsInfo", String.valueOf(config.isKeepGenericsInfo()));
            obfuscationMap.put("obfuscateFlow", config.getObfuscateFlow());
            obfuscationMap.put("exceptionObfuscation", config.getExceptionObfuscation());
            obfuscationMap.put("encryptStringLiterals", config.getEncryptStringLiterals());
            obfuscationMap.put("encryptIntegerConstants", config.getEncryptIntegerConstants());
            obfuscationMap.put("encryptLongConstants", config.getEncryptLongConstants());
            obfuscationMap.put("obfuscateReferences", config.getObfuscateReferences());
            obfuscationMap.put("obfuscateReferenceStructures", config.getObfuscateReferenceStructures());
            obfuscationMap.put("obfuscateReferencesPackage", config.getObfuscateReferencesPackage());
            obfuscationMap.put("autoReflectionPackage", config.getAutoReflectionPackage());
            obfuscationMap.put("autoReflectionHash", config.getAutoReflectionHash());
            obfuscationMap.put("collapsePackagesWithDefault", config.getCollapsePackagesWithDefault());
            obfuscationMap.put("mixedCaseClassNames", config.getMixedCaseClassNames());
            obfuscationMap.put("newPackageNameFile", config.getNewPackageNameFile());
            obfuscationMap.put("newClassNameFile", config.getNewClassNameFile());
            obfuscationMap.put("newFieldNameFile", config.getNewFieldNameFile());
            obfuscationMap.put("newMethodNameFile", config.getNewMethodNameFile());
            obfuscationMap.put("lineNumbers", config.getLineNumbers());
            obfuscationMap.put("localVariables", config.getLocalVariables());
            obfuscationMap.put("methodParameters", config.getMethodParameters());
            obfuscationMap.put("newNamesPrefix", config.getNewNamesPrefix());
            obfuscationMap.put("randomize", String.valueOf(config.isRandomize()));
            obfuscationMap.put("uniqueClassNames", String.valueOf(config.isUniqueClassNames()));
            obfuscationMap.put("uniqueMethodNames", String.valueOf(config.isUniqueMethodNames()));
            obfuscationMap.put("methodParameterChanges", config.getMethodParameterChanges());
            obfuscationMap.put("methodParameterChangesPackage", config.getMethodParameterChangesPackage());
            obfuscationMap.put("obfuscateParameters", config.getObfuscateParameters());
            if (config.version >= 22) {
                obfuscationMap.put("makeClassesPublic", String.valueOf(config.isMakeClassesPublic()));
            }
            obfuscationMap.put("allClassesOpened", String.valueOf(config.isAllClassesOpened()));
            obfuscationMap.put("deriveGroupingsFromInputChangeLog", String.valueOf(config.isDeriveGroupingsFromInputChangeLog()));
            obfuscationMap.put("keepBalancedLocks", String.valueOf(config.isKeepBalancedLocks()));
            obfuscationMap.put("preverify", String.valueOf(config.isPreverify()));
            obfuscationMap.put("legalIdentifiers", String.valueOf(config.isLegalIdentifiers()));
            obfuscationMap.put("assumeRuntimeVersion", config.getAssumeRuntimeVersion());
            obfuscationMap.put("hideFieldNames", String.valueOf(config.isHideFieldNames()));
            obfuscationMap.put("hideStaticMethodNames", String.valueOf(config.isHideStaticMethodNames()));
            obfuscationMap.put("autoReflectionHandling", config.getAutoReflectionHandling());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void addClassPath(String classPath) {
        classPaths.add(classPath);
    }

    public String arrayToString(List<String> list) throws JsonProcessingException {
        StringBuilder builder = new StringBuilder();
        for (String s : list) {
            builder.append(mapper.writeValueAsString(s)).append("\n");
        }
        return builder.toString();
    }

    static final List<String> parsedParameters = Arrays.asList("newNamesPrefix", "assumeRuntimeVersion","changeLogFileIn","looseChangeLogFileIn","changeLogFileOut","obfuscateReferencesPackage","autoReflectionPackage","autoReflectionHash","collapsePackagesWithDefault","newPackageNameFile","newClassNameFile","newFieldNameFile","newMethodNameFile","methodParameterChangesPackage");

    public String mapToString(HashMap<String, String> map) throws JsonProcessingException {
        StringBuilder builder = new StringBuilder();
        for (String key : map.keySet()) {
            if (map.get(key).isEmpty()) {
                continue;
            }
            if (parsedParameters.contains(key)) {
                builder.append(key).append("=").append(mapper.writeValueAsString(map.get(key))).append("\n");
            } else {
                builder.append(key).append("=").append(map.get(key)).append("\n");
            }
        }
        return builder.toString();
    }


    public String generateConfig() throws JsonProcessingException {
        StringBuilder config = new StringBuilder();
        for (String c : this.config.getClassPathExclude()){
            classPaths.removeIf(s -> s.contains(c));
        }
        config.append("classpath ").append(arrayToString(classPaths)).append(";\n");
        config.append("open ").append(open).append(";\n");
        for (String s : this.config.getExclude()) {
            config.append("exclude ").append(s).append(";\n");
        }
        for (String s : this.config.getUnexclude()) {
            config.append("unexclude ").append(s).append(";\n");
        }
        for (String s : this.config.getIgnoreMissingReferences()) {
            config.append("ignoreMissingReferences ").append(s).append(";\n");
        }
        for (String s : this.config.getTrimExclude()) {
            config.append("trimExclude ").append(s).append(";\n");
        }
        for (String s : this.config.getTrimUnexclude()) {
            config.append("trimUnexclude ").append(s).append(";\n");
        }
        for (String s : this.config.getRemoveMethodCallsInclude()) {
            config.append("removeMethodCallsInclude ").append(s).append(";\n");
        }
        for (String s : this.config.getRemoveMethodCallsExclude()) {
            config.append("removeMethodCallsExclude ").append(s).append(";\n");
        }
        for (String s : this.config.getObfuscateFlowExclude()) {
            config.append("obfuscateFlowExclude ").append(s).append(";\n");
        }
        for (String s : this.config.getObfuscateFlowUnexclude()) {
            config.append("obfuscateFlowUnexclude ").append(s).append(";\n");
        }
        if (this.config.version >= 23) {
            for (String s : this.config.getObfuscateExceptionsExclude()) {
                config.append("obfuscateExceptionsExclude ").append(s).append(";\n");
            }
            for (String s : this.config.getObfuscateExceptionsUnexclude()) {
                config.append("obfuscateExceptionsUnexclude ").append(s).append(";\n");
            }
        }
        for (String s : this.config.getStringEncryptionExclude()) {
            config.append("stringEncryptionExclude ").append(s).append(";\n");
        }
        for (String s : this.config.getStringEncryptionUnexclude()) {
            config.append("stringEncryptionUnexclude ").append(s).append(";\n");
        }
        for (String s : this.config.getIntegerEncryptionExclude()) {
            config.append("integerEncryptionExclude ").append(s).append(";\n");
        }
        for (String s : this.config.getIntegerEncryptionUnexclude()) {
            config.append("integerEncryptionUnexclude ").append(s).append(";\n");
        }
        for (String s : this.config.getLongEncryptionExclude()) {
            config.append("longEncryptionExclude ").append(s).append(";\n");
        }
        for (String s : this.config.getLongEncryptionUnexclude()) {
            config.append("longEncryptionUnexclude ").append(s).append(";\n");
        }
        for (String s : this.config.getGroupings()) {
            config.append("groupings {").append(s).append("};\n");
        }
        for (String s : this.config.getExistingSerializedClasses()) {
            config.append("existingSerializedClasses ").append(s).append(";\n");
        }
        if (!this.config.getClassInitializationOrder().isEmpty()) {
            List<String> tmpConditions = new ArrayList<>();
            for (List<String> s : this.config.getClassInitializationOrder()) {
                tmpConditions.add(s.get(0) + " > " + s.get(1));
            }
            config.append("classInitializationOrder ").append(String.join(" and ", tmpConditions)).append(";\n");
        }
        for (String s : this.config.getAccessedByReflection()) {
            config.append("accessedByReflection ").append(s).append(";\n");
        }
        for (String s : this.config.getAccessedByReflectionExclude()) {
            config.append("accessedByReflectionExclude ").append(s).append(";\n");
        }
        for (String s : this.config.getObfuscateReferencesInclude()) {
            config.append("obfuscateReferencesInclude ").append(s).append(";\n");
        }
        for (String s : this.config.getObfuscateReferencesExclude()) {
            config.append("obfuscateReferencesExclude ").append(s).append(";\n");
        }
        for (String s : this.config.getMethodParameterChangesInclude()) {
            config.append("methodParameterChangesInclude ").append(s).append(";\n");
        }
        for (String s : this.config.getMethodParameterChangesExclude()) {
            config.append("methodParameterChangesExclude ").append(s).append(";\n");
        }
        for (String s : this.config.getMethodParameterObfuscationInclude()) {
            config.append("methodParameterObfuscationInclude ").append(s).append(";\n");
        }
        for (String s : this.config.getMethodParameterObfuscationExclude()) {
            config.append("methodParameterObfuscationExclude ").append(s).append(";\n");
        }
        for (int i = 0;i < this.config.getIterations();i++) {
            if (i>=1){
                this.config.setAutoReflectionHandling("none");
            }
            config.append("obfuscate ").append(mapToString(obfuscationMap)).append(";\n");
        }
        config.append("saveAll ").append(save).append(";\n");
        return config.toString();
    }
}
