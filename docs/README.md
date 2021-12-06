# ACES Equipment Builder

## Overview

This program is a desktop application that interfaces with *WebCTRL* to generate *EIKON* scripts using custom *.logicsymbol* libraries. *ACES EB* is also capable of maintaining a shared set of favorite *.logicsymbol* and *.logic-script* files. *ACES EB* was developed for *Windows* and has not been tested on *Mac* or *Linux* operating systems.

The primary *.logicsymbol* library should be stored on a shared network drive. *ACES EB* maintains a local copy of the shared library to ensure the application can be used when offline. Synchronization occurs when the application is launched, if the shared library is accessible.

If you are a software developer who is interested in contributing to *ACES EB*, see the [developer documentation](../README.md). If you find a bug in the application, you can email <cvogt@automaticcontrols.net> or [open an issue](https://github.com/automatic-controls/aces-equipment-builder/issues). If an error occurs, please attach the relevant section of your log file. Use **CTRL+L** to open your log file from *ACES EB*.

Anytime you see text enclosed by *%* in this document, assume the enclosed text refers to an environment variable of your computer. For example, *%Username%* expands to the username of the currently logged in user. To see what an environment variable expands to, type `echo %VARIABLE%` in *command prompt*.

## Installation

- There are two installation methods depending on how your network administrators setup *ACES EB*.

  - If you have been directed to use a script installer (e.g, *Installer.bat*) located on a shared network drive, running the installer will take care of everything for you. A shortcut to the *ACES EB* executable will be created on your desktop.

  - If you downloaded the latest release ([direct download link](<!-- TODO -->)), or were given a *.zip* archive, the first step is to unzip the archive and place the contents in an empty folder. The recommended folder location is "*%LocalAppData%/Programs/ACES Equipment Builder*". For convenience, place a shortcut to "*ACES Equipment Builder.exe*" on your desktop.

- You may be required to specify where the remote synchronization directory is located when you launch *ACES EB* for the first time. Usually, this will be a folder on a shared network drive.

  - If you want to use *ACES EB* without remote synchronization, you should leave this entry blank. Note that some developer tools are disabled when the remote directory is unspecified.

  - Typically, the remote directory location is not changed after the initial setup, but you can use **CTRL+O** while in [*developer mode*](#keyboard-shortcuts) if necessary.

- You may be required to specify your *WebCTRL* installation directory. It is recommended to close all *EIKON* instances before binding to *WebCTRL*. You can rebind to another installation of *WebCTRL* at any time using [keyboard shortcut](#keyboard-shortcuts) **CTRL+K** or **CTRL+ALT+K**.

## Uninstallation

1. Determine the location of the installation directory.
   - Navigate to the origin of your *ACES EB* desktop shortcut.
2. Delete the *ACES EB* installation directory.
3. Delete the *ACES EB* desktop shortcut.

## Basic Operation

1. Launch *ACES EB* and make selections to customize your equipment.
   - If you click on an item and its icon becomes highlighted in orange, you can use the arrow-keys to change the numeric value associated to the selected item.
2. When everything is ready, click the "*Generate Script*" button.
   - An error message will be generated if you haven't made appropriate selections.
3. Launch *EIKON*.
   - **CTRL+E** launches an *EIKON* instance corresponding to the currently bound *WebCTRL* installation.
4. Navigate to " *Tools &#10148; Scripts &#10148; Generated Script &#10148; Execute* " within *EIKON*.
   - Wait for the window to display "*Execution Completed Succesfully*" before exiting the script editor.

## Keyboard Shortcuts

| Shortcut | Description |
| - | - |
| **F3** | Reloads local library files into the application. |
| **F5** | Synchronizes to the remote directory and reloads. |
| **CTRL+E** | Open an instance of *EIKON* using the previously bound *WebCTRL* installation. |
| **CTRL+O** | Opens a window to configure various [settings](#application-settings). More options are available when in developer mode. |
| **CTRL+L** | Opens the log file using the default text editor. |
| **CTRL+K** | Rebinds to another installation of *WebCTRL*. If there is only one version installed, it will be automatically selected. The application looks for folders in *%SystemDrive%* which match the regular expression *`WebCTRL\d+\.\d+`* |
| **CTRL+ALT+K** | Rebinds to another installation of *WebCTRL*. The application will not automatically detect installation directories. You must manually navigate to the appropriate folder. |
| **CTRL+D+E+V** | Toggles *developer mode*. |
| **CTRL+F** | Requires *developer mode*. Initiates a global search through all configuration files in the remote library. Refer to [*Oracle's* Pattern documentation](https://docs.oracle.com/en/java/javase/16/docs/api/java.base/java/util/regex/Pattern.html) for details regarding regular expression syntax. |
| **DELETE** | Requires *developer mode*. Deletes the item the mouse is hovering over in the remote library after prompting for confirmation. |

## Right-Click Context Menu

When *developer mode* is not active, the following menu shows up when you right-click anywhere. When *developer mode* is active, this menu shows up only when you right-click at a location not corresponding to an item.

| Symbol | Name | Description |
|  - | - | - |
| ![](../resources/sync.png) | *Synchronize Library* | Equivalent to **F5**. |
| ![](../resources/refresh.png) | *Reload Library* | Equivalent to **F3**. |
| ![](../resources/eikon.png) | *Launch EIKON* | Equivalent to **CTRL+E**. |
| ![](../resources/edit.png) | *Edit Configuration Options* | Equivalent to **CTRL+O**. |
| ![](../resources/bind.png) | *Rebind WebCTRL* | Equivalent to **CTRL+K**. |
| ![](../resources/open.png) | *Open Log File* | Equivalent to **CTRL+L**. |
| ![](../resources/findall.png) | *Global Find/Replace* | Requires *developer mode*. Equivalent to **CTRL+F**. |
| ![](../resources/edit.png) | *Configure* | Requires *developer mode*. Opens the configuration file corresponding to the root library folder using the default editor. You should [setup VSCode](#visual-studio-code) before using this shortcut. If a configuration file does not already exist, one will be created after prompting for confirmation. |

<br>The following menu shows up when you are in *developer mode* and right-click on an item.

| Symbol | Name | Description |
|  - | - | - |
| ![](../resources/open.png) | *Open* | Opens the item you clicked on in the remote library. If the item corresponds to a *.logicsymbol* file, it will open in *EIKON*. If the item corresponds to a directory, it will open in *Windows File Explorer*. |
| ![](../resources/edit.png) | *Configure* | Opens the configuration file corresponding to the item you clicked on in the remote library using the default editor. You should [setup VSCode](#visual-studio-code) before using this shortcut. If a configuration file does not already exist, one will be created after prompting for confirmation. |
| ![](../resources/delete.png) | *Delete* | Deletes the item you clicked on in the remote library. |
| ![](../resources/findall.png) | *Find/Replace Within* | Similar to **CTRL+F** except the scope is restricted to everything below the item you clicked on. |
| ![](../resources/search.png) | *Find All References* | Searches the library to find all items which have been initialized using a [reference](<!--TODO put link here-->) to the item you clicked on. It is good practice to check which sections of a library rely on an item before making modifications. |
| ![](../resources/search.png) | *Find Direct References* | Similar to *Find All References* except that implicit references are not tracked. For example, suppose **A** &#8594; **B** &#8594; **C** &#8594; **D**, meaning **A** refers to **B**, **B** refers to **C**, and **C** refers to **D**. Under these assumptions, *FindAllReferences(**D**) = {**A**, **B**, **C**}* and *FindDirectReferences(**D**) = {**C**}*. |

## Application Settings

Only the first four settings are visible when *developer mode* is not active. The last two settings are never visible from within the application. They can be modified by editing the application's configuration file (*./config.txt* relative to the installation directory). Use **F3** to reload the configuration file if you make changes.

| Setting | Description |
| - | - |
| *Auto Synchronize* | Specifies whether to synchronize whenever the application is launched. If this value is `false`, then synchronization must be initiated manually using **F5**. |
| *Synchronize Library* | Whenever synchonization occurs, this specifies whether the application should sync *.logicsymbol* library files. |
| *Synchronize Favorites* | Whenever synchronization occurs, this specifies whether the application should sync favorite *.logicsymbol* files. |
| *Synchronize Scripts* | Whenever synchronization occurs, this specifies whether the application should sync favorite *.logic-script* files. |
| *Allow Hidden Entries* | Specifies whether to allow hidden entries in the *.logicsymbol* library. |
| *Allow Locked Entries* | Specifies whether to allow locked entries in the *.logicsymbol* library. |
| *Allow Default Selections* | Specifies whether to allow default selections in the *.logicsymbol* library. |
| *Allow Group Minimums* | Specifies whether to allow *Group* minimums in the *.logicsymbol* library. |
| *Allow Group Maximums* | Specifies whether to allow *Group* maximums in the *.logicsymbol* library. |
| *Allow If-Then Statements* | Specifies whether to allow *If-Then* statements in the *.logicsymbol* library. |
| *Remote Directory Path* | Specifies the folder to synchronize against. This folder is typically located on a shared network drive. |
| `WebCTRLPath` | The path to the *WebCTRL* installation directory (e.g, *C:\\WebCTRL8.0*). This value is modified using **CTRL+K** or **CTRL+ALT+K**. |
| `SupportMessage` | If any error occurs, this message will be included in the pop-up shown to the user. |
| `EmailTo` | A semicolon-delimited list of email addresses (e.g, *abc&#65279;@gmail.com;def&#65279;@gmail.com*) to show when the "*Contact Engineering*" button is pressed. |

## Remote Directory Settings

When you select a remote synchronization directory, the application will automatically generate *config.txt* in the selected folder. The following table describes the settings which can be found in *config.txt*. For additional details, refer to [developer documentation](../README.md#automated-updates).
| Setting | Description |
| - | - |
| `Version` | The minimum application version compatible with the associated *.logicsymbol* library. This setting may be used to push application updates out to all connected users. Upon synchronization, each application will attempt to update if outdated. |
| `UpdateScript` | The script to run when attempting to update. May be resolved absolutely or relatively to the remote directory. |
| `AllowSync` |  |
| `Library` |  |
| `Favorites` |  |
| `Scripts` |  |

## Library Configuration Essentials

## *Visual Studio Code*

*VSCode* is the recommended text editor for configuring *.logicsymbol* libraries.

1. Install [*Visual Studio Code*](https://code.visualstudio.com/) version 1.62 or later.

1. Install the [*ACES EB* extension](https://github.com/automatic-controls/vscode-aces-equipment-builder) for *VSCode*. This extension provides language support for *ACES EB* configuration files.

   - Download the latest *.vsix* extension release file ([direct download link](https://github.com/automatic-controls/vscode-aces-equipment-builder/releases/download/v1.0.0/aces-eb-language-support-1.0.0.vsix)), and then [install](https://code.visualstudio.com/docs/editor/extension-marketplace#_install-from-a-vsix) the extension in *VSCode*.

     ![](vscode_vsix_install.png)

1. Set *VSCode* as the default application to open *.aceseb* configuration files.

   - *Windows File Explorer &#10148; Right-click .aceseb file &#10148; Open with &#10148; Choose another app &#10148;*

     ![](vscode_file_assoc.png)

