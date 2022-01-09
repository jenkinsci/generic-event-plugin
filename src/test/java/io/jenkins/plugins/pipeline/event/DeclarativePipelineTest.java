package io.jenkins.plugins.pipeline.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.cloudbees.hudson.plugins.folder.computed.FolderComputation;

import jenkins.branch.OrganizationFolder;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.BuildWatcher;
import org.jvnet.hudson.test.JenkinsRule;

import hudson.ExtensionList;
import jenkins.branch.BranchSource;
import jenkins.plugins.git.GitBranchSCMHead;
import jenkins.plugins.git.GitSCMSource;
import jenkins.plugins.git.GitSampleRepoRule;
import jenkins.scm.api.SCMHead;

public class DeclarativePipelineTest {

    @ClassRule
    public static BuildWatcher buildWatcher = new BuildWatcher();

    @Rule
    public JenkinsRule r = new JenkinsRule();

    @Rule
    public GitSampleRepoRule sampleRepo = new GitSampleRepoRule();

    @Before
    public void setUp() {
        EventGlobalConfiguration config = EventGlobalConfiguration.get();
        config.setReceiver("http://localhost:8000");
        ExtensionList<WorkflowRunListener> listeners = ExtensionList.lookup(WorkflowRunListener.class);
        listeners.forEach((listener) -> {
            listener.setEventSender(new HttpEventSender());
        });
    }

    @Test
    public void smoke() throws Exception {
        final WorkflowRun run = runPipeline(m(
                "pipeline {",
                "  agent none",
                "  stages {",
                "    stage('Example') {",
                "      steps {",
                "        echo 'Hello World!'",
                "      }",
                "    }",
                "  }",
                "}"));
        // final WorkflowRun run = runPipeline("echo 'Hello World!'");
        r.assertBuildStatusSuccess(run);
        r.assertLogContains("Hello World!", run);
    }

    @Test
    public void multiBranchSmoke() throws Exception {
        sampleRepo.init();
        sampleRepo.write("Jenkinsfile",
                "echo \"branch=${env.BRANCH_NAME}\"; node {checkout scm; echo readFile('file')}");
        sampleRepo.write("file", "initial content");
        sampleRepo.git("add", "Jenkinsfile");
        sampleRepo.git("commit", "--all", "--message=flow");
        sampleRepo.git("checkout", "-b", "dev");

        final OrganizationFolder folderProject = r.createProject(OrganizationFolder.class, "my-devops-project");
        WorkflowMultiBranchProject project = new WorkflowMultiBranchProject(folderProject, "my-multi-branch-pipeline");
//        WorkflowMultiBranchProject project = r.createProject(WorkflowMultiBranchProject.class, "multi-branch-pipeline");
        project.getSourcesList()
                .add(new BranchSource(new GitSCMSource(null, sampleRepo.toString(), "", "*", "", false)));
        project.getSCMSources().forEach(source -> {
            assertEquals(project, source.getOwner());
        });

        project.scheduleBuild2(0).getFuture().get();
        WorkflowJob branchProject = findBranchProject(project, "dev");
        assertEquals(new GitBranchSCMHead("dev"), SCMHead.HeadByItem.findHead(branchProject));
        assertEquals(2, project.getItems().size());
        r.waitUntilNoActivity();

        WorkflowRun lastRun = branchProject.getLastBuild();
        r.assertLogContains("initial content", lastRun);
        r.assertLogContains("branch=dev", lastRun);
    }

    private WorkflowJob findBranchProject(WorkflowMultiBranchProject project, String name)
            throws IOException, InterruptedException {
        WorkflowJob job = project.getItem(name);
        showIndexing(project);
        if (job == null) {
            fail(name + " project not found in " + project.getName());
        }
        return job;
    }

    private void showIndexing(WorkflowMultiBranchProject project) throws IOException, InterruptedException {
        FolderComputation<?> indexing = project.getIndexing();
        System.out.println("---%<---" + indexing.getUrl());
        indexing.writeWholeLogTo(System.out);
        System.out.println("---%>---");
    }

    private WorkflowRun runPipeline(String definition) throws IOException, InterruptedException, ExecutionException {
        final WorkflowJob project = r.createProject(WorkflowJob.class, "example");
        project.setDefinition(new CpsFlowDefinition(definition, true));
        final WorkflowRun run = project.scheduleBuild2(0).waitForStart();
        r.waitForCompletion(run);
        return run;
    }

    private static String m(String... lines) {
        return String.join("\n", lines);
    }

}
