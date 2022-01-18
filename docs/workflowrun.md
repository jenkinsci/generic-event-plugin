# WorkflowRun Event

## WorkflowRun Initialize

```json
{
  "data": {
    "multiBranch": true,
    "parentFullName": "my-devops-project/my-multi-branch-pipeline",
    "run": {
      "_class": "org.jenkinsci.plugins.workflow.job.WorkflowRun",
      "actions": [
        {
          "_class": "hudson.model.ParametersAction",
          "parameters": [
            {
              "_class": "hudson.model.BooleanParameterValue",
              "name": "TOGGLE",
              "value": true
            }
          ]
        },
        {
          "_class": "org.jenkinsci.plugins.displayurlapi.actions.RunDisplayAction"
        },
        {
          "_class": "org.jenkinsci.plugins.pipeline.modeldefinition.actions.RestartDeclarativePipelineAction"
        },
        {},
        {
          "_class": "org.jenkinsci.plugins.workflow.job.views.FlowGraphAction"
        },
        {},
        {},
        {}
      ],
      "artifacts": [],
      "building": true,
      "description": null,
      "displayName": "#2",
      "duration": 0,
      "estimatedDuration": 1826,
      "executor": { "_class": "hudson.model.OneOffExecutor" },
      "fullDisplayName": "my-devops-project » my-multi-branch-pipeline » main #2",
      "id": "2",
      "keepLog": false,
      "number": 2,
      "queueId": 3,
      "result": null,
      "timestamp": 1642499607982,
      "changeSets": [],
      "culprits": [],
      "nextBuild": null,
      "previousBuild": { "number": 1 }
    },
    "projectName": "main"
  },
  "dataType": "io.jenkins.plugins.pipeline.event.data.WorkflowRunData",
  "id": "7cdd07b9-cf58-4d9a-9719-9849aaf93911",
  "source": "job/my-devops-project/job/my-multi-branch-pipeline/job/main/",
  "time": "2022-01-18T17:53:27.998+0800",
  "type": "run.initialize"
}
```

## WorkflowRun started

```json
{
  "data": {
    "multiBranch": true,
    "parentFullName": "my-devops-project/my-multi-branch-pipeline",
    "run": {
      "_class": "org.jenkinsci.plugins.workflow.job.WorkflowRun",
      "actions": [
        {
          "_class": "hudson.model.ParametersAction",
          "parameters": [
            {
              "_class": "hudson.model.BooleanParameterValue",
              "name": "TOGGLE",
              "value": true
            }
          ]
        },
        {
          "_class": "org.jenkinsci.plugins.displayurlapi.actions.RunDisplayAction"
        },
        {
          "_class": "org.jenkinsci.plugins.pipeline.modeldefinition.actions.RestartDeclarativePipelineAction"
        },
        {},
        {
          "_class": "org.jenkinsci.plugins.workflow.job.views.FlowGraphAction"
        },
        {},
        {},
        {}
      ],
      "artifacts": [],
      "building": true,
      "description": null,
      "displayName": "#2",
      "duration": 0,
      "estimatedDuration": 1826,
      "executor": { "_class": "hudson.model.OneOffExecutor" },
      "fullDisplayName": "my-devops-project » my-multi-branch-pipeline » main #2",
      "id": "2",
      "keepLog": false,
      "number": 2,
      "queueId": 3,
      "result": null,
      "timestamp": 1642499607982,
      "changeSets": [],
      "culprits": [],
      "nextBuild": null,
      "previousBuild": { "number": 1 }
    },
    "projectName": "main"
  },
  "dataType": "io.jenkins.plugins.pipeline.event.data.WorkflowRunData",
  "id": "6f2e1eab-4109-465e-ac2d-e2f0b9dc24ce",
  "source": "job/my-devops-project/job/my-multi-branch-pipeline/job/main/",
  "time": "2022-01-18T17:53:28.008+0800",
  "type": "run.started"
}
```

## WorkflowRun Completed

```json
{
  "data": {
    "multiBranch": true,
    "parentFullName": "my-devops-project/my-multi-branch-pipeline",
    "run": {
      "_class": "org.jenkinsci.plugins.workflow.job.WorkflowRun",
      "actions": [
        {
          "_class": "hudson.model.ParametersAction",
          "parameters": [
            {
              "_class": "hudson.model.BooleanParameterValue",
              "name": "TOGGLE",
              "value": true
            }
          ]
        },
        { "_class": "jenkins.scm.api.SCMRevisionAction" },
        { "_class": "org.jenkinsci.plugins.workflow.libs.LibrariesAction" },
        {},
        {
          "_class": "org.jenkinsci.plugins.displayurlapi.actions.RunDisplayAction"
        },
        {
          "_class": "org.jenkinsci.plugins.pipeline.modeldefinition.actions.RestartDeclarativePipelineAction"
        },
        {},
        {
          "_class": "org.jenkinsci.plugins.workflow.job.views.FlowGraphAction"
        },
        {},
        {},
        {}
      ],
      "artifacts": [],
      "building": false,
      "description": null,
      "displayName": "#2",
      "duration": 485,
      "estimatedDuration": 1156,
      "executor": { "_class": "hudson.model.OneOffExecutor" },
      "fullDisplayName": "my-devops-project » my-multi-branch-pipeline » main #2",
      "id": "2",
      "keepLog": false,
      "number": 2,
      "queueId": 3,
      "result": "SUCCESS",
      "timestamp": 1642499607982,
      "changeSets": [],
      "culprits": [],
      "nextBuild": null,
      "previousBuild": { "number": 1 }
    },
    "projectName": "main"
  },
  "dataType": "io.jenkins.plugins.pipeline.event.data.WorkflowRunData",
  "id": "8e285ad8-b11a-4fe9-9cfe-adfe52d2b1fe",
  "source": "job/my-devops-project/job/my-multi-branch-pipeline/job/main/",
  "time": "2022-01-18T17:53:28.484+0800",
  "type": "run.completed"
}
```

