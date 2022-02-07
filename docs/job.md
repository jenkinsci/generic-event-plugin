# Item Event

## Item Create

```json
{
  "data": {
    "_class": "org.jvnet.hudson.test.MockFolder",
    "actions": [
      {},
      {},
      {
        "_class": "com.cloudbees.plugins.credentials.ViewCredentialsAction"
      }
    ],
    "description": null,
    "displayName": "my-devops-project",
    "displayNameOrNull": null,
    "fullDisplayName": "my-devops-project",
    "fullName": "my-devops-project",
    "name": "my-devops-project"
  },
  "dataType": "org.jvnet.hudson.test.MockFolder",
  "id": "10f94a34-587d-43cd-a296-3051f922dc72",
  "source": "",
  "time": "2022-02-06T13:48:15.041+0800",
  "type": "item.created"
}
```

```json
{
    "data":     {
        "_class": "org.jenkinsci.plugins.workflow.job.WorkflowJob",
        "actions":         [
            {},
            {},
            {"_class": "org.jenkinsci.plugins.displayurlapi.actions.JobDisplayAction"},
            {},
            {},
            {"_class": "com.cloudbees.plugins.credentials.ViewCredentialsAction"}
        ],
        "description": null,
        "displayName": "example-pipeline",
        "displayNameOrNull": null,
        "fullDisplayName": "my-devops-project » example-pipeline",
        "fullName": "my-devops-project/example-pipeline",
        "name": "example-pipeline",
        "buildable": true,
        "builds": [],
        "color": "notbuilt",
        "firstBuild": null,
        "healthReport": [],
        "inQueue": false,
        "keepDependencies": false,
        "lastBuild": null,
        "lastCompletedBuild": null,
        "lastFailedBuild": null,
        "lastStableBuild": null,
        "lastSuccessfulBuild": null,
        "lastUnstableBuild": null,
        "lastUnsuccessfulBuild": null,
        "nextBuildNumber": 1,
        "property": [],
        "queueItem": null,
        "concurrentBuild": true,
        "resumeBlocked": false
    },
    "dataType": "org.jenkinsci.plugins.workflow.job.WorkflowJob",
    "id": "ea8b623e-d2b2-45d5-8f1c-65b3ac153e39",
    "source": "job/my-devops-project/",
    "time": "2022-02-06T13:48:15.204+0800",
    "type": "item.created"
}
```

## Item Updated

```json
{
    "data":     {
        "_class": "org.jvnet.hudson.test.MockFolder",
        "actions":         [
            {},
            {},
            {"_class": "com.cloudbees.plugins.credentials.ViewCredentialsAction"}
        ],
        "description": "Fake description",
        "displayName": "my-devops-project",
        "displayNameOrNull": null,
        "fullDisplayName": "my-devops-project",
        "fullName": "my-devops-project",
        "name": "my-devops-project"
    },
    "dataType": "org.jvnet.hudson.test.MockFolder",
    "id": "2934f42b-a6bb-47f4-9f39-7638bbbc967e",
    "source": "",
    "time": "2022-02-07T09:05:13.397+0800",
    "type": "item.updated"
}
```

## Item Deleted

```json
{
    "data":     {
        "_class": "org.jvnet.hudson.test.MockFolder",
        "actions":         [
            {},
            {},
            {"_class": "com.cloudbees.plugins.credentials.ViewCredentialsAction"}
        ],
        "description": "Fake description",
        "displayName": "my-devops-project",
        "displayNameOrNull": null,
        "fullDisplayName": "my-devops-project",
        "fullName": "my-devops-project",
        "name": "my-devops-project"
    },
    "dataType": "org.jvnet.hudson.test.MockFolder",
    "id": "3df06edd-3822-4d8b-b70f-ed13b36a6851",
    "source": "",
    "time": "2022-02-07T09:07:05.993+0800",
    "type": "item.deleted"
}
```
