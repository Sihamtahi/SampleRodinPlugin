# Guide on Developing Rodin plugins

If you want to develop a plugin for the Rodin platform (e.g. a code generation plugin for Event-B), this repository might provide you some useful information/code.

## Prerequisites

As of Apr 2022, when develop plugins for Rodin v3.6, you need to have:
* JDK 11
* Eclipse SDK [v4.18](https://archive.eclipse.org/eclipse/downloads/drops4/R-4.18-202012021800/)
* Rodin development package 
[org.rodinp.dev-3.6.0-77c344946.zip](https://sourceforge.net/projects/rodin-b-sharp/files/Core_Rodin_Platform/3.6/)

## Setup Development Environment

1. Unzip and then open Eclipse SDK v4.18.
   
2. Open the `Preferences` pop-up window by clicking `Window>Preferences`. Then, select `Plug-in Development>Target Platform` in the tree, and click `Add...`.

3. In the Target Definition window that pops up, select: `Nothing: Start with an empty target definition`. 

4. Then click `Next >`. Enter a name for your target platform, for instance `Rodin 3.6`. Then, click the `Add...` button of the `Locations` tab.

5. In the `Add content` window that pops up, select `Software Site`, then click `Next >`.

6. In the new page `Add Software Site` window, click `Add...`, enter a name, like `Rodin 3.6`. Click `Archive...` and navigate to the location of the `org.rodinp.dev-3.6.0-77c344946.zip` you downloaded. Click `Add`.

7. Tick `uncategorized` in the drop list. Click `Finish`. This might take a while to load plugins for setup target platform. Then click `Finish`.

8. When return to the `Plug-in Development>Target platform` window, tick the Rodin platform you have just created to activate it and click `Apply and Close`.

9.  If not clear, see [here](https://wiki.event-b.org/index.php/Using_Rodin_as_Target_Platform) for updates.

## Development Guide

In this repository, you will find a sample Rodin plugin to get start: 

1. Download the source file of this repository, or using `Git` to synchronize with this repository.
2. In Eclipse v4.18, `File > Import > General > Existing Projects into workspace > select archive file`, and navigate to the plugin source file you just downloaded. Then click `Finish`.
3. In the imported project `fr.loria.mosel.rodin.plugin.sample`:
   * plugin meta-information can be accessed via `plugin.xml`, e.g. UI
   * plugin logic starts by `run` method in `DialogAction.Java`

## Plugin Testing Guide

To run/test your plugin in the Rodin platform:

1. In Eclipse SDK v4.18, right click the plugin project, click `Run as > Run configurations`.
   
2. In `create, manage and run configurations` window, double click `Eclipse Application`, and give a name for your configuration in `Name` (e.g. RodinConfig). Then, in the `Program to Run` section, choose `Run a product`, in its dropdown list, select `org.rodinp.platform.product`. Click `Apply`, and then `Run`.

3. A Rodin IDE, that includes your plugin, is then launching. For plugin testing, create Rodin projects and Event-B programs in here to setup your test cases.

For example, our sample plugin prints info of contexts within a project. 
* to setup a test case, create a sample Rodin project as usual, and create a context within it, and create some constants and type them using axioms.
* right click on the project you just created, click `My plugins > Print context`
* A message dialog is then successfully popup, contains the queried information.

