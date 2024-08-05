# CSV Parser Web Application
- A Full-Stack Web Application with React on the frontend and Spark Java API on the backend
## Project Details

Project Name: "REPL"

Team members & Contributions:

Bryanna (`bpajotte`):
- Reactified Echo Code
- Styling
- Handling commands
- Packaging/Organization

Effy  (`ttran59`)
- Mock testing
- Accessibility features
- Adding and removing commands
- Handling commands

We spent roughly 12 hours on this assignment

Repo: https://github.com/cs0320-s2023/sprint-4-bpajotte-ttran59

## Design Choices  
Components:

Our program has 4 different components: `Header`, `HistoryBox`, `InputBox`, and `Table`

- The `Header` component contains the title of our program.
- The `HistoryBox` component handles the display of past commands.
- The `InputBox` component handles the functionality of user inputs to the REPL. This includes parsing the input, and handling of the various commands provided by the REPL.
- The `Table` component is a component that turns input into an HTML table. This component was inspired by the Table component in https://github.com/cs0320-s2023/sprint-4-mmagavi-srosa5.

Functions:

We included 3 functions in our program: `load`, `search`, and `view`. These are all of type `REPLFunction`.
- The `load` function handles the "load_file" command by making a call to the API loadcsv endpoint with the provided user inputs.
- The `view` function handles the "view" command by making a call to the API viewcsv endpoint with the provided user inputs.
- The `search` function handles the "search" command by making a call to the API searchcsv endpoint with the provided user inputs.

Interfaces:

We have 7 interfaces: `HistoryCommand`, `InputBoxProps`, `LoadResponse`, `REPLFunction`, `SearchResponse`, `TableProps`, and `ViewResponse`

- The `InputBoxProps` and `TableProps` interfaces serve as containers for the arguments that need to be passed to the `InputBox` and `Table` components, respectively.
- The `LoadResponse`, `ViewResponse`, and `SearchResponse` interfaces act as classes to hold the responses from the api. They include fields necessary for our REPL to appropriately display the result of the user commands.
- The `HistoryCommand` interface defines an internal representation of a user command. This is the type of our history state list, which keeps track of all the commands inputted by the user.  
- The `REPLFunction` interface defines the type of our functions that handle commands. Each command takes in a list of arguments and outputs a Promise<ReactNode> (either a string or Table). 

## Bugs
None that we know of

## Tests 
- Tests utilize DOM and ReactTestingLibrary 
- See main.test.tsx to see a documented Unit Test & Integration Test.
- Integration testing utilizes mocked API responses and mock API calls for load, view, and search.

## How to...
Run tests: 
- Navigate to the frontend directory and run `npm test`

Run the REPL:
1.  Navigate to the server package in the backend directory and run Server.java. This should print a message to the terminal that says "Server started." If you do not see this message, try changing the spark port and running again (the default is 3005)! *Note: if you do change the spark port, you will also have to change it in the load, search, and view functions*
2. Navigate to the frontend directory, run `npm install` and then `npm run dev` in the terminal 
3. Go to the link of the terminal response in your browser.
4. Use the REPL! <3
