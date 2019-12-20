package eu.quanticol.moonlight.compiler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;


public class MoonlightCompiler {

	private File workingDirectory;
	private ClassLoader classLoader;
	
	public MoonlightCompiler() throws IOException {
		this(Files.createTempDirectory(UUID.randomUUID().toString()));
	}
	
    public MoonlightCompiler(String workingDirectory) throws IOException {
    	this(Paths.get(workingDirectory));
    }

    public MoonlightCompiler(Path workindDirectory) throws IOException {
    	this(workindDirectory, true);
	}

	public MoonlightCompiler(Path path, boolean create) throws IOException {
		this.workingDirectory = new File( path.toUri() );
		if (!workingDirectory.exists()) {
			if (create) {
				this.workingDirectory.mkdir();
			} else {
				throw new IllegalArgumentException("Working directory "+path.toString()+" does not exits!");
			}
		} 
		this.classLoader = URLClassLoader.newInstance(new URL[]{this.workingDirectory.toURI().toURL()});
	}

	public <T> T getIstance(String packageName, String className, String source, Class<T> clazz) throws IOException, ReflectiveOperationException {
        String fileDir = packageName.replace(".", File.separator);
        
        Path tmp = Paths.get(workingDirectory.getAbsolutePath(),fileDir);

        File sourceDir = new File(tmp.toUri()); // On Windows running on C:\, this is C:\java.
        sourceDir.mkdirs();
        File sourceFile = new File(sourceDir, className + ".java");
        Files.write(sourceFile.toPath(), source.getBytes(StandardCharsets.UTF_8));
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null, null, null, sourceFile.getPath());
        Class<?> cls = Class.forName(packageName+"."+className, true, classLoader); // Should print "hello".
        return clazz.cast( cls.getDeclaredConstructor().newInstance() ); // Should print "world"

    }
}