## Progetto ASD - Barbieri Incerti

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).

## Commands for the API

- Command-line compile.  In the connectx/ directory run::

		javac -cp ".." *.java */*.java


CXGame application:

- Human vs Computer.  In the connectx/ directory run:
	
		java -cp ".." connectx.CXGame 6 7 4 connectx.L0.L0


- Computer vs Computer. In the connectx/ directory run:

		java -cp ".." connectx.CXGame 6 7 4 connectx.L0.L0 connectx.L1.L1


CXPlayerTester application:

- Output score only:

	java -cp ".." connectx.CXPlayerTester 6 7 4 connectx.L0.L0 connectx.L1.L1


- Verbose output

	java -cp ".." connectx.CXPlayerTester 6 7 4 connectx.L0.L0 connectx.L1.L1 -v


- Verbose output and customized timeout (1 sec) and number of game repetitions (10 rounds)

	java -cp ".." connectx.CXPlayerTester 6 7 4 connectx.L0.L0 connectx.L1.L1 -v -t 1 -r 10

## vitto gay
