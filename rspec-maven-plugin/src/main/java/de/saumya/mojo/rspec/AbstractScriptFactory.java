package de.saumya.mojo.rspec;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Properties;

public abstract class AbstractScriptFactory implements ScriptFactory {

	protected List<String> classpathElements;
	protected File summaryReport;
	protected File outputDir;
	protected String baseDir;
	protected String sourceDir;
	protected String reportPath;
	protected Properties systemProperties;
	protected File gemHome;
	protected File[] gemPaths;

	public void setClasspathElements(List<String> classpathElements) {
		this.classpathElements = classpathElements;
	}

	public void setSummaryReport(File summaryReport) {
		this.summaryReport = summaryReport;
	}
	
	public void setOutputDir(File outputDir) {
		this.outputDir = outputDir;
	}
	
	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}
	
	public void setSourceDir(String sourceDir) {
		this.sourceDir = sourceDir;
	}
	
	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}
	
	public void setSystemProperties(Properties systemProperties) {
		this.systemProperties = systemProperties;
	}
	
	public void setGemHome(File gemHome) {
		this.gemHome = gemHome;
	}
	
	public void setGemPaths(File[] gemPaths) {
		this.gemPaths = gemPaths;
	}
	
	protected abstract String getScriptName();
	
	public File getScriptFile() {
		return new File( outputDir, getScriptName() );
	}
	
	public void emit() throws Exception {
		String script = getScript();
		
		File scriptFile = getScriptFile();
		
		outputDir.mkdirs();

		FileWriter out = new FileWriter( scriptFile );
		
		try {
			out.write( script );
		} finally {
			if ( out != null ) {
				out.close();
			}
		}
	}

}
