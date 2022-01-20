package io.jenkins.plugins.generic.event.listener;

import com.cloudbees.hudson.plugins.folder.computed.FolderComputation;
import hudson.ExtensionList;
import hudson.model.*;
import io.jenkins.plugins.generic.event.Event;
import io.jenkins.plugins.generic.event.EventGlobalConfiguration;
import io.jenkins.plugins.generic.event.EventSender;
import jenkins.branch.BranchSource;
import jenkins.model.JenkinsLocationConfiguration;
import jenkins.plugins.git.GitBranchSCMHead;
import jenkins.plugins.git.GitSCMSource;
import jenkins.plugins.git.GitSampleRepoRule;
import jenkins.scm.api.SCMHead;
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
import org.jvnet.hudson.test.MockBuilder;
import org.jvnet.hudson.test.MockFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RunListenerTest {

    private static final String JENKINSFILE = m(
            "pipeline {",
            "  agent none",
            "  stages {",
            "    stage('Example') {",
            "      steps {",
            "        echo 'Hello World!'",
            "      }",
            "    }",
            "  }",
            "}");

    @ClassRule
    public static final BuildWatcher buildWatcher = new BuildWatcher();

    @Rule
    public final JenkinsRule r = new JenkinsRule();

    @Rule
    public final GitSampleRepoRule sampleRepo = new GitSampleRepoRule();

    @Mock
    EventSender mockSender;

    @Before
    public void setUp() {
        EventGlobalConfiguration config = EventGlobalConfiguration.get();
        config.setReceiver("http://localhost:8000");
        // simulate outside HTTP request
        JenkinsLocationConfiguration.get().setUrl(null);

        MockitoAnnotations.openMocks(this);
        ExtensionList<RunListener> listeners = ExtensionList.lookup(RunListener.class);
        listeners.forEach((listener) -> listener.setEventSender(mockSender));
    }

    @Test
    public void runSimplePipeline() throws Exception {
        doNothing().when(mockSender).send(any());

        final WorkflowRun run = runPipeline(JENKINSFILE);

        r.assertBuildStatusSuccess(run);
        r.assertLogContains("Hello World!", run);
        // initialize, started, completed and finalized
        verify(mockSender, times(4)).send(any(Event.class));
    }

    @Test
    public void runFreestyleProject() throws IOException, ExecutionException, InterruptedException {
        doNothing().when(mockSender).send(any());

        FreeStyleProject project = r.createFreeStyleProject("my-freestyle-project");
        project.getBuildersList().add(new MockBuilder(Result.SUCCESS));
        FreeStyleBuild build = project.scheduleBuild2(0).waitForStart();
        r.waitForCompletion(build);

        verify(mockSender, times(4)).send(any(Event.class));
    }

    @Test
    public void runMultiBranchPipeline() throws Exception {
        doNothing().when(mockSender).send(any());

        sampleRepo.init();
        sampleRepo.write("Jenkinsfile",
                "echo \"branch=${env.BRANCH_NAME}\"; node {checkout scm; echo readFile('file')}");
        sampleRepo.write("file", "initial content");
        sampleRepo.git("add", "Jenkinsfile");
        sampleRepo.git("commit", "--all", "--message=flow");
        sampleRepo.git("checkout", "-b", "dev");

        MockFolder folderProject = r.createFolder("my-devops-project");
        WorkflowMultiBranchProject project = folderProject.createProject(WorkflowMultiBranchProject.class, "my-multi-branch-pipeline");
        project.getSourcesList()
                .add(new BranchSource(new GitSCMSource(null, sampleRepo.toString(), "", "*", "", false)));
        project.getSCMSources().forEach(source -> assertEquals(project, source.getOwner()));

        Objects.requireNonNull(project.scheduleBuild2(0)).getFuture().get();
        WorkflowJob branchProject = findBranchProject(project, "dev");
        assertEquals(new GitBranchSCMHead("dev"), SCMHead.HeadByItem.findHead(branchProject));
        assertEquals(2, project.getItems().size());
        r.waitUntilNoActivity();

        WorkflowRun lastRun = branchProject.getLastBuild();
        r.assertLogContains("initial content", lastRun);
        r.assertLogContains("branch=dev", lastRun);

        verify(mockSender, times(8)).send(any(Event.class));
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

    private WorkflowRun runPipeline(String definition) throws Exception {
        MockFolder folderProject = r.createFolder("my-devops-project");
        WorkflowJob project = folderProject.createProject(WorkflowJob.class, "example-pipeline");
        project.setDefinition(new CpsFlowDefinition(definition, true));

        BooleanParameterDefinition skipDefinition = new BooleanParameterDefinition("skip", false, "Should we skip this step");
        ParametersDefinitionProperty paramsDefProperty = new ParametersDefinitionProperty(skipDefinition);
        project.addProperty(paramsDefProperty);

        WorkflowRun run = Objects.requireNonNull(project.scheduleBuild2(0)).waitForStart();
        r.waitForCompletion(run);
        return run;
    }

    private static String m(String... lines) {
        return String.join("\n", lines);
    }

}
