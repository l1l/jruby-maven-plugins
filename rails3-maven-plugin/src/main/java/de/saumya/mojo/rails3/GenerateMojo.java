package de.saumya.mojo.rails3;

import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;

import de.saumya.mojo.ruby.gems.GemException;
import de.saumya.mojo.ruby.rails.RailsException;
import de.saumya.mojo.ruby.script.ScriptException;

/**
 * goal to run a generator
 * 
 * @goal generate
 * @requiresDependencyResolution test
 */
public class GenerateMojo extends AbstractRailsMojo {

    /**
     * arguments for the generate command
     * 
     * @parameter default-value="${generate.args}"
     */
    protected String generateArgs = null;

    /**
     * the name of the generator
     * 
     * @parameter default-value="${generator}"
     */
    protected String generator    = null;

    @Override
    protected void executeRails() throws MojoExecutionException,
            ScriptException, IOException, GemException, RailsException {
        this.manager.generate(this.gemsInstaller,
                              this.repoSession,
                              launchDirectory(),
                              this.generator,
                              joinArgs(this.generateArgs, this.args));
    }
}
