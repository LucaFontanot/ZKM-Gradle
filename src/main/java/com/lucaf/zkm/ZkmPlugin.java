package com.lucaf.zkm;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.process.ExecOperations;
import org.gradle.process.ExecResult;
import org.gradle.process.ExecSpec;

import javax.inject.Inject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZkmPlugin implements Plugin<Project> {


    private final ExecOperations execOperations;

    @Inject
    public ZkmPlugin(ExecOperations execOperations) {
        this.execOperations = execOperations;
    }

    @Override
    public void apply(Project target) {
        ZkmConfig config = target.getExtensions().create("zkm", ZkmConfig.class);
        target.getTasks().create("zkmObfuscate", task -> {
            task.doLast(t -> {
                try {
                    System.out.println("Starting ZKM obfuscation...");
                    ZkmGenerator generator = new ZkmGenerator(config);
                    Set<File> classPath = getClassPath(target, config.collectAllClasspath);
                    classPath.forEach(file -> generator.addClassPath(file.getAbsolutePath()));
                    generator.addClassPath(config.getInputJar());
                    Path output = Paths.get(target.getBuildDir().getAbsolutePath() + "/zkm");
                    if (!output.toFile().exists()) {
                        output.toFile().mkdirs();
                    }
                    String script = generator.generateConfig();
                    System.out.println("Generated ZKM script");
                    File scriptFile = output.resolve("config.zkm").toFile();
                    Files.write(scriptFile.toPath(), script.getBytes());
                    System.out.println("Starting execution of ZKM...");
                    ExecResult result = execOperations.exec(new Action<ExecSpec>() {
                        @Override
                        public void execute(ExecSpec execSpec) {
                            System.out.println("Executing: java -jar " + config.getZkmPath() + " " + scriptFile.getAbsolutePath());
                            execSpec.setIgnoreExitValue(false);
                            execSpec.commandLine("java", "-jar", config.getZkmPath(), scriptFile.getAbsolutePath());
                        }
                    });
                    if (result.getExitValue() != 0) {
                        throw new RuntimeException("ZKM obfuscation failed with exit code: " + result.getExitValue());
                    }
                    System.out.println("ZKM obfuscation completed.");
                    File tempJar = output.resolve("temp.jar").toFile();
                    if (tempJar.exists()) {
                        tempJar.delete();
                    }
                    ZipFile original = new ZipFile(config.getInputJar());
                    ZipOutputStream outputJar = new ZipOutputStream(new FileOutputStream(tempJar));
                    ZipFile obfuscated = new ZipFile(config.getOutputJar());
                    Enumeration<? extends ZipEntry> entries = obfuscated.entries();
                    List<String> outputJarFiles = new ArrayList<>();
                    while (entries.hasMoreElements()) {
                        ZipEntry entry = entries.nextElement();
                        if (!entry.isDirectory()) {
                            try (InputStream entryStream = obfuscated.getInputStream(entry)) {
                                outputJar.putNextEntry(new ZipEntry(entry.getName()));
                                byte[] buffer = new byte[1024];
                                int bytesRead;
                                while ((bytesRead = entryStream.read(buffer)) != -1) {
                                    outputJar.write(buffer, 0, bytesRead);
                                }
                                outputJar.closeEntry();
                                outputJarFiles.add(entry.getName());
                            }
                        }
                    }
                    List<String> blackListedFolders = generator.getBlackListedFolders();
                    List<String> whiteListedFolders = generator.getWhiteListedFolders();
                    Enumeration<? extends ZipEntry> originalEntries = original.entries();
                    while (originalEntries.hasMoreElements()) {
                        ZipEntry entry = originalEntries.nextElement();
                        boolean skip = false;
                        for (String folder : blackListedFolders) {
                            if (entry.getName().startsWith(folder)) {
                                skip = true;
                                break;
                            }
                        }
                        boolean force = false;
                        for (String folder : whiteListedFolders) {
                            if (entry.getName().startsWith(folder)) {
                                force = true;
                                break;
                            }
                        }
                        if (!entry.isDirectory() && (!skip || force) && obfuscated.getEntry(entry.getName()) == null && !outputJarFiles.contains(entry.getName())) {
                            try (InputStream entryStream = original.getInputStream(entry)) {
                                outputJar.putNextEntry(new ZipEntry(entry.getName()));
                                byte[] buffer = new byte[1024];
                                int bytesRead;
                                while ((bytesRead = entryStream.read(buffer)) != -1) {
                                    outputJar.write(buffer, 0, bytesRead);
                                }
                                outputJar.closeEntry();
                                outputJarFiles.add(entry.getName());
                            }
                        }
                    }
                    outputJar.close();
                    original.close();
                    obfuscated.close();
                    File obfuscatedFile = new File(config.getOutputJar());
                    if (obfuscatedFile.delete()) {
                        tempJar.renameTo(obfuscatedFile);
                    }
                } catch (Exception e) {
                    System.err.println("Error during ZKM obfuscation: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            });
        });
    }

    public void scanFolderForJar(File folder, Set<File> jars) {
        folder.listFiles(file -> {
            if (file.isDirectory()) {
                scanFolderForJar(file, jars);
            } else if (file.getName().endsWith(".jar")) {
                jars.add(file);
            }
            return false;
        });
    }

    public Set<File> getClassPath(Project p, boolean all) {
        Set<File> deps = p.getConfigurations().getByName("compileClasspath")
                .getResolvedConfiguration()
                .getResolvedArtifacts()
                .stream()
                .map(artifact -> artifact.getFile())
                .collect(Collectors.toSet());
        if (all) {
            File localFolder = Paths.get(System.getProperty("user.home") + "/.gradle/caches/modules-2/files-2.1").toFile();
            scanFolderForJar(localFolder, deps);
        }
        return deps;
    }
}
