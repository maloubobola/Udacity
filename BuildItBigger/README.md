Server (Google Cloud Endpoint) must be started before using the application.

The custom gradle task is called `runAndroidTests`. Previous GCE must be stopped before using it (because port 8080 will be already used).