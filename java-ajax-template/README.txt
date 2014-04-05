A template for projects that use RESTful web services in Java to support thick
client javascript applications.

1. Install Git. Grab this code with this command:

  $ git clone https://github.com/agaber/java-webapp-template.git

2. Install Gradle.

3. Install Eclipse. Set up your project with Eclipse using this command:

  $ gradle eclipse

  Then in Eclipse, File -> Import -> Existing Project Into Workspace
  -> Select root directory -> Finish

4. Compile and test with this command:

  $ gradle build

5. Run test server with this command:

  $ gradle jettyRun


6. Deploy to production:

  $ cp ./build/lib/myapp.war /realserver/deploydir


Alternatively use Maven:

Set up Maven with Eclipse. The very first time you'll need to run this
command so you can have maven set up your Eclipse projects for you:

  $ mvn eclipse:eclipse -Declipse.workspace=$HOME/workspace eclipse:add-maven-repo

Where $HOME/workspace is whatever your Eclipse workspace directory is. You
should only need to do that once.


Alternatively use IntelliJ:

Add "apply plugin: 'idea'" to build.gradle. More than likely you'll be
able to create a project from a gradle file in IntelliJ directly though.
