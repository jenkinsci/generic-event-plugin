# Run Event

## Run Initialize

```json
{
  "data":     {
    "_class": "io.jenkins.plugins.generic.event.data.WorkflowRunData",
    "actions":         [
      {
        "_class": "hudson.model.ParametersAction",
        "parameters": [                {
          "_class": "hudson.model.BooleanParameterValue",
          "name": "skip",
          "value": false
        }]
      },
      {"_class": "org.jenkinsci.plugins.displayurlapi.actions.RunDisplayAction"},
      {"_class": "org.jenkinsci.plugins.pipeline.modeldefinition.actions.RestartDeclarativePipelineAction"},
      {},
      {"_class": "org.jenkinsci.plugins.workflow.job.views.FlowGraphAction"},
      {},
      {},
      {}
    ],
    "artifacts": [],
    "building": true,
    "description": null,
    "displayName": "#1",
    "duration": 0,
    "estimatedDuration": -1,
    "executor": {"_class": "hudson.model.OneOffExecutor"},
    "fullDisplayName": "my-devops-project » example-pipeline #1",
    "id": "1",
    "keepLog": false,
    "number": 1,
    "queueId": 1,
    "result": null,
    "timestamp": 1644126495293,
    "changeSets": [],
    "culprits": [],
    "nextBuild": null,
    "previousBuild": null,
    "_multiBranch": false,
    "_parentFullName": "my-devops-project",
    "_projectName": "example-pipeline"
  },
  "dataType": "org.jenkinsci.plugins.workflow.job.WorkflowRun",
  "id": "50c33b0e-d7f1-4a34-b57e-bb82cd453894",
  "source": "job/my-devops-project/job/example-pipeline/",
  "time": "2022-02-06T13:48:15.307+0800",
  "type": "run.initialize"
}
```

## Run started

```json
{
  "data":     {
    "_class": "io.jenkins.plugins.generic.event.data.WorkflowRunData",
    "actions":         [
      {
        "_class": "hudson.model.ParametersAction",
        "parameters": [                {
          "_class": "hudson.model.BooleanParameterValue",
          "name": "skip",
          "value": false
        }]
      },
      {"_class": "org.jenkinsci.plugins.displayurlapi.actions.RunDisplayAction"},
      {"_class": "org.jenkinsci.plugins.pipeline.modeldefinition.actions.RestartDeclarativePipelineAction"},
      {},
      {"_class": "org.jenkinsci.plugins.workflow.job.views.FlowGraphAction"},
      {},
      {},
      {}
    ],
    "artifacts": [],
    "building": true,
    "description": null,
    "displayName": "#1",
    "duration": 0,
    "estimatedDuration": -1,
    "executor": {"_class": "hudson.model.OneOffExecutor"},
    "fullDisplayName": "my-devops-project » example-pipeline #1",
    "id": "1",
    "keepLog": false,
    "number": 1,
    "queueId": 1,
    "result": null,
    "timestamp": 1644126495293,
    "changeSets": [],
    "culprits": [],
    "nextBuild": null,
    "previousBuild": null,
    "_multiBranch": false,
    "_parentFullName": "my-devops-project",
    "_projectName": "example-pipeline"
  },
  "dataType": "org.jenkinsci.plugins.workflow.job.WorkflowRun",
  "id": "eb227ba6-9135-45ff-bec5-a28ccb587bb9",
  "source": "job/my-devops-project/job/example-pipeline/",
  "time": "2022-02-06T13:48:15.333+0800",
  "type": "run.started"
}
```

## Run Completed

```json
{
  "data":     {
    "_class": "io.jenkins.plugins.generic.event.data.WorkflowRunData",
    "actions":         [
      {
        "_class": "hudson.model.ParametersAction",
        "parameters": [                {
          "_class": "hudson.model.BooleanParameterValue",
          "name": "skip",
          "value": false
        }]
      },
      {"_class": "org.jenkinsci.plugins.workflow.libs.LibrariesAction"},
      {},
      {"_class": "org.jenkinsci.plugins.displayurlapi.actions.RunDisplayAction"},
      {"_class": "org.jenkinsci.plugins.pipeline.modeldefinition.actions.RestartDeclarativePipelineAction"},
      {},
      {"_class": "org.jenkinsci.plugins.workflow.job.views.FlowGraphAction"},
      {},
      {},
      {}
    ],
    "artifacts": [],
    "building": false,
    "description": null,
    "displayName": "#1",
    "duration": 1715,
    "estimatedDuration": 1715,
    "executor": {"_class": "hudson.model.OneOffExecutor"},
    "fullDisplayName": "my-devops-project » example-pipeline #1",
    "id": "1",
    "keepLog": false,
    "number": 1,
    "queueId": 1,
    "result": "SUCCESS",
    "timestamp": 1644126495293,
    "changeSets": [],
    "culprits": [],
    "nextBuild": null,
    "previousBuild": null,
    "_multiBranch": false,
    "_parentFullName": "my-devops-project",
    "_projectName": "example-pipeline"
  },
  "dataType": "org.jenkinsci.plugins.workflow.job.WorkflowRun",
  "id": "aaf73c16-574b-4c1b-a740-4494318a59d5",
  "source": "job/my-devops-project/job/example-pipeline/",
  "time": "2022-02-06T13:48:17.022+0800",
  "type": "run.completed"
}
```

