# ACES Equipment Builder

## Description

This program is a desktop application that interfaces with WebCTRL to generate EIKON scripts using custom .logicsymbol libraries. ACES EB was developed for Windows and has not been tested on Mac or Linux operating systems.

The primary .logicsymbol library should be stored on a shared network drive. ACES EB maintains a local copy of the shared library to ensure the application can be used when offline. Synchronization occurs when the application is launched, if the shared library is accessible.

View general-use documentation at "[install/docs/ACES Equipment Builder.pdf](install/docs/ACES%20Equipment%20Builder.pdf)".

## Compile Instructions

1. Clone this repository to your local machine.

1. Install JDK 16 or later.

1. Install Launch4j 3.14 or later.

1. Edit [Utility.bat](Utility.bat) to set the locations of the JDK bin and the Launch4j executable.

1. Launch [Utility.bat](Utility.bat) and use the following commands:
    - `make`
      - Compiles source code and packs .class files into .jar archive.
    - `jre`
      - Uses jdeps and jlink to create a runtime image for the .jar archive.
    - `wrap`
      - Uses launch4j to create an executable wrapper for the .jar archive.
    - `exec`
      - Launches the .jar archive using the custom runtime image.

## Miscellaneous Information
  - "[install/docs/ACES Equipment Builder.pdf](install/docs/ACES%20Equipment%20Builder.pdf)" is the result of converting [docs.docx](docs.docx) to pdf format.
  - [Icon.ico](Icon.ico) is used by Launch4j to set the icon for the executable wrapper.
  - All files placed under [resources](resources) will be included in the .jar archive.