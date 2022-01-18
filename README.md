# Pipeline Event Plugin

## Introduction

The Pipeline Event Plugin built for Jenkins is mainly to solve the interaction problem between the third-party system and Jenkins.

By using the plugin, the third-party system can receive the events it is interested in more quickly and respond quickly.

At present, our event body mainly refers to CloudEvents, and CloudEvents specification may be supported in the future. We look forward to more people to push this goal.

## Getting Started

You can configure the event receiver in system management:

```txt
Event Dispatcher

Event Receiver
,-----------------------------------------------.
|http://localhost:8000/v1alpha1/webhooks/jenkins|
`-----------------------------------------------'
The receiver that can receive the workflow event.
```

## Events Ducumentation

After the event receiver is configured, we can easily receive events from Jenkins. The following is the detailed document of the corresponding event:

- [WorkflowRun Event](docs/workflowrun.md)
- [WorkflowJob Event](docs/workflowjob.md)

## Contributing

Please refer to [Developer Guide](docs/developer.md).

## LICENSE

Licensed under Apache 2.0, see [LICENSE](LICENSE.md)