## Run Finalized

```json
{
  "data":     {
    "_class": "io.jenkins.plugins.generic.event.data.WorkflowRunData",
    "actions":         [
      {
        "_class": "hudson.model.ParametersAction",
        "parameters": [                {
          "_class": "hudson.model.BooleanParameterValue",
          "name": "skip",
          "value": false
        }]
      },
      {"_class": "org.jenkinsci.plugins.workflow.libs.LibrariesAction"},
      {},
      {"_class": "org.jenkinsci.plugins.displayurlapi.actions.RunDisplayAction"},
      {"_class": "org.jenkinsci.plugins.pipeline.modeldefinition.actions.RestartDeclarativePipelineAction"},
      {},
      {"_class": "org.jenkinsci.plugins.workflow.job.views.FlowGraphAction"},
      {},
      {},
      {}
    ],
    "artifacts": [],
    "building": false,
    "description": null,
    "displayName": "#1",
    "duration": 1715,
    "estimatedDuration": 1715,
    "executor": {"_class": "hudson.model.OneOffExecutor"},
    "fullDisplayName": "my-devops-project » example-pipeline #1",
    "id": "1",
    "keepLog": false,
    "number": 1,
    "queueId": 1,
    "result": "SUCCESS",
    "timestamp": 1644126495293,
    "changeSets": [],
    "culprits": [],
    "nextBuild": null,
    "previousBuild": null,
    "_multiBranch": false,
    "_parentFullName": "my-devops-project",
    "_projectName": "example-pipeline"
  },
  "dataType": "org.jenkinsci.plugins.workflow.job.WorkflowRun",
  "id": "bc11e508-a3ee-4900-8864-2be30e1ffda5",
  "source": "job/my-devops-project/job/example-pipeline/",
  "time": "2022-02-06T13:48:17.055+0800",
  "type": "run.finalized"
}
```

## Run Deleted

```json
{
  "data":     {
    "_class": "io.jenkins.plugins.generic.event.data.WorkflowRunData",
    "actions":         [
      {
        "_class": "hudson.model.ParametersAction",
        "parameters": [                {
          "_class": "hudson.model.BooleanParameterValue",
          "name": "skip",
          "value": false
        }]
      },
      {"_class": "org.jenkinsci.plugins.workflow.libs.LibrariesAction"},
      {},
      {"_class": "org.jenkinsci.plugins.displayurlapi.actions.RunDisplayAction"},
      {"_class": "org.jenkinsci.plugins.pipeline.modeldefinition.actions.RestartDeclarativePipelineAction"},
      {},
      {"_class": "org.jenkinsci.plugins.workflow.job.views.FlowGraphAction"},
      {},
      {},
      {}
    ],
    "artifacts": [],
    "building": false,
    "description": null,
    "displayName": "#1",
    "duration": 1584,
    "estimatedDuration": 1584,
    "executor": null,
    "fullDisplayName": "my-devops-project » example-pipeline #1",
    "id": "1",
    "keepLog": false,
    "number": 1,
    "queueId": 1,
    "result": "SUCCESS",
    "timestamp": 1644126900080,
    "changeSets": [],
    "culprits": [],
    "nextBuild": null,
    "previousBuild": null,
    "_multiBranch": false,
    "_parentFullName": "my-devops-project",
    "_projectName": "example-pipeline"
  },
  "dataType": "org.jenkinsci.plugins.workflow.job.WorkflowRun",
  "id": "af7e9187-c244-46ac-8e4c-15335f38550c",
  "source": "job/my-devops-project/job/example-pipeline/",
  "time": "2022-02-06T13:55:01.792+0800",
  "type": "run.deleted"
}
```
