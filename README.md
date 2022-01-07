# ACES Equipment Builder

## Overview

**NOTE** - this repository is currently [unlicensed](https://opensource.stackexchange.com/questions/1720/what-can-i-assume-if-a-publicly-published-project-has-no-license) (may change in the future).

This program is a desktop application that interfaces with *WebCTRL* to generate *EIKON* scripts using custom *.logicsymbol* libraries. *ACES EB* is also capable of maintaining a shared set of favorite *.logicsymbol* and *.logic-script* files. *ACES EB* was developed for *Windows* and has not been tested on *Mac* or *Linux* operating systems.

The primary *.logicsymbol* library should be stored on a shared network drive. *ACES EB* maintains a local copy of the shared library to ensure the application can be used when offline. Synchronization occurs when the application is launched, if the shared library is accessible.

For editing *.logicsymbol* library configuration files, it is recommended to use [*Visual Studio Code*](https://code.visualstudio.com/) with the [*ACES EB* extension](https://github.com/automatic-controls/vscode-aces-equipment-builder). The following documentation is relevant for software developers only. General-use documentation may be found at [*docs/README.md*](docs/README.md).

## Compilation

1. Clone this repository to your local machine.

1. Install [*JDK 16*](https://jdk.java.net/) or later.

1. Install [*Launch4j 3.14*](https://sourceforge.net/projects/launch4j/) or later.

1. Edit [*Utility.bat*](Utility.bat) to set the locations of the *JDK* bin and *Launch4j* executable.

1. Launch [*Utility.bat*](Utility.bat) and use the following commands:
    - `make`
      - Compiles source code and packs *.class* files into *.jar* archive.
      - Takes an optional parameter to recompile only a specific file (omitting the *.java* extension) or subfolder of [*src*](src). For example, `make ACESEquipmentBuilder` compiles only the main class.
    - `jre`
      - Uses *jdeps* and *jlink* to create a runtime image for the *.jar* archive.
    - `wrap`
      - Uses *Launch4j* to create an executable wrapper for the *.jar* archive.
    - `exec`
      - Launches the *.jar* archive using the executable wrapper and custom runtime image.

## Packaging and Installation

### Standard

The standard installation method requires that your clone of this repository is located on a shared network drive. People with access to the shared drive should run [*Installer.bat*](Installer.bat) to install *ACES EB* on their computer. It is recommended that *install/config.txt* contain `RemoteDirectoryPath`, `SupportMessage`, and `EmailTo` entries (refer to [settings](docs/README.md#application-settings)).

### Custom

 After compiling the source code, zip the contents of your [*install*](install) folder. Distribute your *.zip* archive to other people. To install *ACES EB* from the *.zip* archive, unzip and place the contents anywhere. The recommended installation location is "*%LocalAppData%/Programs/ACES Equipment Builder*". To launch *ACES EB*, run the executable "*ACES Equipment Builder.exe*". Feel free to create a desktop shortcut for the executable. If you compile *ACES EB* with a 64bit *JDK*, then it will run only on 64bit platforms. The following files should be included in your *.zip* archive.

- *ACES Equipment Builder.exe*
- *ACES Equipment Builder.jar*
- *README.txt*
- *LICENSE.txt*
- *jre/\*\**

If you want the install to include a *.logicsymbol* library, you should also include the *lib* folder. If you want to specify configuration defaults, you should include *config.txt*. Note that *lib* and *config.txt* will be generated in your [*install*](install) folder when the application is launched using the `exec` command.

## Automated Updates

This section describes how to push *ACES EB* application updates to all users synchronized to a shared library. Library synchronization occurs automatically; this section is only relevant if you want to push out updates to core application files. When a remote directory is selected, *ACES EB* will generate *config.txt* inside the chosen directory. This configuration file contains two entries which may be used to push out source code updates.

- `Version` - The version of *ACES EB* as specified at the top of [*src/ACESEquipmentBuilder.java*](src/ACESEquipmentBuilder.java).
- `UpdateScript` - The update script to launch. May be resolved absolutely or relatively to location of the remote directory.

When an instance of *ACES EB* attempts to synchronize with the remote directory, it checks the configuration file before doing anything else. If the internal version of *ACES EB* is less than the version listed in the configuration file, the application launches the update script and terminates itself. The update script will be passed a parameter specifying the installation location of *ACES EB* on the local machine.

If you are using the [standard installation method](#standard), then it is recommended to set [*Installer.bat*](Installer.bat) as the update script. It is generally expected for the update script to relaunch an instance of *ACES EB* when updates are complete. If `UpdateScript` is unspecified or cannot be resolved, then the application will instead open up the remote directory using *Windows File Explorer*.

If the `Version` string ends with *-dev*, then updates will only occur when the user is in *developer mode*. An optional tag may be included, like *1.0.0-tag* or *1.0.0-tag-dev*. The application stores the current tag in a local configuration entry: `VersionTag`. If the tag specified in the `Version` string does not match the local `VersionTag`, then the update script is invoked.

## Miscellaneous Information
  - [*icon.ico*](icon.ico) is used by *Launch4j* to set the icon for the executable wrapper, "*install/ACES Equipment Builder.exe*".
  - All files in [*resources*](resources) are included in the *.jar* archive, "*install/ACES Equipment Builder.jar*".

## Changes Under Consideration

- Move this section into *CHANGELOG.md*
- Add comments to the source code.
- Refactor code to improve readability and elegance.
- Make parsing logic more robust (e.g, basic arithmetic).
- Use `java.nio` for all *I/O* operations.
- Replace custom dynamic array logic with `ArrayList`.
- Replace &#119978;*(n)* search algorithms and structures with &#119978;*(log n)*.
- Use subscription-update model wherever possible (in particular, overhaul processing mechanisms for *If-Then* statements and display names).
- Implement *read_lock* and *write_lock* files for controlling access to the remote directory.
- Separate the contents of [*ACESEquipmentBuilder.java*](src/ACESEquipmentBuilder.java) into many classes.
- Add configuration option to toggle dark mode (requires duplicating the icons in [*resources*](resources)).
- Compress *PNG* images in [*docs*](docs) to save space.
- Add *Else* section to *If-Then* statements.
- Add wildcard selection at the end of reference paths in *Then* blocks.