## WorkflowRun Finalized

```json
{
  "data": {
    "multiBranch": true,
    "parentFullName": "my-devops-project/my-multi-branch-pipeline",
    "run": {
      "_class": "org.jenkinsci.plugins.workflow.job.WorkflowRun",
      "actions": [
        {
          "_class": "hudson.model.ParametersAction",
          "parameters": [
            {
              "_class": "hudson.model.BooleanParameterValue",
              "name": "TOGGLE",
              "value": true
            }
          ]
        },
        { "_class": "jenkins.scm.api.SCMRevisionAction" },
        { "_class": "org.jenkinsci.plugins.workflow.libs.LibrariesAction" },
        {},
        {
          "_class": "org.jenkinsci.plugins.displayurlapi.actions.RunDisplayAction"
        },
        {
          "_class": "org.jenkinsci.plugins.pipeline.modeldefinition.actions.RestartDeclarativePipelineAction"
        },
        {},
        {
          "_class": "org.jenkinsci.plugins.workflow.job.views.FlowGraphAction"
        },
        {},
        {},
        {}
      ],
      "artifacts": [],
      "building": false,
      "description": null,
      "displayName": "#2",
      "duration": 485,
      "estimatedDuration": 1156,
      "executor": { "_class": "hudson.model.OneOffExecutor" },
      "fullDisplayName": "my-devops-project » my-multi-branch-pipeline » main #2",
      "id": "2",
      "keepLog": false,
      "number": 2,
      "queueId": 3,
      "result": "SUCCESS",
      "timestamp": 1642499607982,
      "changeSets": [],
      "culprits": [],
      "nextBuild": null,
      "previousBuild": { "number": 1 }
    },
    "projectName": "main"
  },
  "dataType": "io.jenkins.plugins.pipeline.event.data.WorkflowRunData",
  "id": "c9d2e7a6-ebc6-477e-afde-4267fcfd8d34",
  "source": "job/my-devops-project/job/my-multi-branch-pipeline/job/main/",
  "time": "2022-01-18T17:53:28.530+0800",
  "type": "run.finalized"
}
```

## WorkflowRun Deleted

```json
{
  "data": {
    "multiBranch": true,
    "parentFullName": "my-devops-project/my-multi-branch-pipeline",
    "run": {
      "_class": "org.jenkinsci.plugins.workflow.job.WorkflowRun",
      "actions": [
        {
          "_class": "hudson.model.ParametersAction",
          "parameters": [
            {
              "_class": "hudson.model.BooleanParameterValue",
              "name": "TOGGLE",
              "value": true
            }
          ]
        },
        { "_class": "jenkins.scm.api.SCMRevisionAction" },
        { "_class": "org.jenkinsci.plugins.workflow.libs.LibrariesAction" },
        {},
        {
          "_class": "org.jenkinsci.plugins.displayurlapi.actions.RunDisplayAction"
        },
        {
          "_class": "org.jenkinsci.plugins.pipeline.modeldefinition.actions.RestartDeclarativePipelineAction"
        },
        {},
        {
          "_class": "org.jenkinsci.plugins.workflow.job.views.FlowGraphAction"
        },
        {},
        {},
        {}
      ],
      "artifacts": [],
      "building": false,
      "description": null,
      "displayName": "#2",
      "duration": 443,
      "estimatedDuration": 1113,
      "executor": null,
      "fullDisplayName": "my-devops-project » my-multi-branch-pipeline » main #2",
      "id": "2",
      "keepLog": false,
      "number": 2,
      "queueId": 3,
      "result": "SUCCESS",
      "timestamp": 1642499845976,
      "changeSets": [],
      "culprits": [],
      "nextBuild": null,
      "previousBuild": { "number": 1 }
    },
    "projectName": "main"
  },
  "dataType": "io.jenkins.plugins.pipeline.event.data.WorkflowRunData",
  "id": "6d9e79f9-e54f-4a3d-b09f-c10ef281b172",
  "source": "job/my-devops-project/job/my-multi-branch-pipeline/job/main/",
  "time": "2022-01-18T17:57:26.556+0800",
  "type": "run.deleted"
}
```

