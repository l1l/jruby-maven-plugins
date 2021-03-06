package de.saumya.mojo.jruby;

import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import de.saumya.mojo.ruby.script.ScriptException;

/**
 * maven wrapper around IRB. deprecated use gem:irb instead
 * 
 * @goal irb
 * @requiresDependencyResolution test
 */
@Deprecated
public class IRBMojo extends AbstractJRubyMojo {

    /**
     * arguments for the irb command.
     * <br/>
     * Command line -Dirb.args=...
     * 
     * @parameter expression="${irb.args}"
     */
    protected String irbArgs = null;

    /**
     * launch IRB in a swing window.
     * <br/>
     * Command line -Dirb.swing=...
     * 
     * @parameter default-value="${irb.swing}"
     */
    protected boolean swing = false;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        // make sure the whole things run in the same process
        this.jrubyFork = false;
        super.execute();
    }

    @Override
    public void executeJRuby() throws MojoExecutionException, ScriptException,
            IOException {
        this.factory.newScriptFromJRubyJar(
                this.swing ? "jirb_swing" : "jirb")
                .addArgs(this.irbArgs).addArgs(this.args).execute();
    }
}
